/*******************************************************************************
 * @project: ebmproject
 * @package: com.hand.sapadp
 * @file: 
 * @author: fionn
 * @created: 2015年5月12日
 * @purpose:
 * 
 * @version: 1.0
 * 
 * 
 * Copyright 2015 HAND All rights reserved.
 ******************************************************************************/
/*******************************************************************************
 * @project: XPS
 * @package: com.xps.sap.adptor
 * @file: RFCAdaptor.java
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoRecordFieldIterator;
import com.sap.conn.jco.JCoTable;

/**
 * @author amy
 *
 */
public class RFCAdaptor {

    public final static String RESULT_STATUS_SUCCESS = "S";
    public final static String RESULT_STATUS_ERROR = "E";
    public final static String RESULT_STATUS_WARNING = "W";
    public final static String RESULT_STATUS_INFO = "I";
    public final static String RESULT_STATUS_ABORT = "A";
    private static Logger log = Logger.getLogger(RFCAdaptor.class);
    private JCoDestination destination;
    private JCoFunction jcoFunction;
    private JCoTable jcoRequestTable;
    private JCoTable jcoResponseTable;
    
    /**
     * @param config
     * @throws JCoException
     */
    public RFCAdaptor(String config) throws JCoException {
        long begin = new Date().getTime();
//        System.getProperties().list(System.out);
        log.debug("Begin to connect SAP by" + config);
        this.destination = JCoDestinationManager.getDestination(config);
        log.debug("Connect to SAP take time (ms): "+(new Date().getTime() - begin));
    }
    
    /**
     * @param function
     * @param requesttable
     * @throws JCoException
     */
    public void setRequestVault(String function, String requesttable) throws JCoException {
        try {
            long begin = new Date().getTime();
            this.jcoFunction = destination.getRepository().getFunction(function);
            log.debug("getFunction() take time (ms): "+(new Date().getTime() - begin));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
           throw new RuntimeException(function + " is not found in SAP.");
        }
        long begin = new Date().getTime();
        this.jcoRequestTable = jcoFunction.getTableParameterList().getTable(requesttable);
        log.debug("getTable() take time (ms): "+(new Date().getTime() - begin));
    }
    
    /**
     * re-set request params in jcoTable
     * @param params
     */
    public void updateRequestParams(List<Map<String, String>> params) {
        jcoRequestTable.clear();
        for(Map<String, String> param : params) {
           addRequestParam(param);
        }
    }
    
    /**
     * Set param for single param in request
     * @param key
     * @param value
     */
    public void SetTopRequestParam(String key, String value) {
        this.jcoFunction.getImportParameterList().setValue(key, value);
    }
    
    /**
     * submit request
     * @throws JCoException
     */
    public void submit() throws JCoException {
        log.debug("Begin to submit SAP request... ");
        long begin = new Date().getTime();
        jcoFunction.execute(destination);
        log.debug("Submit SAP request complete... ");
        log.debug("Submit() take time (ms): "+(new Date().getTime() - begin));
    }
    
    /**
     * @param resonseTable
     * @return
     */
    public List<Map<String,String>> getResponse(String resonseTable){
        List<Map<String, String>> response = new ArrayList<Map<String, String>>();
        long begin = new Date().getTime();
        this.jcoResponseTable = jcoFunction.getTableParameterList().getTable(resonseTable);
        log.debug("get jcoResponseTable take time (ms): "+(new Date().getTime() - begin));
        if(jcoResponseTable.getNumRows() > 0) {
            jcoResponseTable.firstRow();
            Map<String, String> map = null;
            do {
                map = new HashMap<String, String>();
                JCoRecordFieldIterator fit = jcoResponseTable.getRecordFieldIterator();
                JCoField field = null;
                while(fit.hasNextField()) {
                   field = fit.nextField();
                   log.debug(field.getName() + " =>" +field.getString());
                   map.put(field.getName(), field.getString());
                }
                response.add(map);
            }while(jcoResponseTable.nextRow());
        }
        return response;
    }
    
    /**
     * @param key
     * @return
     */
    public String getTopResponseValue(String key) {
        return jcoFunction.getExportParameterList().getString(key);
    }
    
    /**
     * add new param into jcoTable
     * @param param
     */
    private void addRequestParam(Map<String, String> param) {
        jcoRequestTable.appendRow();
        for(Map.Entry<String, String> entry : param.entrySet()) {
            jcoRequestTable.setValue(entry.getKey(), entry.getValue());
        }
    }
    
}
