/*******************************************************************************
 * @project: XPS
 * @package: com.xps.agile.px
 * @file: UpdateChangeAttachmentEvent.java
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

import java.util.Iterator;

import com.agile.api.APIException;
import com.agile.api.IAgileSession;
import com.agile.api.IChange;
import com.agile.api.INode;
import com.agile.api.IRow;
import com.agile.api.ITable;
import com.agile.api.ITwoWayIterator;
import com.agile.px.ActionResult;
import com.agile.px.EventActionResult;
import com.agile.px.EventConstants;
import com.agile.px.IEventAction;
import com.agile.px.IEventDirtyFile;
import com.agile.px.IEventDirtyRow;
import com.agile.px.IEventDirtyRowFileUpdate;
import com.agile.px.IEventDirtyTable;
import com.agile.px.IEventInfo;
import com.agile.px.IUpdateTableEventInfo;

/**
 * @author amy
 *
 */
public class UpdateChangeAttachmentEvent implements IEventAction {

    private static final Integer status_field = new Integer(3687);
    /**
     * 
     */
    public UpdateChangeAttachmentEvent() {
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see com.agile.px.IEventAction#doAction(com.agile.api.IAgileSession, com.agile.api.INode, com.agile.px.IEventInfo)
     */
    @Override
    public EventActionResult doAction(IAgileSession session, INode arg1,
            IEventInfo arg2) {
        EventActionResult result = null;
        IUpdateTableEventInfo eventinfo = (IUpdateTableEventInfo)arg2;
        try {
            session.disableAllWarnings();
            ActionResult actionResult = new ActionResult(ActionResult.NORESULT, "");
            IChange change = (IChange)eventinfo.getDataObject();
            IEventDirtyTable dtable = eventinfo.getTable();
            Iterator it = dtable.iterator();
            if(it.hasNext()) {
                IEventDirtyRow row = (IEventDirtyRow) it.next();
                System.out.println(change.getName() + " --->  Row Action: < " + row.getAction() + " > is ADD FILE? " + (row.getAction() ==EventConstants.DIRTY_ROW_ACTION_ADD_FILE));
                if(row.getAction() == EventConstants.DIRTY_ROW_ACTION_ADD_FILE) {
                    IEventDirtyRowFileUpdate rfu = (IEventDirtyRowFileUpdate)row;
                    IEventDirtyFile file = rfu.getFile();
                    String filename = file.getFilename();
                    System.out.println("文件名："+ filename +", >>>>>>>>>文件夹：" + file.getFileFolder());
                    actionResult = doAction(session, change, filename);
                }
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

    public ActionResult doAction(IAgileSession session, IChange change, String filename) throws APIException {
        ActionResult result = new ActionResult(ActionResult.NORESULT, "");
        StringBuffer sql = new StringBuffer("[").append(status_field).append("] is null and [1046] equal to %0");
        ITable atable = change.getAttachments().where(sql.toString(), new String[] {filename});
        ITwoWayIterator it = atable.getTableIterator();
        while(it.hasNext()) {
            IRow row = (IRow)it.next();
            row.setValue(status_field, change.getStatus().getName());
        }
        result = new ActionResult(ActionResult.NORESULT, "上传附件 流程状态设置成功。");
        return result;
    }
}
