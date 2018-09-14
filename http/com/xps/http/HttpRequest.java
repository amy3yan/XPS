/*******************************************************************************
 * @project: XPS
 * @package: com.xps.http
 * @author: amy
 * @created: 2018.9
 * @purpose:
 * 
 * @version: 1.0
 * 
 * 
 * Copyright 2018 AMY All rights reserved.
 ******************************************************************************/
package com.xps.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

/**
 * @author amy
 *
 */
public class HttpRequest {

    private static Logger log = Logger.getLogger(HttpRequest.class);
    private String contentEncoding;
    private int bufferSize = 1024*5;

    public static void main(String[] args) throws Exception {
        HttpRequest req = new HttpRequest();
        String url = "http://jira.rd.ci-g.com:8090/rest/api/2/search";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("jql", "issue in (TEST-227)");
        
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("Authorization", "Basic Z2l0bGFiOmdpdGxhYg==");
        properties.put("Accept", "application/json");
        
        HttpResponse res = req.get(url, params, properties);
        log.debug(res.getContent());
    }
    
    /**
     * @desc:  默认采用UTF-8
     *
     *
     */
    public HttpRequest() {
        this.contentEncoding = "UTF-8";//Charset.defaultCharset().name();
    }
    

    /**
     * @desc: 无参数提交 Get 方式请求
     *
     * @param urlString
     * @return
     * @throws Exception
     */
    public HttpResponse get(String urlString) throws Exception {
        return this.send(urlString, "GET", null, null);
    }

    /**
     * @desc: 带参数提交Get方式请求，参数变量在params中定义
     *
     * @param urlString
     * @param params
     * @return
     * @throws Exception
     */
    public HttpResponse get(String urlString, Map<String, Object> params) throws Exception {
        return this.send(urlString, "GET", params, null);
    }

    /**
     * @desc: 带参数和 Propertys 提交GET方式请求
     *
     * @param urlString
     * @param params
     * @param propertys
     * @return
     * @throws Exception
     */
    public HttpResponse get(String urlString, Map<String, Object> params, Map<String, String> propertys) throws Exception {
        return this.send(urlString, "GET", params, propertys);
    }

    /**
     * @desc: 无参数提交 POST 方式请求
     *
     * @param urlString
     * @return
     * @throws Exception
     */
    public HttpResponse post(String urlString) throws Exception {
        return this.send(urlString, "POST", null, null);
    }

    /**
     * @desc: 带参数提交POST方式请求，参数变量在params中定义
     *
     * @param urlString
     * @param params
     * @return
     * @throws Exception
     */
    public HttpResponse post(String urlString, Map<String, Object> params) throws Exception {
        return this.send(urlString, "POST", params, null);
    }

    /**
     * @desc: 带参数和 Propertys 提交POST方式请求
     *
     * @param urlString
     * @param params
     * @param propertys
     * @return
     * @throws IOException
     */
    public HttpResponse post(String urlString, Map<String, Object> params, Map<String, String> propertys) throws Exception {
        return this.send(urlString, "POST", params, propertys);
    }

    
    /**
     * @return the contentEncoding
     */
    public String getContentEncoding() {
        return contentEncoding;
    }

    /**
     * @param contentEncoding the contentEncoding to set
     */
    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    /**
     * @return the bufferSize
     */
    public int getBufferSize() {
        return bufferSize;
    }

    /**
     * @param bufferSize the bufferSize to set
     */
    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    /**
     * @desc: 提交请求
     *
     * @param httpurl
     * @param method
     * @param parameters
     * @param requestProperties
     * @return
     * @throws IOException
     */
    private HttpResponse send(String httpurl, String method, Map<String, Object> parameters, Map<String, String> requestProperties) throws Exception {
        HttpResponse httpResponse = new HttpResponse();
        
        HttpURLConnection httpConn = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        try{
            log.debug("[ "+method+" ] 访问URL："+httpurl + ", 参数：" + parameters);
            httpConn = "GET".equalsIgnoreCase(method) ? submitGet(httpurl, parameters, requestProperties) : submitPost(httpurl, parameters, requestProperties);
            if(httpConn == null) return httpResponse;
            // 格式化httpConn返回对象
            
            bufferedReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "utf-8"));
            StringWriter swriter = new StringWriter();
            bufferedWriter = new BufferedWriter(swriter);
            char[] cbuf = new char[bufferSize];
            int length = 0;
            while ((length = bufferedReader.read(cbuf)) != -1) {
                bufferedWriter.append(new String(cbuf, 0, length));
            }
            bufferedWriter.flush();
            String response = swriter.toString();
            httpResponse.url = httpurl;
            httpResponse.content = response;
            httpResponse.contentEncoding = contentEncoding;

