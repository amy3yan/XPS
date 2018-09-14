package sap.demo;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

public class CallRFC {

	private final String conConfig  = "config/ABAP_AS_WITH_POOL";  // �����ļ� 
	private final String sFunctionName = "Z_IN_BOUND_INVOICE";
	private final String setInvoicetableName = "IT_INVOICE";
	private final String getInvoicetableName = "ET_MSG";
	
		public CallRFC(){
			
		}
	
		
		public void testRFC() throws JCoException{
			//��ȡ�����ļ�
			JCoDestination sDestination = JCoDestinationManager.getDestination(conConfig); 	
			//���ú�����
			JCoFunction sFunction = sDestination.getRepository().getFunction(sFunctionName); 
			
			//����
			JCoTable setInvoices = sFunction.getTableParameterList().getTable(setInvoicetableName);
			
			/*�������
			 	SAP��Ʊ��   		��˾		��˰��
			 	90000013	1010	G90000013_4
				90000014	1010	G90000013_4
			�˴�Ϊһ��һ����ӽ�ȥ������ʵ�ʵ�ʱ�򣬿�ѭ����ֵ������
			*/
			setInvoices.appendRow();
			setInvoices.setValue("VBELN", "90000013" );
			setInvoices.setValue("BUKRS", "1010" );
			setInvoices.setValue("GTNO", "G90000013_4" );	

			setInvoices.appendRow();
			setInvoices.setValue("VBELN", "90000014" );
			setInvoices.setValue("BUKRS", "1010" );
			setInvoices.setValue("GTNO", "G90000013_4" );	
		
			
			
			//ȡ����Ϣ
			JCoTable getMsg = sFunction.getTableParameterList().getTable(getInvoicetableName);
			try{
				//ִ�к���
				sFunction.execute(sDestination);
				for(int i = 0; i < getMsg.getNumRows(); i++){
					getMsg.setRow(i);
					System.out.println(getMsg.getString("MESSAGE"));
				}
				
			}
			catch(AbapException e) {
					//����
			}
		}
				
		
}
