package com.dhr.EDMSExport;


/*
	
	Java server app to export EDMS Documents  to CSV and pathnames to actual files stored. 

 */



import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfActivity;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfDocbaseMap;
import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfProcess;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfQueueItem;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfType;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.client.IDfVirtualDocument;
import com.documentum.fc.client.IDfVirtualDocumentNode;
import com.documentum.fc.client.IDfWorkflow;
import com.documentum.fc.client.IDfWorkflowBuilder;
import com.documentum.fc.client.IDfWorkitem;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfList;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfAttr;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfList;
import com.documentum.fc.common.IDfLoginInfo;
import com.documentum.operations.IDfCancelCheckoutNode;
import com.documentum.operations.IDfCancelCheckoutOperation;
import com.documentum.operations.IDfCheckinNode;
import com.documentum.operations.IDfCheckinOperation;
import com.documentum.operations.IDfCheckoutNode;
import com.documentum.operations.IDfCheckoutOperation;
import com.documentum.operations.IDfExportNode;
import com.documentum.operations.IDfExportOperation;
import com.documentum.operations.IDfImportNode;
import com.documentum.operations.IDfImportOperation;
import com.documentum.operations.IDfOperation;
import com.documentum.operations.IDfOperationError;
import com.documentum.xml.xdql.DfXmlQuery;
import com.documentum.xml.xdql.IDfXmlQuery;


public class DFCUtils {

	IDfSysObject sysObject = null;
	IDfSession idfSession = null;
	IDfSessionManager sessMgr = null;
	

	public DFCUtils(String user, String passwd, String docbase) throws Exception {
		getDfSession(user, passwd, docbase);
	}

	public IDfSession getDfSession(String args1, String args2, String args3) throws Exception {

		IDfLoginInfo login = new DfLoginInfo();
		login.setUser(args1);
		login.setPassword(args2);
		IDfClient client = new DfClient();
		sessMgr = client.newSessionManager();
		sessMgr.setIdentity(args3, login);
		idfSession = sessMgr.getSession(args3);

		if ( idfSession != null )
			System.out.println("Session created successfully");

		return idfSession;
	}

	public void getAllDocbases() throws Exception {

		IDfClient client = DfClient.getLocalClient();
		IDfDocbaseMap docbaseMap = client.getDocbaseMap();
		for ( int i=0;i<docbaseMap.getDocbaseCount();i++) {
			System.out.println("Docbase Name : " + docbaseMap.getDocbaseName(i));
			System.out.println("Docbase Desc : " + docbaseMap.getDocbaseDescription(i));
		}
	}

	
	/*
	 * Gets the the calculated storage pathname 
	 * 
	 */
	public String GetStoragePath(String ObjectID) 
	{

		long ticket;

		String DMRQRY = "select data_ticket, dos_extension, file_system_path, r_docbase_id from dmr_content c, dm_format f, dm_filestore fs, dm_location l, dm_docbase_config dc where any c.parent_id = '" + ObjectID + "' and f.r_object_id = c.format and fs.r_object_id = c.storage_id and l.object_name = fs.root";

		System.out.println(DMRQRY);
		
		try {
			IDfQuery query = new DfQuery();
			query.setDQL(DMRQRY);
			IDfCollection coll = query.execute(idfSession, 0);
			
			System.out.println("Storage path");

			while ( coll.next() ) {
				
				IDfTypedObject typeObject = (IDfTypedObject) coll.getTypedObject();
				
				//String r_object_id = typeObject.getId("r_object_id").toString();
				String dos_extension = typeObject.getString("dos_extension").toString();
				String file_system_path = typeObject.getString("file_system_path").toString();
				String data_ticket = typeObject.getString("data_ticket").toString();
				String r_docbase_id = typeObject.getString("r_docbase_id").toString();
				
				if ( coll != null )
					coll.close();
				
				//System.out.println("Storage Ticket Object ID: " + r_object_id);
				System.out.println("Storage Ticket Data Ticket ID: " + data_ticket);
				
				ticket = Long.parseLong(data_ticket);
				long value =-2^32;
				ticket += 4294967296l;
				
				System.out.println("Big Ticket: : " + ticket);
				String resultHex = Long.toString(ticket, 16);
				
				System.out.println("Big Ticket Hex: : " + resultHex);
				
				// Resultant Hex will be 8000099e
				//                       01234567                         
				String p1 = resultHex.substring(0, 2);
				String p2 = resultHex.substring(2, 4);
				String p3 = resultHex.substring(4, 6);
				String p4 = resultHex.substring(6, 8);
				
				int docbaseId = Integer.parseInt(r_docbase_id);
				
				String actualPath = String.format("%s\\%08x\\%2s\\%2s\\%2s\\%2s.pdf", file_system_path,docbaseId,p1,p2,p3,p4);
				
				System.out.println("Calculated Path: : " + actualPath);
				
				return actualPath;
						
							

			}
		}
		catch (Exception dfe) {
			System.out.println("Error: " + dfe.getMessage());
			DfLogger.error(this, dfe.getMessage(), null, null);
		}


		return "";
	}

	public void ExportSelected() throws Exception {

		String ObjectId;

		IDfQuery query = new DfQuery();
		query.setDQL("select r_object_id,object_name,r_creation_date,i_folder_id from epa_licence where r_object_id ='0900029a80001bf3' ");
		IDfCollection coll = query.execute(idfSession, 0);

		while ( coll.next() ) {

			IDfTypedObject typeObject = (IDfTypedObject) coll.getTypedObject();

			//IDfId folderObject = typeObject.getId("i_folder_id");

			String ObjectID = typeObject.getId("r_object_id").toString();
		
			DfLogger.info("this", "ObjectId", null, null);			
			
			System.out.println("Calling Storage Path");
			String StoragePath = GetStoragePath(ObjectID) ;

			IDfId folderObject = (IDfId) typeObject.getId("i_folder_id");

			IDfSysObject obj = (IDfSysObject)idfSession.getObject(folderObject);

			//IDfTypedObject folderObject = (IDfTypedObject) idfSession.getObject((IDfId) folderObject);


			System.out.println("Object ID " + typeObject.getId("r_object_id"));
			System.out.println("Folder ID " + typeObject.getId("i_folder_id"));

			System.out.println("Pathname " + obj.getString("r_folder_path"));

			System.out.println("Object Name " + typeObject.getString("object_name"));			
			System.out.println("creation date " + typeObject.getString("r_creation_date"));



		}

		if ( coll != null )
			coll.close();
	}


	public void releaseSession() throws Exception {
		sessMgr.release(idfSession);
	}

	

	public static void main(String[] args) throws Exception {



		DFCUtils object = new DFCUtils("dmadmin","Password99","EPADev");


		//DFCUtils object = new DFCUtils(args[0], args[1], args[2]);

		try {

			// To get a list of available docbase
			object.getAllDocbases();
			object.ExportSelected();

		} finally {
			// to release a docbase session
			object.releaseSession();
		}
	}
}

/*

END

 */


