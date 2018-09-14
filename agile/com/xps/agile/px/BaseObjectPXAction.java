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

import org.apache.log4j.Logger;

import com.agile.api.IAgileSession;
import com.agile.api.IDataObject;
import com.agile.api.INode;
import com.agile.px.ActionResult;
import com.agile.px.EventActionResult;
import com.agile.px.ICustomAction;
import com.agile.px.IEventAction;
import com.agile.px.IEventInfo;
import com.agile.px.IObjectEventInfo;

/**
 * @author amy
 *
 */
public abstract class BaseObjectPXAction implements IEventAction, ICustomAction {
    
    private static Logger log = Logger.getLogger(BaseObjectPXAction.class);

    protected IObjectEventInfo eventInfo;
    
    protected INode node;
    
    protected Integer eventType;
    
    /**
     * @desc: 
     *
     *
     */
    public BaseObjectPXAction() {
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see com.agile.px.ICustomAction#doAction(com.agile.api.IAgileSession, com.agile.api.INode, com.agile.api.IDataObject)
     */
    @Override
    public ActionResult doAction(IAgileSession session, INode arg1, IDataObject arg2) {
        this.node = arg1;
        ActionResult result = doAction(session, arg2);
        return result;
    }

    /* (non-Javadoc)
     * @see com.agile.px.IEventAction#doAction(com.agile.api.IAgileSession, com.agile.api.INode, com.agile.px.IEventInfo)
     */
    @Override
    public EventActionResult doAction(IAgileSession session, INode arg1, IEventInfo arg2) {
        this.node = arg1;
        EventActionResult result = null;
        try {
            eventInfo = (IObjectEventInfo) arg2;
            eventType = eventInfo.getEventType();
            IDataObject agileobj = eventInfo.getDataObject();
            result = new EventActionResult(eventInfo, doAction(session, agileobj));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            e.printStackTrace(System.out);
            result = new EventActionResult(arg2, new ActionResult(ActionResult.EXCEPTION, e));
        }
        return result;
    }
    
    /**
     * @desc: EventInfo
     *
     * @return
     * @author: fionn
     */
    public IObjectEventInfo getObjectEventInfo() {
        return eventInfo;
    }
    
    /**
     * @desc: 实际程序
     *
     * @param session
     * @param agileobj
     * @return
     * @author: fionn
     */
    protected abstract ActionResult doAction(IAgileSession session, IDataObject agileobj);

}
