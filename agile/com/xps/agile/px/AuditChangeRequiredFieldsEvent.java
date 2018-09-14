/*******************************************************************************
 * @project: XPS
 * @package: com.xps.agile.px
 * @file: AuditChangeRequiredFieldsEvent.java
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.agile.api.APIException;
import com.agile.api.ExceptionConstants;
import com.agile.api.IAgileSession;
import com.agile.api.IChange;
import com.agile.api.INode;
import com.agile.api.IStatus;
import com.agile.api.StatusConstants;
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
public class AuditChangeRequiredFieldsEvent implements IEventAction {

    private static final String IGNORE_EXCEPTION_MESSAGE_ZH = "无任何错误或警告。";
    private static final String IGNORE_EXCEPTION_MESSAGE_EN = "No errors or warnings.";
    private static Logger log = Logger.getLogger(AuditChangeRequiredFieldsEvent.class);
    private boolean allApproved;
    /**
     * 
     */
    public AuditChangeRequiredFieldsEvent() {
        // TODO Auto-generated constructor stub
        allApproved = true;
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
            session.enableAllWarnings();
            IChange change = (IChange)eventinfo.getDataObject();
            result = new EventActionResult(eventinfo, doAudit(change));
        } catch (APIException e) {
            e.printStackTrace(System.out);
            result = new EventActionResult(eventinfo, new ActionResult(ActionResult.EXCEPTION, e));
        }finally {
            try {
                session.enableAllWarnings();
            } catch (APIException e) {
                e.printStackTrace(System.out);
            }
        }
        return result;
    }

    public ActionResult doAudit(IChange change) throws APIException {
        ActionResult result = null;
        IStatus status = change.getStatus();
        System.out.println(status.getName() + " IS RELEASED STATUS?  >>>>>>>>>>>>>>>> "+(status.getStatusType() == StatusConstants.TYPE_RELEASED));
//        Map auditResult = change.audit(status.getStatusType() != StatusConstants.TYPE_RELEASED);
        Map auditResult = change.audit(false);
        Collection auditResultValues = auditResult.values();
//        System.out.println(" AuditResult ---------SIZE-------->  "+auditResultValues.size());
        if(auditResultValues.isEmpty()) return new ActionResult(ActionResult.STRING, "Passed audit without any error or warnings."); 
        Iterator it = auditResultValues.iterator();
        StringBuffer messages = new StringBuffer();
        while(it.hasNext()) {
            Object value = it.next();
            ArrayList exceptions = (ArrayList)value;
            for(Object obj : exceptions) {
                APIException exception = (APIException) obj;
                if(isValidAudit(exception)) {
                    messages.append("<p>").append(exception.getMessage()).append(";</p>");
//                    messages.append("<a id=​\"auditerrors_0\" href=​\"javascript:​openObjectInParentWindow('auditerrors_0','1','ChangeHandler', '7000','6038515','1544','0','6038515','', '0', '', '','1')​;​\">​").append(exception.getMessage()).append("</a>");
                }
            }
        }
        log.debug(messages);
        if(messages.length() > 0) {
            result = new ActionResult(ActionResult.EXCEPTION, new Exception(messages.toString()));
//            result = new ActionResult(ActionResult.EXCEPTION, messages.toString());
        }else {
            result = new ActionResult(ActionResult.STRING, "必填字段校验通过.");
        }
        return result;
    }
    
    private boolean isValidAudit(APIException e) {
        String message = e.getMessage();
        log.debug("isValidAudit mes==============>"+message + " : " + e.getErrorCode() +" : "+ExceptionConstants.API_SEE_ROOT_CAUSE.equals(e.getErrorCode()));
        if(ExceptionConstants.APDM_NOTALLAPPROVERSRESPOND_WARNING.equals(e.getErrorCode())) {
            this.allApproved = false;
            System.out.println(">>>>>>>>> NOT ALL APPROVED <<<<<<<<<<<<");
        }
        return message != null
                && ExceptionConstants.API_SEE_ROOT_CAUSE.equals(e.getErrorCode())
                && !message.contains(IGNORE_EXCEPTION_MESSAGE_ZH)
                && !message.contains(IGNORE_EXCEPTION_MESSAGE_EN);
            
    }

    /**
     * @return the allApproved
     */
    public boolean isAllApproved() {
        return allApproved;
    }
    
}
