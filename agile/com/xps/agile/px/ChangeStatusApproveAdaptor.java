/*******************************************************************************
 * @project: XPS
 * @package: com.xps.agile.px
 * @file: ChangeStatusApproveAdaptor.java
 * @author: amy
 * @created: 2018.9
 * @purpose:
 * 
 * @version: 1.0
 * 
 * 
 * Copyright 2018 AMY All rights reserved.
 ******************************************************************************/
package com.xps.agile.px;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.agile.api.APIException;
import com.agile.api.ChangeConstants;
import com.agile.api.ExceptionConstants;
import com.agile.api.IAgileSession;
import com.agile.api.ICell;
import com.agile.api.IChange;
import com.agile.api.IDataObject;
import com.agile.api.INode;
import com.agile.api.IStatus;
import com.agile.api.WorkflowConstants;
import com.agile.px.ActionResult;
import com.agile.px.EventActionResult;
import com.agile.px.IEventAction;
import com.agile.px.IEventInfo;
import com.agile.px.ISignOffEventInfo;
import com.xps.agile.A9ListConfig;
import com.xps.agile.SystemConfigAdaptor;
import com.xps.agile.utils.CellUtils;
import com.xps.agile.utils.ChangeUtils;

/**
 * @author amy
 *
 */
public class ChangeStatusApproveAdaptor implements IEventAction {

    private static final String NODE_NAME = "PXChangeStatusApprove";
    private static final String REGEX_FLAGS = "[!\\*]";
    private static final String HYPHEN = "@@";
    private static Logger log = Logger.getLogger(ChangeStatusApproveAdaptor.class);
    private A9ListConfig statusConfig;
    private Map<String, A9ListConfig> control;
    private AuditChangeRequiredFieldsEvent auditAction;
    
    /**
     * 
     */
    public ChangeStatusApproveAdaptor() {
        // TODO Auto-generated constructor stub
        control = new HashMap<String, A9ListConfig>();
        auditAction = new AuditChangeRequiredFieldsEvent();
    }

    /* (non-Javadoc)
     * @see com.agile.px.IEventAction#doAction(com.agile.api.IAgileSession, com.agile.api.INode, com.agile.px.IEventInfo)
     */
    @Override
    public EventActionResult doAction(IAgileSession session, INode arg1,
            IEventInfo arg2) {
        EventActionResult result = null;
        ISignOffEventInfo eventinfo = (ISignOffEventInfo)arg2;
        try {
//            session.disableAllWarnings();
            session.enableAllWarnings();
            session.disableWarning(ExceptionConstants.APDM_UNRELEASELOSEATTACHREDLINE_WARNING); 
            session.disableWarning(ExceptionConstants.APDM_BASECHANGEUNRELEASED_WARNING); //AML redline warning
            session.disableWarning(ExceptionConstants.APDM_BASEECOUNRELEASED_WARNING); // BOM redline warning
            session.disableWarning(ExceptionConstants.APDM_UNRELEASENOTALLOWED_WARNING); // Attachment redline warning
            session.disableWarning(ExceptionConstants.APDM_UNRELEASEDCHILD_WARNING);// child in bom
            
            IChange change = (IChange)eventinfo.getDataObject();
            result = new EventActionResult(eventinfo, doChangeStatus(session, change));
        } catch (Exception e) {
            result = new EventActionResult(eventinfo, new ActionResult(ActionResult.EXCEPTION, e));
            e.printStackTrace(System.out);
        } finally {
            try {
                session.enableAllWarnings();
            } catch (APIException e) {
                e.printStackTrace(System.out);
            }
        }
        return result;
    }
    
    /**
     * 根据配置执行流程跳转
     * @param session
     * @param change
     * @return
     * @throws NumberFormatException
     * @throws APIException
     */
    public ActionResult doChangeStatus(IAgileSession session, IChange change) throws NumberFormatException, APIException {
        ActionResult result =  new ActionResult(ActionResult.NORESULT, "IGNOREABLE RECORDs.");
        
        A9ListConfig config = getCurrentChangeStatusConfig(session, change);
        if(config == null) return result; // 无配置条件
        
        // 当前节点配置属性值有对应的配置条件
        String toStatusName = config.getValue();
        IStatus nextStatus = ChangeUtils.getStatusByName(change, toStatusName);
        System.out.println(change.getName() + " 跳转目标节点: " +toStatusName +" ( "+ (nextStatus !=null) + " )");
        if(nextStatus == null) return result;// 跳转状态不存在
        auditAction.doAudit(change);
        if(!auditAction.isAllApproved()) return new ActionResult(ActionResult.NORESULT, "不是所有审批者都已响应");
        
        // 当前配置跳转的状态存在 -- 执行跳转
        List<IDataObject> approvers = ChangeUtils.getReviewers(change, nextStatus, WorkflowConstants.USER_APPROVER);
        List<IDataObject> observers = ChangeUtils.getReviewers(change, nextStatus, WorkflowConstants.USER_OBSERVER);
        List<IDataObject> acknowledgers = ChangeUtils.getReviewers(change, nextStatus, WorkflowConstants.USER_ACKNOWLEDGER);
        System.out.println(change.getName() + "  当前节点状态: "+change.getStatus());
        change.changeStatus(nextStatus, false, "", false, false, Collections.EMPTY_LIST, approvers, observers, acknowledgers, false);
        // 跳转完成后(清空属性的值)
        String reset = resetValue(change) ? ", 执行RESET成功." : "";
        result = new ActionResult(ActionResult.STRING, "根据设定条件成功跳转到 "+nextStatus.getName() + reset);
        return result;
    }
    
