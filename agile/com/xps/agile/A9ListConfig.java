/*******************************************************************************
 * @project: XPS
 * @package: com.xps.agile
 * @file: A9ListConfig.java
 * @author: amy
 * @created: 2018.9
 * @purpose:
 * 
 * @version: 1.0
 * 
 * 
 * Copyright 2018 AMY All rights reserved.
 ******************************************************************************/
package com.xps.agile;

/**
 * @author amy
 *
 */
public class A9ListConfig {

    private String feature;
    private String caseFlag;
    private String value;
//    private int flag;
    /**
     * 
     */
    public A9ListConfig() {
        // TODO Auto-generated constructor stub
    }
    /**
     * @param feature
     * @param caseFlag
     * @param value
     */
    public A9ListConfig(String feature, String caseFlag, String value) {
        super();
        this.feature = feature;
        this.caseFlag = caseFlag;
//        try {
//            this.flag = Integer.parseInt(caseFlag.substring("CASE".length()));
//        } catch (NumberFormatException e) {
//            this.flag = -1;
//        }
        this.value = value;
    }
    /**
     * @return the feature
     */
    public String getFeature() {
        return feature;
    }
    /**
     * @param feature the feature to set
     */
    public void setFeature(String feature) {
        this.feature = feature;
    }
    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }
    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return new StringBuffer("[").append("fature: ").append(feature)
                .append(", ").append("caseFlag: ").append(caseFlag)
                .append(", ").append("value: ").append(value)
//                .append(", ").append("flag: ").append(flag)
                .append("]").toString();
    }
    /**
     * @return the caseFlag
     */
    public String getCaseFlag() {
        return caseFlag;
    }
    /**
     * @param caseFlag the caseFlag to set
     */
    public void setCaseFlag(String caseFlag) {
        this.caseFlag = caseFlag;
//        try {
//            this.flag = Integer.parseInt(caseFlag.substring("CASE".length()));
//        } catch (NumberFormatException e) {
//            this.flag = -1;
//        }
    }
    
//    public int getFlag() {
//        return flag;
//    }
    
    
    
}
