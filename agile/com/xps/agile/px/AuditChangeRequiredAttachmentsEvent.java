/*******************************************************************************
 * @project: XPS
 * @package: com.xps.agile.px
 * @file: AuditChangeRequiredAttachmentsEvent.java
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

import java.util.Map;

import org.apache.log4j.Logger;

import com.agile.api.APIException;
import com.agile.api.ChangeConstants;
import com.agile.api.IAgileSession;
import com.agile.api.ICell;
import com.agile.api.IChange;
import com.agile.api.INode;
import com.agile.api.IRow;
import com.agile.api.IStatus;
import com.agile.api.ITable;
import com.agile.api.ITwoWayIterator;
import com.agile.px.ActionResult;
import com.agile.px.EventActionResult;
import com.agile.px.IEventAction;
import com.agile.px.IEventInfo;
import com.agile.px.ISignOffEventInfo;
import com.xps.agile.A9ListConfig;
import com.xps.agile.SystemConfigAdaptor;
import com.xps.agile.utils.CellUtils;

/**
 * 
 *
 * @author Amy
 */
public class AuditChangeRequiredAttachmentsEvent implements IEventAction {

    private static Logger log = Logger.getLogger(AuditChangeRequiredAttachmentsEvent.class);
    private static final String NODE_NAME = "RequiredFileInWorkflow";
    
    
    private A9ListConfig changeConfig;
    private Map<String, A9ListConfig> control;
    
    /**
     * 
     */
    public AuditChangeRequiredAttachmentsEvent() {
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see com.agile.px.IEventAction#doAction(com.agile.api.IAgileSession, com.agile.api.INode, com.agile.px.IEventInfo)
     */
    @Override
    public EventActionResult doAction(IAgileSession session, INode arg1,
            IEventInfo arg2) {
        // TODO Auto-generated method stub
        EventActionResult result = null;
        ISignOffEventInfo eventinfo = (ISignOffEventInfo)arg2;
        try {
            session.disableAllWarnings();
            IChange change = (IChange)eventinfo.getDataObject();
            updateCurrentChangeStatusConfig(session, change);
            result = new EventActionResult(eventinfo, doAction(session, change));
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

    public ActionResult doAction(IAgileSession session, IChange change) throws APIException {
        ActionResult result = new ActionResult(ActionResult.NORESULT, "无必须上传附件要求。 ");
        System.out.println(change.getName() + " >>>>>>>>>> " + changeConfig);
        if(changeConfig == null) return result;
        IStatus status = change.getStatus();
        String statusName = status.getName();
        A9ListConfig config = control.get(statusName);
        System.out.println(change.getName() + "/" + statusName + " >>>>>>>>>>>>>>> " + config);
        if(config == null) return new ActionResult(ActionResult.NORESULT, statusName + " 状态无必须上传附件要求。");
        Integer field = Integer.valueOf(changeConfig.getValue());
//        [3687] equal to '等待中'
        StringBuffer sql = new StringBuffer("[").append(changeConfig.getValue()).append("] Equal to %0");
//        boolean hasAttachement = false;
        ITable table = change.getTable(ChangeConstants.TABLE_ATTACHMENTS).where(sql.toString(), new String[] {statusName});
//        ITwoWayIterator it = table.getTableIterator();
//        while(it.hasNext()) {
//            IRow row = (IRow)it.next();
//            ICell cell  = row.getCell(field);
//            String value = CellUtils.getCellStringValue(cell);
//            if(statusName.equals(value)) {
//                hasAttachement = true;
//                break;
//            }
//            continue;
//        }
        return table.size() > 0 ? new ActionResult(ActionResult.NORESULT, statusName + " 满足上传附件要求。")
                                                : new ActionResult(ActionResult.EXCEPTION, new Exception(statusName + " 要求先上传附件! 请完成附件上传后再进行处理。"));
    }
    
    /**
     * 得到流程 必须附件 配置数据
     * @param session
     * @param change
     * @return
     * @throws APIException
     */
    public void updateCurrentChangeStatusConfig(IAgileSession session, IChange change) throws APIException {
     // 获取配置信息
        ICell cell = change.getCell(ChangeConstants.ATT_COVER_PAGE_CHANGE_TYPE);
        String changeType = CellUtils.getCellStringValue(cell);
        String[] nodePath = {NODE_NAME, changeType};
        changeConfig = SystemConfigAdaptor.getCaseConfig(session.getAdminInstance(), nodePath);
        System.out.println(NODE_NAME+"/"+changeType + " ==> " + changeConfig);
        control = SystemConfigAdaptor.getCaseConfigs(session.getAdminInstance(), nodePath);
    }
}
