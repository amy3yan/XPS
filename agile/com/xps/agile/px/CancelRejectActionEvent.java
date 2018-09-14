/*******************************************************************************
 * @project: XPS
 * @package: com.xps.agile.px
 * @file: CancelRejectActionEvent.java
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

import com.agile.api.APIException;
import com.agile.api.IAgileSession;
import com.agile.api.INode;
import com.agile.px.ActionResult;
import com.agile.px.EventActionResult;
import com.agile.px.EventConstants;
import com.agile.px.IEventAction;
import com.agile.px.IEventInfo;
import com.agile.px.ISignOffEventInfo;

/**
 * @author amy
 *
 */
public class CancelRejectActionEvent implements IEventAction {

    /**
     * 
     */
    public CancelRejectActionEvent() {
        // TODO Auto-generated constructor stub
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
            session.disableAllWarnings();
            ActionResult actionResult = new ActionResult(ActionResult.NORESULT, "执行批准操作。");
            System.out.println("当前操作： " + eventinfo.getEventType() + " == " + EventConstants.EVENT_REJECT_FOR_WORKFLOW);
            if(eventinfo.getEventType() == EventConstants.EVENT_REJECT_FOR_WORKFLOW) {
                actionResult = new ActionResult(ActionResult.EXCEPTION, new Exception("‘拒绝’操作已禁用， 请使用‘批准’操作提交流程。"));
            }
            result = new EventActionResult(eventinfo, actionResult);
        }catch(Exception e) {
            e.printStackTrace(System.out);
            result = new EventActionResult(eventinfo, new ActionResult(ActionResult.EXCEPTION, e));
        }finally {
            try {
                session.enableAllWarnings();
            } catch (APIException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
