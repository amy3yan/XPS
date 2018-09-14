/*******************************************************************************
 * @project: XPS
 * @package: com.xps.sap.adptor
 * @file: RFCResponse.java
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fionn
 *
 */
public class RFCResponse {

    private Map<String,String> topValues;
    private List<Map<String,String>> responseValues;
    
    public RFCResponse() {
        this.topValues = new HashMap<String, String>();
        this.responseValues = new ArrayList<Map<String,String>>();
    }
    /**
     * Get all single values from sap RFC response
     * @return
     */
    public Map<String, String> getTopResponseValues(){
        return this.topValues;
    }
    /**
     * Get return values from sap RFC response
     * @return
     */
    public List<Map<String, String>> getResponseValues(){
        return this.responseValues;
    }
    
    /**
     * @param key
     * @param value
     */
    public void addTopResponseValues(String key, String value) {
        this.topValues.put(key, value);
    }
    
    /**
     * @param value
     */
    public void addResponseValues(Map<String, String> value) {
        this.responseValues.add(value);
    }
    
    /**
     * @param values
     */
    public void setResponseValues(List<Map<String,String>> values) {
        this.responseValues = values;
    }
}
