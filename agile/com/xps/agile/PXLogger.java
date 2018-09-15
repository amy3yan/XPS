/*******************************************************************************
 * @project: CIG
 * @package: com.hand.agile.log
 * @author: fionn
 * @created: 2016年9月8日
 * @purpose:
 * 
 * @version: 1.0
 * 
 * 
 * Copyright 2016 HAND All rights reserved.
 ******************************************************************************/
package com.xps.agile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author fionn
 *
 */
public class PXLogger {

    private static boolean log_to_file = true;
    private static Map<String, PXLogger> logs = new HashMap<String, PXLogger>();
    private String name;
    private static PrintWriter flw;
    private static FileOutputStream fis;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    
    /**
     * @desc: 根据class初始化实例
     *
     * @param cls
     * @return
     * @author: fionn
     */
    public static PXLogger getLogger(Class cls) {
        return getLogger(cls.getName());
    }
    
    /**
     * @desc: 初始化实例 
     *
     * @return
     * @author: fionn
     */
    public static PXLogger getLogger(String logname) {
        PXLogger log = new PXLogger(logname);
        if(log_to_file) initLogWriter();
        return log;
    }
    
    
    private PXLogger() {
        this("PXLOG");
    }
    
    /**
     * @desc: 
     *
     *
     */
    private PXLogger(String name) {
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        this.name = name;
    }
    
    public void log(String string) {
        // [PXLOG](L322 T111): 2016-09-14 09:16:45 mes
        Thread currtd = Thread.currentThread();
        StackTraceElement ste = currtd.getStackTrace()[2];
        String mes = new StringBuffer("[").append(ste.getClassName()).append("]")
                .append("(L").append(ste.getLineNumber()).append("")
                .append(" T").append(currtd.getId()).append("): ")
                .append(sdf.format(new Date())).append(" ").append(string).toString();
        if(log_to_file) flw.println(mes);
        System.out.println(string);
    }

    public void log(Object obj) {
        String mes = obj != null ? obj.toString() : "NULL";
        log(mes);
    }
    
    public void error(Exception e) {
        log(">>>>>>>>>>>>>>>>> Error >>>>>>>>>>>>>>>>>>>>>>>>>>" + e.getMessage());
        if(log_to_file) e.printStackTrace(flw);
        e.printStackTrace(System.out);
    }
    
    /**
     * @desc: 开启文件记录功能
     *
     * @author: fionn
     */
    public static void enable2File() {
        log_to_file = true;
        initLogWriter();
    }
    
    /**
     * @desc: 关闭用日志文件记录功能
     *
     * @author: fionn
     */
    public static void disabled2File() {
        log_to_file = false;
        if(flw != null) {
            flw.flush();
            flw.close();
            flw = null;
        }
        if(fis != null) {
            try {
                fis.flush();
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                fis = null;
            }
        }
    }
    
    /**
     * @desc: 初始化文件写入器
     *
     * @author: fionn
     */
    private static void initLogWriter() {
        if(flw == null) {
            try {
                File pxlog = new File("logs/px.log");
                if(!pxlog.getParentFile().exists()) pxlog.getParentFile().mkdirs();
                if(!pxlog.exists()) pxlog.createNewFile(); 
                fis = new FileOutputStream(pxlog, true);
                flw = new PrintWriter(fis, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * @return the name
     */
    public String getName() {
        return name;
    }


    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}
