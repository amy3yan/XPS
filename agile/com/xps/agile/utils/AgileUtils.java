/*******************************************************************************
 * @project: XPS
 * @package: com.xps.agile.utils
 * @file: AgileUtils.java
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.agile.api.APIException;
import com.agile.api.AgileSessionFactory;
import com.agile.api.IAdmin;
import com.agile.api.IAdminList;
import com.agile.api.IAgileClass;
import com.agile.api.IAgileList;
import com.agile.api.IAgileSession;
import com.agile.api.IAttribute;
import com.agile.api.IAutoNumber;
import com.agile.api.IDataObject;
import com.agile.api.IQuery;
import com.agile.api.IRow;
import com.agile.api.ITable;
import com.agile.api.ITwoWayIterator;
import com.agile.api.PropertyConstants;
import com.xps.agile.PXConstant;
import com.xps.utils.StringUtils;

/**
 * @author amy
 *
 */
public class AgileUtils {

	private static Logger log = Logger.getLogger(AgileUtils.class);
	
	public static IAgileSession createAgileSession(String url, String user, String passwd) {
		try {
			HashMap<Integer, Object> maps = new HashMap<Integer, Object>();
			maps.put(AgileSessionFactory.USERNAME, user);
			maps.put(AgileSessionFactory.PASSWORD, passwd);
			AgileSessionFactory sessionFactory = AgileSessionFactory.getInstance(url);
			IAgileSession session = sessionFactory.createSession(maps);
			return session;
		} catch (APIException e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * @param className
	 * @param session
	 * @return
	 * @throws APIException
	 */
	public static String getOneAutoNumber(String className, IAgileSession session) throws APIException {
	   String number = null;
	   IAdmin admin = session.getAdminInstance();
	   IAgileClass a9class = admin.getAgileClass(className);
	   IAutoNumber[] ans = a9class.getAutoNumberSources();
	   if(ans.length > 0) {
	       number = ans[0].getNextNumber();
	   }
	   return number;
	}
	
	/**
	 * 将Agile取得的值转为字符串
	 * @param obj
	 * @return
	 * @throws APIException 
	 */
	public static String getAgileValue(Object obj) throws APIException {
	    if (obj == null) return null;
	    // 列表值
        if (obj instanceof IAgileList) {
            return getListValue(obj);
        } else if (obj instanceof Date) {// 日期，转换为本地日期
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            format.setTimeZone(TimeZone.getTimeZone("GMT+8"));//本地时间
            String s = format.format(obj);
            log.info("date1: "+s);
            return s;
        } else if(obj instanceof Double){
            String val = String.valueOf(obj);
            if(val.endsWith(".0")) val = val.substring(0, val.indexOf("."));
            return val;
        } else { // 其他 直接转换成字符串
            return String.valueOf(obj);
        }
	}
	
	/**
	 * Agile 中列表值转换成字符串， 分多列表和单列表处理
	 * @param object
	 * @return
	 * @throws APIException
	 */
	public static String getListValue(Object object) throws APIException {
	    String value = null;
	    if(object == null) return value;
        IAgileList list = (IAgileList) object;
        IAgileList[] selected = list.getSelection();
        if(selected == null || selected.length < 1) return null;
        // cascadeList 多列表，直接返回字符串即可
        if (selected[0] instanceof IAgileList) {
            value = String.valueOf(list);
        } else { // 单列表
            value = (String)(selected[0].getValue());
        }
        return value;
    }
	
	/**
	 * @desc: 生成对象的SmartURL
	 *
	 * @param agileurl
	 * @param obj
	 * @param tab
	 * @return
	 * @throws APIException
	 */
	public static String getSmartURL(String agileurl, IDataObject obj, String tab) throws APIException{
	    StringBuilder url = new StringBuilder(agileurl).append("/object/");
	    url.append(obj.getAgileClass().getName()).append("/");
	    url.append(obj.getName());
	    if(!"".equals(tab) && tab != null) url.append("/tab/").append(tab);
	    return url.toString();
	}
	
	/**
     * @desc: 更新列表
     *
     * @param admlst
     * @param newname
     * @param oldname
     * @param desc
     * @throws Exception
     */
    public static void updateList(IAdminList admlst, String newname, String oldname, String desc) throws Exception{
        IAgileList list = admlst.getValues();
        Collection<IAgileList> nodes = list.getChildNodes();
        if("".equals(oldname) || oldname == null) { // 新增Product line
            IAgileList item = (IAgileList)list.addChild(newname, "V"+StringUtils.getNumberByPrefix(String.valueOf(nodes.size()+1), 4));
            if(desc != null) item.setDescription(desc);
        }else { // 修改Product line
            if(newname.equals(oldname)) return;
            Iterator<IAgileList> it = nodes.iterator();
            while(it.hasNext()) {
                IAgileList item = it.next();
                if(!oldname.equals(item.getName())) continue;
                String apiname = item.getAPIName();
                list.addChild(newname, apiname);
                break;
            }
        }
        admlst.setValues(list);
    }
    
    /**
     * @desc: 执行Query获取数据, 返回的列与IQuery定义的列相同(API_NAME)。
     * AGILE_OBJECT： 搜索的Agile对象
     * @param query
     * @return
     * @throws Exception
     *
     * @author Amy
     */
    public static List<Map<String, Object>> queryResults(IQuery query, Object[] params) throws Exception{
    	List<Map<String, Object>> results = new ArrayList<>();
    	IAttribute[] resultAtts = query.getResultAttributes(false);
    	ITable qtbl = query.execute(params);
    	ITwoWayIterator qtblit = qtbl.getTableIterator();
    	while(qtblit.hasNext()) {
    		Map<String, Object> result = new HashMap<>();
    		IRow row = (IRow) qtblit.next();
    		IDataObject obj = row.getReferent();
    		result.put(PXConstant.KEY_OBJECT_IN_QUERY, obj);
    		for(IAttribute attribute : resultAtts) {
    			Object value = row.getValue(attribute);
    			result.put(attribute.getAPIName(), value);
    			result.put(String.valueOf(attribute.getId()), value);
    		}
    	}
    	return results;
    }
    
}
