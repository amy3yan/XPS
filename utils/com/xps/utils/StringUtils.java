/*******************************************************************************
 * @project: XPS
 * @package: com.xps.agile.utils
 * @file: StringUtils.java
 * @author: amy
 * @created: 2018.9
 * @purpose:
 * 
 * @version: 1.0
 * 
 * 
 * Copyright 2018 AMY All rights reserved.
 ******************************************************************************/
package com.xps.utils;

import java.util.regex.Pattern;

/**
 * @author amy
 *
 */
public class StringUtils {

    /**
     * 
     */
    public StringUtils() {
        // TODO Auto-generated constructor stub
    }

    /**
     * 若number全是数字，返回前面补0的18位长字符串
     * SAP 物料编码若全是数字，要求前面用0补全18位长度
     * @param matnr
     * @return
     */
    public static String getNumberByPrefix(String number, int length) {
        if(Pattern.matches("^\\d{1,}$", number) && number.length() < length) {
            StringBuffer number18 = new StringBuffer();
            for(int i=0; i<(length - number.length()); i++) {
                number18.append("0");
            }
            return number18.append(number).toString();
        }else {
            return number;
        }
    }

    /**
     * @desc: 返回有效可用于文件路径的字符串
     *
     * @param str
     * @return
     */
    public static String getValidPathStr(String str) {
        return str != null ? str.replaceAll("[\\\\/:\\-?\"<>|]", "") : str;
    }
    
    public static double getDoubleValue(Object obj)
    {
        double d = 0.0;
        String value = getStr(obj);
        value = value.replace("ALT ", "");
        if(!"".equals(value))
        {
            d = Double.parseDouble(value);
        }
        return d;
    }
    
    public static String getStr(Object obj)
    {
        return null == obj ? "" : obj.toString();
    }
    
    /**
     * @desc: 
     *
     * @param content
     * @return
     */
    public static String html(String content) {
        if(content==null) return "";       
        content = content.replace("'", "&apos;")
                .replace("\"", "&quot;")
                .replace("\t", "&nbsp;&nbsp;")// 替换跳格
//                .replace(" ", "&nbsp;")// 替换空格
                .replace("<", "&lt;")
                .replace(">", "&gt;");
        return content;
    }

}
