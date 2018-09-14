/*******************************************************************************
 * @project: XPS
 * @package: com.xps.sap.adptor
 * @file: IMappable.java
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

import java.util.Map;

/**
 * @author amy
 *
 */
public interface IMappable {

    public Map<String, String> toMap();
    public void fromMap(Map<String, String> map);
}
