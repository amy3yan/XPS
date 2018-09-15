/*******************************************************************************
 * @project: CIG-PX
 * @package: com.cig.constant
 * @author: fionn
 * @created: 2016年9月20日
 * @purpose:
 * 
 * @version: 1.0
 * 
 * 
 * Copyright 2016 HAND All rights reserved.
 ******************************************************************************/
package com.xps.agile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author fionn
 *
 */
public class PXConstant {

    private static PXLogger log = PXLogger.getLogger(PXConstant.class);
    private static Properties p = new Properties();
    private static Map<String, Long> lastmodifieds = new HashMap<String, Long>();
    
    /**
     * Database connect config
     */
    private static Map<String, String> dbconfig;
    /**
     * Set by 'agile.url' in properties file.
     */
    public static String AGILE_URL = get("agile_url");
    /**
     * Set by 'agile.user' in properties file.
     */
    public static String AGILE_USER = get("agile_user");
    /**
     * Set by 'agile.passwd' in properties file.
     */
    public static String AGILE_PASSWD = get("agile_passwd");
    /**
     * Set by 'mail.server' in properties file.
     */
    public static String MAIL_SERVER = get("mail_server");
    /**
     * Set by 'mail.user' in properties file.
     */
    public static String MAIL_USER = get("mail_user");
    /**
     * Set by 'mail.passwd' in properties file.
     */
    public static String MAIL_PASSWD = get("mail_passwd");
    /**
     * Set by 'mail.from' in properties file.
     */
    public static String MAIL_FROM = get("mail_from");
    
    public static final String KEY_OBJECT_IN_QUERY = "AGILE_OBJECT";
    
    /**
     * @desc: 
     *
     *
     */
    protected PXConstant() {
        
    }

    /**
     * @desc: 返回配置文件中的字符串值
     *
     * @param key
     * @return
     * @author: fionn
     */
    private static String get(String key) {
    	loadConfig(new File("config/agile.properties"));
    	loadConfig(new File("config/system.properties"));
        return getValue(key);
    }

    public static String getValue(String key) {
    	return p.getProperty(key);
    }
    
    /**
     * @desc: Agile 数据库参数
     * @return
     * @author: fionn
     */
    public static Map<String, String> getDBinfo(){
        if(dbconfig == null) {
            dbconfig = new HashMap<String, String>();
            dbconfig.put("db.datasource", get("datasource"));
            dbconfig.put("db.driver", get("db.driver"));
            dbconfig.put("db.url", get("db.url"));
            dbconfig.put("db.user", get("db.user"));
            dbconfig.put("db.password", get("db.password"));
        }
        return dbconfig;
    }
    
    /**
     * @desc: 获取配置文件中设定的集合对象
     * Could not be added or removed any element in the list.
     * @param key
     * @return
     * @author: fionn
     */
    public static List<String> getList(String key, String split){
        String[] ignores = get(key).split(split);
        return Arrays.asList(ignores);
    }
    
    /**
     * 加载指定配置文件
     * @param file
     *
     * @author Amy
     */
    protected static void loadConfig(File file) {
        Long lastmodified = lastmodifieds.get(file.getName());
        FileInputStream fis = null;
        try {
            if(lastmodified == null || (file.exists() && lastmodified < file.lastModified())) {
	            fis = new FileInputStream(file);
	            p.load(fis);
	            lastmodifieds.put(file.getName(), file.lastModified());
            }
        } catch (Exception e) {
        	log.error(e);
            e.printStackTrace();
        } finally {
            if(fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
}
