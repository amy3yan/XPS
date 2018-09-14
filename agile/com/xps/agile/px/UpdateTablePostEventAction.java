/*******************************************************************************
 * @project: XPS
 * @package: com.xps.agile.px
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

import com.agile.api.IAgileSession;
import com.agile.api.IDataObject;
import com.agile.api.INode;
import com.agile.px.ActionResult;
import com.agile.px.EventActionResult;
import com.agile.px.EventConstants;
import com.agile.px.IEventAction;
import com.agile.px.IEventDirtyRow;
import com.agile.px.IEventDirtyTable;
import com.agile.px.IEventInfo;
import com.agile.px.IUpdateTableEventInfo;

/**
 * @author amy
 *
 */
public abstract class UpdateTablePostEventAction implements IEventAction {

    protected IUpdateTableEventInfo eventInfo;
    protected IAgileSession session;
    protected INode node; 
    protected IDataObject dataObject;
    /**
     * @desc: 
     *
     *
     */
    public UpdateTablePostEventAction() {
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see com.agile.px.IEventAction#doAction(com.agile.api.IAgileSession, com.agile.api.INode, com.agile.px.IEventInfo)
     */
    @Override
    public EventActionResult doAction(IAgileSession session, INode node, IEventInfo evinfo) {
        ActionResult result = null;
        this.session = session;
        this.node = node;
        this.eventInfo = (IUpdateTableEventInfo)evinfo;
        try {
            this.dataObject = eventInfo.getDataObject();
            IEventDirtyTable dirtyTbl = eventInfo.getTable();
            Iterator<IEventDirtyRow> dirtyit = dirtyTbl.iterator();
            while(dirtyit.hasNext()) {
                IEventDirtyRow dirtyRow = dirtyit.next();
                int action = dirtyRow.getAction();
                switch(action) {
                case EventConstants.DIRTY_ROW_ACTION_ADD:
                    result = addRow(dirtyRow);
                    break;
                case EventConstants.DIRTY_ROW_ACTION_DELETE:
                    result = deleteRow(dirtyRow);
                    break;
                case EventConstants.DIRTY_ROW_ACTION_UPDATE:
                    result = updateRow(dirtyRow);
                    break;
                case EventConstants.DIRTY_ROW_ACTION_REDLINEADD:
                    result = redlineAdd(dirtyRow);
                    break;
                case EventConstants.DIRTY_ROW_ACTION_REDLINEDELETE:
                    result = redlineDelete(dirtyRow);
                    break;
                case EventConstants.DIRTY_ROW_ACTION_REDLINEUPDATE:
                    result = redlineUpdate(dirtyRow);
                    break;
                case EventConstants.DIRTY_ROW_ACTION_UNDOREDLINE:
                    result = redlineUndo(dirtyRow);
                    break;
                case EventConstants.DIRTY_ROW_ACTION_ADD_FILE:
                    result = addFile(dirtyRow);
                    break;
                case EventConstants.DIRTY_ROW_ACTION_REPLACE_FILE:
                    result = replaceFile(dirtyRow);
                    break;
                default:
                    result = doNothing();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = new ActionResult(ActionResult.EXCEPTION, e);
        }
        return new EventActionResult(evinfo, result);
    }

    /**
     * @desc: 不在事件范围内，什么都不做
     *
     * @author: fionn
     */
    private ActionResult doNothing() {
        return null;
    }
    
    /**
     * @desc: Table增加行事件操作(添加文件行除外)
     *
     * @param dirtyRow
     * @throws Exception
     * @author: fionn
     */
    protected ActionResult addRow(IEventDirtyRow dirtyRow){
        return null;
    }
    
    /**
     * @desc: Table更新行事件操作
     *
     * @param dirtyRow
     * @throws Exception
     * @author: fionn
     */
    protected ActionResult updateRow(IEventDirtyRow dirtyRow){
        return null;
    }
    
    /**
     * @desc: Table删除行事件操作
     *
     * @param dirtyRow
     * @throws Exception
     * @author: fionn
     */
    protected ActionResult deleteRow(IEventDirtyRow dirtyRow){
        return null;
    }
    
    /**
     * @desc: Table Redline 增加行事件操作
     *
     * @param dirtyRow
     * @throws Exception
     * @author: fionn
     */
    protected ActionResult redlineAdd(IEventDirtyRow dirtyRow){
        return null;
    }
    
    /**
     * @desc: Table Redline 更新行事件操作
     *
     * @param dirtyRow
     * @throws Exception
     * @author: fionn
     */
    protected ActionResult redlineUpdate(IEventDirtyRow dirtyRow){
        return null;
    }
    
    /**
     * @desc: Table Redline 删除行事件操作
     *
     * @param dirtyRow
     * @throws Exception
     * @author: fionn
     */
    protected ActionResult redlineDelete(IEventDirtyRow dirtyRow){
        return null;
    }
    
    /**
     * @desc: Table undo Redline 行事件操作
     *
     * @param dirtyRow
     * @throws Exception
     * @author: fionn
     */
    protected ActionResult redlineUndo(IEventDirtyRow dirtyRow){
        return null;
    }
    
    /**
     * @desc: Table 新增文件行事件操作
     *
     * @param dirtyRow
     * @throws Exception
     * @author: fionn
     */
    protected ActionResult addFile(IEventDirtyRow dirtyRow){
        return null;
    }
    
    /**
     * @desc: Table 替换文件行事件操作
     *
     * @param dirtyRow
     * @throws Exception
     * @author: fionn
     */
    protected ActionResult replaceFile(IEventDirtyRow dirtyRow){
        return null;
    }
}