            httpResponse.defaultPort = httpConn.getURL().getDefaultPort();
            httpResponse.file = httpConn.getURL().getFile();
            httpResponse.host = httpConn.getURL().getHost();
            httpResponse.path = httpConn.getURL().getPath();
            httpResponse.port = httpConn.getURL().getPort();
            httpResponse.protocol = httpConn.getURL().getProtocol();
            httpResponse.query = httpConn.getURL().getQuery();
            httpResponse.ref = httpConn.getURL().getRef();
            httpResponse.userInfo = httpConn.getURL().getUserInfo();
            httpResponse.code = httpConn.getResponseCode();
            httpResponse.message = httpConn.getResponseMessage();
            httpResponse.contentType = httpConn.getContentType();
            httpResponse.method = httpConn.getRequestMethod();
            httpResponse.connectTimeout = httpConn.getConnectTimeout();
            httpResponse.readTimeout = httpConn.getReadTimeout();
        }catch(Exception e) {
            log.error(e);
            throw e;
        }finally {
            if(bufferedReader != null) bufferedReader.close();
            if(bufferedWriter != null) bufferedWriter.close();
            if(httpConn != null) httpConn.disconnect();
        }
        return httpResponse;
    }
    
    /**
     * @desc: 提交Get请求
     *
     * @param httpurl
     * @param parameters
     * @param requestProperties
     * @return
     * @throws Exception
     */
    private HttpURLConnection submitGet(String httpurl, Map<String, Object> parameters, Map<String, String> requestProperties) throws Exception{
        String[] paramKeys = parameters.keySet().toArray(new String[] {});
        StringBuffer param = new StringBuffer();
        param.append("?").append(paramKeys[0]).append("=")
             .append(URLEncoder.encode((String)parameters.get(paramKeys[0]), contentEncoding));
        for (int i=1; i<paramKeys.length; i++) {
            param.append("&").append(paramKeys[i]).append("=")
                 .append(URLEncoder.encode((String)parameters.get(paramKeys[i]), contentEncoding));
        }
        httpurl += param;
        log.debug(httpurl);
        // 开始提交连接
        URL url = new URL(httpurl);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("GET");
        initConnection(httpConn);
        addRequestProperties(httpConn, requestProperties);
        return httpConn;
    }
    
    /**
     * @desc: 提交Post请求
     *
     * @param httpurl
     * @param method
     * @param parameters
     * @param requestProperties
     * @return
     * @throws Exception
     */
    private HttpURLConnection submitPost(String httpurl, Map<String, Object> parameters, Map<String, String> requestProperties) throws Exception{
        // 开始提交连接
        HttpURLConnection httpConn = null;
        DataOutputStream oStream = null;
        try {
            log.debug(URLEncoder.encode(httpurl, contentEncoding));
            URL url = new URL(URLEncoder.encode(httpurl, contentEncoding));
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("POST");
            initConnection(httpConn);
            addRequestProperties(httpConn, requestProperties);
            String[] paramKeys = parameters.keySet().toArray(new String[] {});
            StringBuffer param = new StringBuffer();
            for (int i=0; i<paramKeys.length; i++) {
                param.append("&").append(paramKeys[i]).append("=").append(parameters.get(paramKeys[i]));
            }
            oStream = new DataOutputStream(httpConn.getOutputStream());
            oStream.writeBytes(param.toString());
            oStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if(oStream != null) {
                oStream.close();
            }
        }
        return httpConn;
    }
    
    /**
     * @desc: 初始化HttpConnection
     *
     * @param httpConn
     */
    private void initConnection(HttpURLConnection httpConn) {
        httpConn.setConnectTimeout(3000);// 设置连接时间3s, 检查网络状态
        httpConn.setReadTimeout(300000);
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        httpConn.setUseCaches(false);
    }
    
    /**
     * @desc: 为httpConn设置requestProperty信息
     *
     * @param httpConn
     * @param requestProperties
     */
    private void addRequestProperties(HttpURLConnection httpConn, Map<String, String> requestProperties) {
        if (requestProperties.isEmpty()) return;
        for (Entry<String, String> entry : requestProperties.entrySet()) {
            httpConn.addRequestProperty(entry.getKey(), entry.getValue());
        }
    }
    
    /**
     * @desc: 根据HTTP返回结果转换成HttpResponse对象
     *
     * @param urlString
     * @param urlConnection
     * @return
     * @throws IOException
     */
    private HttpResponse makeContent(String urlString, HttpURLConnection urlConnection) throws IOException {
        HttpResponse httpResponser = new HttpResponse();
        try {
            InputStream in = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            StringBuffer temp = new StringBuffer();
            String line = bufferedReader.readLine();
            while (line != null) {
                temp.append(line).append("\r\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();

            httpResponser.url = urlString;

            httpResponser.defaultPort = urlConnection.getURL().getDefaultPort();
            httpResponser.file = urlConnection.getURL().getFile();
            httpResponser.host = urlConnection.getURL().getHost();
            httpResponser.path = urlConnection.getURL().getPath();
            httpResponser.port = urlConnection.getURL().getPort();
            httpResponser.protocol = urlConnection.getURL().getProtocol();
            httpResponser.query = urlConnection.getURL().getQuery();
            httpResponser.ref = urlConnection.getURL().getRef();
            httpResponser.userInfo = urlConnection.getURL().getUserInfo();

            httpResponser.content = new String(temp.toString().getBytes(), contentEncoding);
            httpResponser.contentEncoding = contentEncoding;
            httpResponser.code = urlConnection.getResponseCode();
            httpResponser.message = urlConnection.getResponseMessage();
            httpResponser.contentType = urlConnection.getContentType();
            httpResponser.method = urlConnection.getRequestMethod();
            httpResponser.connectTimeout = urlConnection.getConnectTimeout();
            httpResponser.readTimeout = urlConnection.getReadTimeout();

            return httpResponser;
        } catch (IOException e) {
            log.error(e);
            throw e;
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }
    }

}
