/*******************************************************************************
 * @project: XPS
 * @package: com.xps.agile.utils
 * @file: CellUtils.java
 * @author: amy
 * @created: 2018.9
 * @purpose:
 * 
 * @version: 1.0
 * 
 * 
 * Copyright 2018 AMY All rights reserved.
 ******************************************************************************/


package com.xps.agile.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.agile.api.APIException;
import com.agile.api.DataTypeConstants;
import com.agile.api.IAgileList;
import com.agile.api.ICell;
import com.agile.api.IDataObject;
import com.agile.api.IRow;

/**
 * 读取/存储 ICell 的值
 * 
 * @author amy
 */
public class CellUtils{

	/**
	 * 单列表cell属性
	 * 
	 * @param cell
	 * @return
	 * @throws APIException
	 */
	public static Object getSingleListCellValue(ICell cell) throws APIException{
	    if(cell == null) return null;
		Object value = null;
		IAgileList list = (IAgileList) cell.getValue();
		IAgileList[] selected = list.getSelection();
		if(selected != null && selected.length > 0){
			value = selected[0].getValue();
		}
		return value;
	}

	/**
	 * 多列表cell属性
	 * 
	 * @param cell
	 * @return
	 * @throws APIException
	 */
	public static String getMultiListCellValue(ICell cell) throws APIException{
		String value = null;
		IAgileList agileList = (IAgileList) cell.getValue();
		value = agileList.toString();
		return value;
	}

	/**
	 * 非列表属性
	 * 
	 * @param cell
	 * @return
	 * @throws APIException
	 */
	public static Object getCellValue(ICell cell) throws APIException{
		Object value = null;
		value = cell.getValue();
		return value;
	}

	/**
	 * cell 的文本表示值
	 * 
	 * @param cell
	 * @return
	 * @throws APIException
	 */
	public static String getCellStringValue(ICell cell) throws APIException{
		if(cell == null)
			return "";
		Object value = null;
		switch(cell.getDataType()){
		case DataTypeConstants.TYPE_SINGLELIST:
			value = getSingleListCellValue(cell);
			break;
		case DataTypeConstants.TYPE_MULTILIST:
			value = getMultiListCellValue(cell);
			break;
		default:
			value = getCellValue(cell);
		}
		return value != null ? value.toString() : "";
	}

	/**
     * cell 的文本表示Old值
     * 
     * @param cell
     * @return
     * @throws APIException
     */
    public static String getCellStringOldValue(ICell cell) throws APIException{
        if(cell == null)
            return "";
        Object value = null;
        switch(cell.getDataType()){
        case DataTypeConstants.TYPE_SINGLELIST:
            value = getSingleListCellOldValue(cell);
            break;
        case DataTypeConstants.TYPE_MULTILIST:
            value = getMultiListCellOldValue(cell);
            break;
        default:
            value = getCellOldValue(cell);
        }
        return value != null ? value.toString() : "";
    }
	
    /**
     * 单列表cell属性Old
     * 
     * @param cell
     * @return
     * @throws APIException
     */
    public static Object getSingleListCellOldValue(ICell cell) throws APIException{
        Object value = null;
        IAgileList list = (IAgileList) cell.getOldValue();
        IAgileList[] selected = list.getSelection();
        if(selected != null && selected.length > 0){
            value = selected[0].getValue();
        }
        return value;
    }
    
    /**
     * 多列表cell属性Old
     * 
     * @param cell
     * @return
     * @throws APIException
     */
    public static String getMultiListCellOldValue(ICell cell) throws APIException{
        String value = null;
        IAgileList agileList = (IAgileList) cell.getOldValue();
        value = agileList.toString();
        return value;
    }
    
    /**
     * 非列表属性Old
     * 
     * @param cell
     * @return
     * @throws APIException
     */
    public static Object getCellOldValue(ICell cell) throws APIException{
        Object value = null;
        value = cell.getOldValue();
        return value;
    }
    
	/**
	 * 为List属性赋值
	 * @param cell
	 * @param newValues
	 * @throws APIException
	 */
	public static void setListCellValue(ICell cell, Object[] newValues) throws APIException {
	    IAgileList values = cell.getAvailableValues();
	    values.setSelection(newValues.length > 0 ? newValues : new Object[] {});
	    cell.setValue(values);
	}
	
	/**
	 * @desc: 根据BaseID取得obj的属性值
	 *
	 * @param obj
	 * @param baseid
	 * @return
	 * @throws APIException
	 * @author: fionn
	 */
	public static String getStringByBaseID(IDataObject obj, int baseid) throws APIException{
        ICell cell = obj.getCell(baseid);
        return getCellStringValue(cell);
    }
	
	/**
     * @desc: 根据BaseID取得obj的属性值
     *
     * @param obj
     * @param baseid
     * @return
     * @throws APIException
     * @author: fionn
     */
    public static String getStringByBaseID(IRow obj, int baseid) throws APIException{
        ICell cell = obj.getCell(baseid);
        return getCellStringValue(cell);
    }

	/**
	 * @desc: 取list Cell的所选对象
	 *
	 * @param cell
	 * @return
	 * @throws APIException
	 * @author: fionn
	 */
	public static Set<Object> getCellListValues(ICell cell) throws APIException{
	    Set<Object> values = new HashSet<Object>();
	    IAgileList list = (IAgileList)cell.getValue();
	    IAgileList[] selecteds = list.getSelection();
	    for(IAgileList lst : selecteds) {
	        values.add(lst.getValue());
	    }
	    return values;
	}
	
	/**
	 * @desc: 获取List选择值的详细信息，包括Value、APINAME、Description
	 *
	 * @param cell
	 * @return
	 * @throws APIException
	 * @author: fionn
	 */
	public static List<Map<String, String>> getCellTextListDetailValues(ICell cell) throws APIException{
	    List<Map<String, String>> detailValues = new ArrayList<Map<String,String>>();
	    if(cell == null) return detailValues;
	    IAgileList list = (IAgileList)cell.getValue();
        IAgileList[] selecteds = list.getSelection();
        if(selecteds == null) return detailValues;
        for(IAgileList lst : selecteds) {
            Map<String, String> values = new HashMap<String, String>();
            values.put("VALUE", (String)lst.getValue());
            values.put("APINAME", lst.getAPIName());
            values.put("DESCRIPTION", lst.getDescription());
            detailValues.add(values);
        }
	    return detailValues;
	}
	
	/**
	 * @desc: 取文本单列表属性选择的值的详细信息
	 *
	 * @param obj
	 * @param cellid
	 * @return
	 * @throws APIException
	 * @author: fionn
	 */
	public static Map<String, String> getSingleTextListDetailValuesByBaseID(IDataObject obj, int cellid) throws APIException{
	    ICell cell = obj.getCell(cellid);
	    if(cell == null) return Collections.EMPTY_MAP; 
	    List<Map<String, String>> details = getCellTextListDetailValues(cell);
	    if(details.isEmpty()) return Collections.EMPTY_MAP;
	    return details.get(0);
	}
	
	/**
	 * @desc: 获取obj 指定Numeric类型Cell的值，若为空，返回null
	 *
	 * @param obj
	 * @param cellid
	 * @return
	 * @throws APIException
	 * @author: fionn
	 */
	public static Double getCellNumericValue(IDataObject obj, int cellid) throws APIException{
	    ICell cell = obj.getCell(cellid);
	    if(cell == null || DataTypeConstants.TYPE_DOUBLE != cell.getDataType()) return null;
	    Double numeric = (Double) cell.getValue();
	    return numeric;
	}
}