    /**
     * 得到流程当前状态下 满足属性值的流程跳转条件数据
     * @param session
     * @param change
     * @return
     * @throws APIException
     */
    public A9ListConfig getCurrentChangeStatusConfig(IAgileSession session, IChange change) throws APIException {
     // 获取配置信息
        ICell cell = change.getCell(ChangeConstants.ATT_COVER_PAGE_CHANGE_TYPE);
        String changeType = CellUtils.getCellStringValue(cell);
        IStatus currentStatus = change.getStatus();
        String[] nodePath = {NODE_NAME, changeType, currentStatus.getName()};
        statusConfig = SystemConfigAdaptor.getCaseConfig(session.getAdminInstance(), nodePath);
        System.out.println(NODE_NAME+"/"+changeType + "/" + currentStatus.getName() + " ==> " + statusConfig);
        if(statusConfig == null) return null;
        
        String[] baseIds = statusConfig.getValue().split(HYPHEN);
        StringBuffer key = new StringBuffer();
        for(int i = 0; i < baseIds.length -1; i++){
            cell = change.getCell(Integer.valueOf(baseIds[i]));
            key.append(CellUtils.getCellStringValue(cell)).append(HYPHEN);
        }
        cell = change.getCell(Integer.valueOf(baseIds[baseIds.length -1]));
        key.append(CellUtils.getCellStringValue(cell));
        
        control = SystemConfigAdaptor.getCaseConfigs(session.getAdminInstance(), nodePath);
        A9ListConfig config = control.get(key.toString());
        System.out.println(change.getName() + " >>>>>>>>>>> DIRECT KEY >>>>>>>>>>>> " + key + " -->" + config);
        // 通配符解析
        if(config == null && statusConfig.getCaseFlag().endsWith("REGEX")) { // 该状态包含通配符
            System.out.println(change.getName() + " *************** REGEX KEY **************** " + key );
            config = getCurrentChangeStatusRegexConfig(key.toString());
        }
        
        return config;
    }
    
    private A9ListConfig getCurrentChangeStatusRegexConfig(String value) {
        A9ListConfig config = null;
        value = value.endsWith(HYPHEN) ? value+"_NULL_" : value;
        String[] values = value.split(HYPHEN);
        for(Map.Entry<String, A9ListConfig> entry : control.entrySet()) {
            A9ListConfig listconfig = entry.getValue();
            Pattern pattern = Pattern.compile(REGEX_FLAGS);
            if(!pattern.matcher(listconfig.getFeature()).find()) continue; // 配置的值中是否包含通配符
            String[] kvalues = listconfig.getFeature().split(HYPHEN);
            boolean match = true;
            for(int i=0; i<kvalues.length; i++) {
                if(!pattern.matcher(kvalues[i]).find()) {
                    match = match && values[i].equals(kvalues[i]);
                }else {
                    if(kvalues[i].contains("*")) {
                        match = match && true;
                    }else if(kvalues[i].contains("!")) {
                        match = match && !kvalues[i].contains(values[i]);
                    }else {
                        // 其他通配符
                    }
                }
                if(!match) break;
            }
            if(match) {
                config = listconfig;
                System.out.println(value +  " >>>>>>>>>>> REGEX KEY >>>>>>>>>>>> " + listconfig.getFeature() + " -->" + config);
                break;
            }
        }
        return config;
    }
    
    
    private boolean resetValue(IChange change) {
        String apiname = statusConfig.getCaseFlag();
        if(!apiname.startsWith("RESET")) return false;
        try {
            String[] baseIds = statusConfig.getValue().split(HYPHEN);
            for(int i = 0; i < baseIds.length; i++){
                ICell cell = change.getCell(Integer.valueOf(baseIds[i]));
                CellUtils.setListCellValue(cell, new Object[] {});
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return false;
        }
    }
    
}
