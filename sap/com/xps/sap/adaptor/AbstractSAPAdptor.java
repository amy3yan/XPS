/*******************************************************************************
 * @project: XPS
 * @package: com.xps.sap.adaptor
 * @file: AbstractSAPAdptor.java
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.sap.conn.jco.JCoException;

/**
 * @author amy
 *
 */
public abstract class AbstractSAPAdptor {

    private static Logger log = Logger.getLogger(AbstractSAPAdptor.class);
    protected RFCAdaptor rfc;
    protected Set<String> rfcSuccessStatus;
    private RFCResponse rfcResponse;
    
    public AbstractSAPAdptor() {
        rfcSuccessStatus = new HashSet<String>();
        rfcSuccessStatus.add(RFCAdaptor.RESULT_STATUS_SUCCESS);
        rfcSuccessStatus.add(RFCAdaptor.RESULT_STATUS_INFO);
        rfcSuccessStatus.add(RFCAdaptor.RESULT_STATUS_WARNING);
    }
    
    /**
     * connect to sap
     * @param sapurl
     * @throws JCoException
     */
    public void connectSAP() throws JCoException {
        this.rfc = new RFCAdaptor(getRFCConfig());
    }
    
    /**
     * The config name of RFC for SAP
     * @return
     */
    public abstract String getRFCConfig();
    /**
     * Request Function Name for sap
     * @return
     */
    public abstract String getFunction();
    /**
     * Request table for sap
     * @return
     */
    public abstract String getRequestTable();
    /**
     * Request top params setting
     * @return
     */
    public abstract Map<String, String> getTopRequestParams();
    /**
     * Request data submit to sap
     * @return
     */
    public abstract List<IMappable> getRequestDatas();
    /**
     * Response Table Name
     * @return
     */
    public abstract String getResonseTable();
    /**
     * Response keys
     * @return
     */
    public abstract List<String> getResponseTopParamKeys();
    /**
     * Response object for fromMap()
     * @return
     */
    public abstract IMappable createResponseData();
    /**
     * do action before Submit;
     */
    public abstract void preSubmit();
    /**
     * do action after submit;
     */
    public abstract void afterSubmit();
    /**
     * @return
     */
    public IRequest getRequest() {
        return new IRequest() {
            
            @Override
            public Map<String, String> getTopRequestParams() {
                return AbstractSAPAdptor.this.getTopRequestParams();
            }
            
            @Override
            public String getTable() {
                return AbstractSAPAdptor.this.getRequestTable();
            }
            
            @Override
            public List<Map<String, String>> getRequestParams() {
                List<Map<String, String>> params = new ArrayList<Map<String,String>>();
                List<IMappable> datas = AbstractSAPAdptor.this.getRequestDatas();
                for(IMappable data : datas) {
                    params.add(data.toMap());
                }
                return params;
            }
            
            @Override
            public String getFunction() {
                return AbstractSAPAdptor.this.getFunction();
            }
        };
    }
    
    /**
     * @throws JCoException 
     * 
     */
    public void submit() throws JCoException {
        preSubmit();
        IRequest req = getRequest();
        rfc.setRequestVault(req.getFunction(), req.getTable());
        Map<String, String> map = req.getTopRequestParams();
        for(Map.Entry<String, String> entry : map.entrySet()) {
            rfc.SetTopRequestParam(entry.getKey(), entry.getValue());
        }
        rfc.updateRequestParams(req.getRequestParams());
        rfc.submit();
        rfcResponse = new RFCResponse();
        for(String key : getResponseTopParamKeys()) {
            rfcResponse.addTopResponseValues(key, rfc.getTopResponseValue(key));
        }
        rfcResponse.setResponseValues(rfc.getResponse(getResonseTable()));
        afterSubmit();
    }
 
    /**
     * @return
     */
    public RFCResponse getRFCResponse() {
        return rfcResponse;
    }
    
    /**
     * @return
     */
    public List<IMappable> getResponseDatas(){
        List<IMappable> datas = new ArrayList<IMappable>();
        List<Map<String, String>> responses = rfcResponse.getResponseValues();
        for(Map<String, String> response : responses) {
            IMappable data = createResponseData();
            data.fromMap(response);
            datas.add(data);
        }
        return datas;
    }
    
}
