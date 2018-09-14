package sap.demo;

import java.io.File;     
import java.io.FileOutputStream;     
import java.util.Properties;    

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoException;  
import com.sap.conn.jco.ext.DestinationDataProvider;  
import com.sap.conn.jco.JCoDestination;  
import com.sap.conn.jco.JCoDestinationManager;  
import com.sap.conn.jco.JCoFunction;


public class SetRFClink 
{
	
	final String ABAP_AS  = "ABAP_AS_WITH_POOL";   
	final String suffix = "jcoDestination";//�����ļ���׺
	private String host ;//SAP IP
	private String sysnr;//ʵ���
	private String router;
	private String clnt;
	private String user;
	private String passw;
	private String lang;
	private String maxidle;//���idel����
	private String maxactive;//�������
	
	
	public SetRFClink()
	{
	}
	
	//���������Ը�ֵ
	public void setSAPlinkfile()
	{
		host = "10.10.0.2";
		sysnr = "00";
		router = "/H/allwins.3322.org/H/";
		clnt = "300";
		user = "HANDXXX";
		passw = "handxxx";
		lang = "ZH";
		maxidle = "3";
		maxactive = "10";	
		setProperties();
	}
	
	//���������Ը�ֵ
	public void setSAPlinkfile(String host, String sysnr, String router, String clnt, String user, String passw, String lang,String maxidle,String maxactive)
	{
		this.host = host;
		this.sysnr = sysnr;
		this.router = router;
		this.clnt = clnt;
		this.user = user;
		this.passw = passw;
		this.lang = lang;
		this.maxidle = maxidle;
		this.maxactive = maxactive;	
		setProperties();
	}
	
	//������������
	public void setProperties()
	{
		Properties proConn = new Properties();
		proConn.setProperty(DestinationDataProvider.JCO_ASHOST, host);
		proConn.setProperty(DestinationDataProvider.JCO_SYSNR, sysnr);
		proConn.setProperty(DestinationDataProvider.JCO_SAPROUTER, router);
		proConn.setProperty(DestinationDataProvider.JCO_CLIENT, clnt);
		proConn.setProperty(DestinationDataProvider.JCO_USER, user);
		proConn.setProperty(DestinationDataProvider.JCO_PASSWD, passw);
		proConn.setProperty(DestinationDataProvider.JCO_LANG, lang);
		proConn.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, maxidle);
		proConn.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, maxactive); 
		createDataFile(ABAP_AS, suffix, proConn);     
	}
	
	//���������ļ�
	public void createDataFile(String name, String suffix, Properties properties)     
	{     
		File cfg = new File(name+"."+suffix);     
		if(true)     
		{     
			try     
			{     
				FileOutputStream fos = new FileOutputStream(cfg, false);     
				properties.store(fos, "Allwins");     
				fos.close();     
			}     
			catch (Exception e)     
			{     
				throw new RuntimeException("Unable to create the destination file " + cfg.getName(), e);     
			}     
		}     
	}    

	//ɾ�������ļ�
	public void deleteSAPlinkfile()
	{
		String fileName = ABAP_AS+"."+suffix;
		File cfg = new File(fileName);
		if(!cfg.exists())
		{
			System.out.println("File "+ABAP_AS+"."+suffix+"does not exists!");
		}
		else
		{
			cfg.delete();    
		}
	}
	
	//��������RFC
	public void callTest() throws JCoException     
	{
		//��ȡ���������ļ�
		JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS);     
		
		//���ú�����
		JCoFunction function = destination.getRepository().getFunction("STFC_CONNECTION");     
		if(function == null) 
		{
			throw new RuntimeException("STFC_CONNECTION not found in SAP.");     
		}	
		//���ú������
		function.getImportParameterList().setValue("REQUTEXT", "Hello SAP");     
		try
		{
			//ִ�к���
			function.execute(destination);
		}	
		catch(AbapException e) 
		{
			System.out.println(e.toString());
			return;
		}

		System.out.println(" Echo: " + function.getExportParameterList().getString("ECHOTEXT"));
		System.out.println(" Response: " + function.getExportParameterList().getString("RESPTEXT"));
	}

	
	
	
}
