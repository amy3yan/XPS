/*******************************************************************************
 * @project: XPS
 * @package: com.xps.agile
 * @file: SystemConfigAdaptor.java
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.agile.api.APIException;
import com.agile.api.IAdmin;
import com.agile.api.IAgileList;
import com.agile.api.ITreeNode;

/**
 * @author amy
 *
 */
public class SystemConfigAdaptor {
    private static final String SYSTEM_CONFIG_LISTNAME = "SystemConfig";
    private static final String CASCAD_HYPHEN = "_#_";
    private static Logger log = Logger.getLogger(SystemConfigAdaptor.class);
    /**
     * 
     */
    public SystemConfigAdaptor() {
        // TODO Auto-generated constructor stub
    }
    
    /**
     * 根据Path找到SystemConfig中的节点
     * @param admin
     * @param nodePath
     * @return
     * @throws APIException
     */
    public static ITreeNode getConfigNode(IAdmin admin, String[] nodePath) throws APIException {
        IAgileList sysConfig = admin.getListLibrary().getAdminList(SYSTEM_CONFIG_LISTNAME).getValues();
        ITreeNode node = null;
        for(String path : nodePath) {
            node = (node == null) ? sysConfig.getChildNode(path) : node.getChildNode(path);
        }
        return node;
    }
    
    /**
     * 取得SystemConfig配置列表中路径nodePath下所有叶节点的配置数据
     * @param admin
     * @param nodePath
     * @return
     * @throws APIException
     */
    public static Map<String, A9ListConfig> getCaseConfigs(IAdmin admin, String[] nodePath) throws APIException{
        Map<String, A9ListConfig> map = new HashMap<String, A9ListConfig>();
        ITreeNode node = getConfigNode(admin, nodePath);
        getNodeLeafCaseConfigs(node, map);
        return map;
    }
    
    /**
     * 取得parent节点中路径childPath下所有也节点的配置数据
     * @param parent
     * @param childPath
     * @return
     * @throws APIException
     */
    public static Map<String, A9ListConfig> getNodeCaseConfigs(ITreeNode parent, String[] childPath) throws APIException{
        Map<String, A9ListConfig> map = new HashMap<String, A9ListConfig>();
        ITreeNode node = null;
        for(String path : childPath) {
            node = (node == null) ? parent.getChildNode(path) : node.getChildNode(path);
        }
        getNodeLeafCaseConfigs(node, map);
        return map;
    }
    
    /**
     * 取得SystemConfig配置列表中路径该节点的配置数据
     * @param admin
     * @param nodePath
     * @return
     * @throws APIException
     */
    public static A9ListConfig getCaseConfig(IAdmin admin, String[] nodePath) throws APIException {
        ITreeNode node = getConfigNode(admin, nodePath);
        return getConfigFromSingleAgileList(node);
    }
    
    /**
     * 取得parent节点childPath对应配置数据
     * @param parent
     * @param childPath
     * @return
     * @throws APIException
     */
    public static A9ListConfig getCaseConfig(ITreeNode parent, String[] childPath) throws APIException {
        ITreeNode node = null;
        for(String path : childPath) {
            node = (node == null) ? parent.getChildNode(path) : node.getChildNode(path);
        }
        return getConfigFromSingleAgileList(node);
    }
    
    /**
     * 取叶节点配置数据, 加入到map中
     * @param node
     * @param map
     * @throws APIException
     */
    private static void getNodeLeafCaseConfigs(ITreeNode node, Map<String, A9ListConfig> map) throws APIException {
        if(node == null) return;
        Collection<Object> children = node.getChildNodes();
        if(children == null) {
            IAgileList nodeList = (IAgileList)node;
            A9ListConfig config = getConfigFromSingleAgileList(nodeList);
            map.put(config.getFeature(), config);
            return;
        }
        Iterator it = children.iterator();
        while(it.hasNext()) {
            IAgileList nodelist = (IAgileList)it.next();
            if(nodelist.isEnumeratable()) {
                getNodeLeafCaseConfigs(nodelist, map);
                continue;
            }
            A9ListConfig config = getConfigFromSingleAgileList(nodelist);
            map.put(config.getFeature(), config);
        }
    }
    
    private static A9ListConfig getConfigFromSingleAgileList(IAgileList list) throws APIException {
        if(list == null) return null;
        String value = String.valueOf(list.getValue());
        return new A9ListConfig(value.replace(CASCAD_HYPHEN, "|"), list.getAPIName(), list.getDescription());
    }
    
    private static A9ListConfig getConfigFromSingleAgileList(ITreeNode node) throws APIException {
        if(node == null) return null;
        IAgileList list = (IAgileList)node;
        return getConfigFromSingleAgileList(list);
    }
}
