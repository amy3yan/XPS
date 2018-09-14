/*******************************************************************************
 * @project: XPS
 * @package: com.xps.agile.utils
 * @file: ChangeUtils.java
 * @author: amy
 * @created: 2018.9
 * @purpose:
 * 
 * @version: 1.0
 * 
 * 
 * Copyright 2018 AMY All rights reserved.
 ******************************************************************************/
package com.xps.agile.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.agile.api.APIException;
import com.agile.api.IAgileList;
import com.agile.api.ICell;
import com.agile.api.IChange;
import com.agile.api.IDataObject;
import com.agile.api.ISignoffReviewer;
import com.agile.api.IStatus;
import com.agile.api.IWorkflow;
import com.agile.api.WorkflowConstants;

/**
 * @author amy
 *
 */
public class ChangeUtils {

    /**
     * 为Change添加fromCell所选的人或用户组作为审批者
     * @param change
     * @param status
     * @param fromcell
     * @throws APIException
     */
    public static void addApproversFromCell(IChange change, IStatus status, ICell fromcell) throws APIException {
        if(fromcell == null) return;
        List<Object> approvers = new ArrayList<Object>();
        IAgileList list = (IAgileList)fromcell.getValue();
        for(IAgileList selected : list.getSelection()) {
            approvers.add(selected.getValue());
        }
        change.addReviewers(status, approvers, Collections.EMPTY_LIST, Collections.EMPTY_LIST, false, "");
    }
    
    /**
     * 获取Change指定状态status的审批者/观察者/确认者
     * @param change
     * @param status
     * @param reviewerType
     * @return
     * @throws APIException
     */
    public static List<IDataObject> getReviewers(IChange change, IStatus status, Integer reviewerType) throws APIException{
        List<IDataObject> reviewers = new ArrayList<IDataObject>();
        ISignoffReviewer[] signoffReviewers = change.getAllReviewers(status, reviewerType);
        for(ISignoffReviewer reviewer : signoffReviewers) {
            reviewers.add(reviewer.getReviewer());
        }
        return reviewers;
    }

    /**
     * 根据节点状态名称获取节点
     * @param change
     * @param statusName
     * @return
     * @throws APIException
     */
    public static IStatus getStatusByName(IChange change, String statusName) throws APIException {
        IStatus status = null;
        IWorkflow wf = change.getWorkflow();
        IStatus[] statuses = wf.getStates();
        for(IStatus istatus : statuses) {
            if(statusName.equals(istatus.getName())) {
                status = istatus;
                break;
            }
        }
        return status;
    }
    
    /**
     * @desc: 变更流程状态，同时可增加新的Reviewer（不必考虑是否重复）
     *
     * @param change
     * @param newStatus
     * @param comment
     * @param notifyList
     * @param addApprovers
     * @param addObservers
     * @param addAcknowledgers
     * @throws Exception
     */
    public static void changeStatus(IChange change, IStatus newStatus, String comment,
                Collection<IDataObject> addApprovers, Collection<IDataObject> addObservers , Collection<IDataObject> addAcknowledgers, 
                Collection<IDataObject> notifyList ) throws Exception{
        List<IDataObject> approvers = getReviewers(change, newStatus, WorkflowConstants.USER_APPROVER);
        List<IDataObject> observers = getReviewers(change, newStatus, WorkflowConstants.USER_OBSERVER);
        List<IDataObject> acknowledgers = getReviewers(change, newStatus, WorkflowConstants.USER_ACKNOWLEDGER);
        approvers.addAll(addApprovers);
        observers.addAll(addObservers);
        acknowledgers.addAll(addAcknowledgers);
        change.changeStatus(newStatus, false, comment, true, true, notifyList, approvers, observers, acknowledgers, false);
    }
}
