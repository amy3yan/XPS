/*******************************************************************************
 * @project: XPS
 * @package: com.xps.sap.adptor
 * @file: IRequest.java
 * @author: AMY
 * @created: 2018.9
 * @purpose:
 * 
 * @version: 1.0
 * 
 * 
 * Copyright 2018 AMY All rights reserved.
 ******************************************************************************/
package com.xps.sap.adaptor;

import java.util.List;
import java.util.Map;

/**
 * @author amy
 *
 */
public interface IRequest {

    /**
     * Call RFC Function Name
     * @return
     */
    public String getFunction();
    /**
     * Table name for the RFC Function
     * @return
     */
    public String getTable();
    /**
     * The payload submited to RFC function 
     * @return
     */
    public List<Map<String, String>> getRequestParams();
    /**
     * Set top single param for request
     * @return
     */
    public Map<String, String> getTopRequestParams();
    
}
