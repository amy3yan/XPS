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


/**
 * @author amy
 *
 */
public class HttpResponse {

    String url;
    String file;
    String host;
    String path;
    String protocol;
    String query;
    String ref;
    String userInfo;
    String contentEncoding;
    String content;
    String contentType;
    String message;
    String method;
    int defaultPort;
    int port;
    int code;
    int connectTimeout;
    int readTimeout;

    /**
     * @desc: 
     *
     *
     */
    public HttpResponse() {
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return the file
     */
    public String getFile() {
        return file;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @return the protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * @return the ref
     */
    public String getRef() {
        return ref;
    }

    /**
     * @return the userInfo
     */
    public String getUserInfo() {
        return userInfo;
    }

    /**
     * @return the contentEncoding
     */
    public String getContentEncoding() {
        return contentEncoding;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the method
     */
    public String getMethod() {
        return method;
    }

    /**
     * @return the defaultPort
     */
    public int getDefaultPort() {
        return defaultPort;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * @return the connectTimeout
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * @return the readTimeout
     */
    public int getReadTimeout() {
        return readTimeout;
    }
    
    
}
