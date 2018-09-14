/*******************************************************************************
 * @project: XPS
 * @package: com.xps.utils
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

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author amy
 *
 */
public class ExcelUtils {

    /**
     * @desc: 设置 单元格 字符串值
     *
     * @param cell
     * @param value
     */
    public static void setCellStringValue(Cell cell, String value) {
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue(value);
    }
    
    /**
     * @desc: 取 单元格 字符串值
     *
     * @param cell
     * @return
     */
    public static String getCellStringValue2(Cell cell) {
        String value = "";
        if(cell == null) return value;
        Workbook wb = cell.getRow().getSheet().getWorkbook();
        int type = cell.getCellType();
        switch(type) {
            case Cell.CELL_TYPE_NUMERIC:
                double val = cell.getNumericCellValue();
                value = String.valueOf(val);
                break;
            case Cell.CELL_TYPE_FORMULA:
                FormulaEvaluator eva = (wb instanceof HSSFWorkbook) ? new HSSFFormulaEvaluator((HSSFWorkbook)wb) : new XSSFFormulaEvaluator((XSSFWorkbook)wb);
                CellValue cellValue = eva.evaluate(cell);
                int cellType = cellValue.getCellType();
                if(cellType == Cell.CELL_TYPE_NUMERIC) {
                    value = String.valueOf(cellValue.getNumberValue());
                }else {
                    value = cellValue.getStringValue();
                }
                break;
            default:
                value = cell.getStringCellValue();
        }
        return value.trim();
    }
    
    public static String getCellStringValue(Cell cell) {
        String value = "";
        if(cell == null) return value;
        cell.setCellType(Cell.CELL_TYPE_STRING);
        value = cell.getStringCellValue();
        return value.trim();
    }
    
    public static String getCellStringValue(Row row, int cellIdx) {
        Cell cell = row.getCell(cellIdx);
        return getCellStringValue(cell);
    }
    
}
