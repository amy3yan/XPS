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

import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
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
    public static String getCellStringValue(Cell cell) {
        String value = "";
        if(cell == null) return value;
        Workbook wb = cell.getRow().getSheet().getWorkbook();
        int type = cell.getCellType();
        switch(type) {
            case Cell.CELL_TYPE_NUMERIC:
//                double val = cell.getNumericCellValue();
//                value = String.valueOf(val);
                HSSFDataFormatter dataFormatter = new HSSFDataFormatter();
                value = dataFormatter.formatCellValue(cell);
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
    
    /**
     * @desc: 返回单元格中的字符串值
     *
     * @param cell
     * @return
     */
    public static String getCellStringValue2(Cell cell) {
        String value = "";
        if(cell == null) return value;
        cell.setCellType(Cell.CELL_TYPE_STRING);
        value = cell.getStringCellValue();
        return value.trim();
    }
    
    /**
     * @desc: 取row指定索引单元格的值
     *
     * @param row
     * @param cellIdx
     * @return
     */
    public static String getCellStringValue(Row row, int cellIdx) {
        Cell cell = row.getCell(cellIdx);
        return getCellStringValue(cell);
    }
    
    /**
     * @desc: 向row中各单元格写入数据
     *
     * @param row
     * @param values
     */
    public static void writeRow(Row row, Object[] values) {
        for(int i=0; i<values.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(String.valueOf(values[i]));
        }
    }
    
    /**
     * @desc: 向sheet中增加一行
     *
     * @param sheet
     * @param values
     */
    public static void appendRow(Sheet sheet, Object[] values) {
        int idx = sheet.getLastRowNum();
        Row row = sheet.createRow(++idx);
        writeRow(row, values);
    }
    
}
