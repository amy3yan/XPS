/*******************************************************************************
 * @package: com.hand.mail
 * @file: MailUtils.java
 * @author: fionn
 * @created: 2016年4月22日
 * @purpose:
 * 
 * @version: 1.0
 * 
 * 
 * Copyright 2016 HAND All rights reserved.
 ******************************************************************************/
package com.xps.utils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;
import javax.mail.internet.MimePartDataSource;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;

import org.apache.log4j.Logger;

/**
 * @author fionn
 *
 */
public class MailUtils {

    private static Logger log = Logger.getLogger(MailUtils.class);
    private String server;
    private String user;
    private String password;
    private String from;
    private Transport transport;
    private MimeMessage message;
    
    /**
     * @desc: 发送邮件，邮件服务器和地址在mailconfig中设置
     * mailConfig - KEY：
     * server：
     * user：
     * password：
     * from：
     * to：
     * cc：
     * bcc：
     *
     * @param mailConfig
     * @param subject
     * @param content
     * @author: fionn
     */
    public static void send(Map<String, String> mailConfig, String subject, String content, Map<String, Object> attachments) {
        MailUtils mailUtils = null;
        try {
            mailUtils = new MailUtils(mailConfig.get("server"), mailConfig.get("user"), 
                    mailConfig.get("password"), mailConfig.get("from"));
            mailUtils.setEmailAddress(mailConfig.get("to"), mailConfig.get("cc"), mailConfig.get("bcc"));
            StringBuffer email = new StringBuffer();
            email.append("<style>body,td,th{background-color:#FFFFFF;font-size:12px;}</style>");
            email.append(content);
            mailUtils.send(subject, email.toString(), attachments);
        } catch (MessagingException e) {
            log.debug("邮件发送失败.....");
            log.error(e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if(mailUtils != null) mailUtils.close();
        }
    }
    
    /**
     * 
     */
    public MailUtils() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param server
     * @param user
     * @param password
     * @param from
     * @throws MessagingException 
     */
    public MailUtils(String server, String user, String password, String from) throws MessagingException {
        super();
        this.server = server;
        this.user = user;
        this.password = password;
        this.from = from;
        init();
    }

    /**
     * 发送邮件
     * @param subject： 邮件主题
     * @param content： 邮件正文html格式
     * @throws MessagingException
     */
    /**
     * @desc: 
     *
     * @param subject:  邮件主题
     * @param content:  邮件正文html格式
     * @param attachments:  附件内容 key->文件名， value->附件对象，支持File，InputStream, URL, MimePart
     * @throws MessagingException
     * @author: fionn
     */
    public void send(String subject, String content, Map<String, Object> attachments) throws MessagingException{
        log.debug("发送邮件：" + message);
        try {
            message.setSubject(MimeUtility.encodeText(subject, "GB2312", "B"));
            Multipart multipart = new MimeMultipart();
            /** 邮件正文 **/
            BodyPart bodypart = new MimeBodyPart();
            bodypart.setContent(content, "text/html;charset=gb2312");
            multipart.addBodyPart(bodypart); // 将含有信件内容的BodyPart加入到MimeMulitipart对象中
            /** 邮件附件 **/
            if(attachments != null && !attachments.isEmpty()) {  
                for(Entry<String, Object> entry : attachments.entrySet()) {  
                    BodyPart attachmentPart = new MimeBodyPart();
                    DataSource source = getAttachmentDataSource(entry.getValue());  
                    attachmentPart.setDataHandler(new DataHandler(source));  
                    log.debug("文件名： " + source.getName());
                    //避免中文乱码的处理
                    attachmentPart.setFileName(MimeUtility.encodeWord(entry.getKey()));
                    multipart.addBodyPart(attachmentPart);  
                }  
            }
            message.setContent(multipart);
            message.saveChanges();
            transport.send(message, message.getAllRecipients());
        } catch (Exception e) {
            e.printStackTrace();
            throw new MessagingException(e.getMessage(), e);
        }
    }
    
    /**
     * 设置Email地址
     * @param to
     * @param cc
     * @param bcc
     * @throws MessagingException
     */
    public void setEmailAddress(String to, String cc, String bcc) throws MessagingException {
        setEmailAddress(message, RecipientType.TO, to);
        setEmailAddress(message, RecipientType.CC, cc);
        setEmailAddress(message, RecipientType.BCC, bcc);
    }

    /**
     * 关闭邮件连接
     */
    public void close() {
        if(transport != null) {
            try {
                transport.close();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * @desc: 邮件附件内容转换
     *
     * @param attachment
     * @return
     * @throws Exception
     * @author: fionn
     */
    private DataSource getAttachmentDataSource(Object attachment) throws Exception{
        DataSource source = null; 
        if(attachment instanceof File) {
            source = new FileDataSource((File)attachment);
        }else if(attachment instanceof InputStream) {
            source = new ByteArrayDataSource((InputStream)attachment, "application/octet-stream");
        }else if(attachment instanceof URL) {
            source = new URLDataSource((URL) attachment);
        }else if(attachment instanceof MimePart) {
            source = new MimePartDataSource((MimePart) attachment);
        }else {
            throw new Exception("附件格式不支持");
        }
        return source;
    }
    
    /**
     * 初始化
     * @throws MessagingException
     */
    private void init() throws MessagingException {
        try {
            Properties prop = new Properties();
            prop.put("mail.smtp.host", server);
            prop.put("mail.smtp.auth", "true");
            Session session = Session.getInstance(prop, new Authenticator() {
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, password);
                }
            });
            session.setDebug(false);
            message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            transport = session.getTransport("smtp");
            transport.connect(server, user, password);
        } catch (Exception e) {
            throw new MessagingException(e.getMessage(), e);
        }
    }
    
    /**
     * 设置Email地址，如发送/抄送
     * @param message
     * @param type
     * @param addresses
     * @throws MessagingException
     */
    private void setEmailAddress(MimeMessage message, Message.RecipientType type, String addresses) throws MessagingException {
        log.debug("邮件地址： " + addresses);
        if(addresses == null || "".equals(addresses)) return;
        String[] emails = addresses.split(",");
        Set<String> emailLst = new HashSet<String>();
        for(String email : emails) {
            if("".equals(email) || email == null) continue;
            emailLst.add(email);
        }
        InternetAddress[] mailAddrs = new InternetAddress[emailLst.size()];
        int i = 0;
        for (String email : emailLst) {
            mailAddrs[i] = new InternetAddress(email);
            i++;
        }
        message.setRecipients(type, mailAddrs);
    }
    
    /**
     * @return the server
     */
    public String getServer() {
        return server;
    }

    /**
     * @param server the server to set
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the from
     */
    public String getFrom() {
        return from;
    }

    /**
     * @param from the from to set
     */
    public void setFrom(String from) {
        this.from = from;
    }
    
    
}
