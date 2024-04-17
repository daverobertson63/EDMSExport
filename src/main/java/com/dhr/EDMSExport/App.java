package com.dhr.EDMSExport;

/*
 * 
 * 
Object ID - r_object_id
Object Name - object_name
Folder - folder_path
EPA Reg 
Import File Name - set_file
Create Date
Version - r_version_label
Current State    
Private
Effective Date
Source System
 * 
 * 
 */

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/*

	Java server app to export EDMS Documents  to CSV and pathnames to actual files stored. 

 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

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
import com.documentum.fc.client.IDfVersionLabels;
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

import jdk.internal.org.jline.utils.Log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App {

	IDfSysObject sysObject = null;
	IDfSession idfSession = null;
	IDfSessionManager sessMgr = null;
	// PropertyConfigurator.configure(App.class.getResourceAsStream("log4j.properties"));
	private static final Logger logger = LogManager.getLogger(App.class);

	// final static Logger logger = Logger.getLogger(App.class);

	public App(String user, String passwd, String docbase) throws Exception {
		getDfSession(user, passwd, docbase);
	}

	public App() throws Exception {
		int i = 1;
	}

	public IDfSession getDfSession(String args1, String args2, String args3) throws Exception {

		IDfLoginInfo login = new DfLoginInfo();
		login.setUser(args1);
		login.setPassword(args2);
		IDfClient client = new DfClient();
		sessMgr = client.newSessionManager();
		sessMgr.setIdentity(args3, login);
		idfSession = sessMgr.getSession(args3);

		if (idfSession != null)
			logger.info("DFC Session created successfully!");

		return idfSession;
	}

	public void getAllDocbases() throws Exception {

		IDfClient client = DfClient.getLocalClient();
		IDfDocbaseMap docbaseMap = client.getDocbaseMap();
		for (int i = 0; i < docbaseMap.getDocbaseCount(); i++) {
			logger.info("Docbase Name : " + docbaseMap.getDocbaseName(i));
			logger.info("Docbase Desc : " + docbaseMap.getDocbaseDescription(i));
		}
	}

	/*
	 * Gets the the calculated storage pathname
	 * 
	 */
	public StoragePath GetStoragePath(String ObjectID) {

		long ticket;

		StoragePath sp = new StoragePath();

		String DMRQRY = "select data_ticket, set_file, dos_extension, file_system_path, r_docbase_id from dmr_content c, dm_format f, dm_filestore fs, dm_location l, dm_docbase_config dc where any c.parent_id = '"
				+ ObjectID
				+ "' and f.r_object_id = c.format and fs.r_object_id = c.storage_id and l.object_name = fs.root";

		logger.debug(DMRQRY);

		try {
			IDfQuery query = new DfQuery();
			query.setDQL(DMRQRY);
			IDfCollection coll = query.execute(idfSession, 0);

			logger.debug("Storage path");

			while (coll.next()) {

				IDfTypedObject typeObject = (IDfTypedObject) coll.getTypedObject();

				// String r_object_id = typeObject.getId("r_object_id").toString();
				String dos_extension = typeObject.getString("dos_extension").toString();
				String file_system_path = typeObject.getString("file_system_path").toString();
				String data_ticket = typeObject.getString("data_ticket").toString();
				String r_docbase_id = typeObject.getString("r_docbase_id").toString();
				String set_file = typeObject.getString("set_file").toString();

				if (coll != null)
					coll.close();

				// System.out.println("Storage Ticket Object ID: " + r_object_id);
				logger.debug("Storage Ticket Data Ticket ID: " + data_ticket);

				ticket = Long.parseLong(data_ticket);
				long value = -2 ^ 32;
				ticket += 4294967296l;

				logger.debug("Big Ticket: : " + ticket);
				String resultHex = Long.toString(ticket, 16);

				logger.debug("Big Ticket Hex: : " + resultHex);

				// Resultant Hex will be 8000099e
				// 01234567
				String p1 = resultHex.substring(0, 2);
				String p2 = resultHex.substring(2, 4);
				String p3 = resultHex.substring(4, 6);
				String p4 = resultHex.substring(6, 8);

				int docbaseId = Integer.parseInt(r_docbase_id);

				String actualPath = String.format("%s\\%08x\\%2s\\%2s\\%2s\\%2s.pdf", file_system_path, docbaseId, p1,
						p2, p3, p4);

				logger.debug("Calculated Path: : " + actualPath);

				sp.setDataTicket(ticket);
				sp.setDocbaseID(r_docbase_id);
				sp.setDosExtension(dos_extension);
				sp.setOriginalName(set_file);
				sp.setStoragePath(actualPath);

				return sp;

			}
		} catch (Exception dfe) {
			logger.error("Error: " + dfe.getMessage());
			// DfLogger.error(this, dfe.getMessage(), null, null);
		}

		return null;
	}

	public void ExportSelected() throws Exception {

		String ObjectId;

		Properties loadProps = new Properties();
		loadProps.loadFromXML(new FileInputStream("settings.xml"));

		String DQLQuery = loadProps.getProperty("query");
		String header = loadProps.getProperty("header");
		String exportFile = loadProps.getProperty("export");

		logger.info("Query from settings {}: ", DQLQuery);
		logger.info("Header from settings {}: ", header);
		logger.info("Export File from settings {}: ", exportFile);

		IDfQuery query = new DfQuery();
		query.setDQL(DQLQuery);

		IDfCollection coll = query.execute(idfSession, 0);
		String r_object_id;
		String folder_r_object_id;
		String folder_path;
		String reg_no;
		String object_name;
		String r_creation_date;
		String epa_doctype;
		String epa_subtype;
		String effective_date;
		String r_version_label;
		String r_current_version;
		String epa_controlled;
		String epa_publish_status;
		String epa_isprivate;
		String reg_number;

		BufferedWriter writer = Files.newBufferedWriter(Paths.get(exportFile));

		String[] headerArray = header.split(",", -1);

		final CSVFormat csvFormat = CSVFormat.Builder.create().setHeader(headerArray).setAllowMissingColumnNames(true)
				.build();

		CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat);

		while (coll.next()) {

			IDfTypedObject typeObject = (IDfTypedObject) coll.getTypedObject();
			IDfId id = coll.getId("r_object_id");
			IDfSysObject objRoot = (IDfSysObject) idfSession.getObject(id);

			// IDfId folderObject = typeObject.getId("i_folder_id");

			String ObjectID = typeObject.getId("r_object_id").toString();

			// DfLogger.info("this", "ObjectId", null, null);

			logger.debug("Calling Storage Path calculation path");

			StoragePath sp = GetStoragePath(ObjectID);

			if (sp == null) {
				logger.debug("SP Is null: " + ObjectID);
				sp = new StoragePath();
				sp.setDocbaseID("666");
				sp.setDataTicket(0L);

				sp.setDosExtension("pdf");
				sp.setOriginalName("Not Found");
				sp.setStoragePath("Not Found");

			}

			// Get the folder pathname
			IDfId folderObject = (IDfId) typeObject.getId("i_folder_id");

			IDfSysObject obj = (IDfSysObject) idfSession.getObject(folderObject);

			// Get the attributes required for export.
			reg_no = obj.getString("object_name");
			folder_r_object_id = obj.getString("r_object_id");

			r_object_id = typeObject.getString("r_object_id");
			object_name = typeObject.getString("object_name");
			r_creation_date = typeObject.getString("r_creation_date");
			effective_date = typeObject.getString("effective_date");
			epa_doctype = typeObject.getString("epa_doctype");
			epa_subtype = typeObject.getString("epa_subtype");
			epa_publish_status = typeObject.getString("epa_publish_status");
			epa_controlled = typeObject.getString("epa_controlled");
			epa_isprivate = typeObject.getString("isprivate");

			IDfVersionLabels labels = objRoot.getVersionLabels();

			int count = labels.getVersionLabelCount();

			// Version numbers
			r_current_version = labels.getVersionLabel(0);
			r_version_label = labels.getVersionLabel(1);

			for (int index = 0; index < count; index++) {

				logger.debug("Label: " + labels.getVersionLabel(index));

				// Do your thing - system.out or whatever

			}

			folder_path = obj.getString("r_folder_path");

			RegInfo rt = App.getRegfromFolder(folder_path);
			
			// Get the source folder names from the folder path
			String EPASource = App.getSourcefromFolder(folder_path);
			String EPASourceCode = App.getSourcefromFolder(folder_path);

			logger.debug("Object ID: " + r_object_id);
			logger.debug("Folder ID: " + folder_r_object_id);
			logger.debug("Folder Pathname: " + folder_path);
			
			logger.debug("Object Name: " + object_name);
			logger.debug("creation date: " + r_creation_date);
			logger.debug("Doctype: " + epa_doctype);
			logger.debug("Sub Doctype: " + epa_subtype);

			csvPrinter.printRecord(
					r_object_id, 
					object_name, 
					rt.RegNumber, 
					rt.EPAType, 
					epa_doctype, 
					epa_subtype,
					epa_isprivate, 
					epa_controlled,					
					epa_publish_status, 
					r_creation_date,
					effective_date, 
					r_version_label,
					r_current_version,
					EPASource,
					EPASourceCode,
					folder_path, 
					sp.OriginalName, 
					sp.StoragePath);

		}
		csvPrinter.flush();
		csvPrinter.close();

		if (coll != null)
			coll.close();
	}

	public void releaseSession() throws Exception {
		sessMgr.release(idfSession);
	}

	public static String getCategoryfromFolder(String FolderName)

	{
		return "";
	}

	/*
	 * Get the Source of the document from the folder name This uses a map
	 * EPARegMaps.EPASourceMap as the way to determine the code used in the document
	 * encoding name and also the full name of the source
	 * 
	 * The last part of the foldername will always be the source name
	 * 
	 */
	public static String getSourcefromFolder(String FolderName)

	{
		for (Map.Entry<String, String> item : EPARegMaps.EPASourceMap.entrySet()) {
			String key = item.getKey();
			String value = item.getValue();

			if (FolderName.contains(key))
				return key;

		}

		return "NO SOURCE DEFINED";
	}

	/*
	 * Get the Source of the document from the folder name This uses a map
	 * EPARegMaps.EPASourceMap as the way to determine the code used in the document
	 * encoding name and also the full name of the source
	 * 
	 * The last part of the foldername will always be the source name
	 * 
	 */
	public static String getSourceCodefromFolder(String FolderName)

	{
		for (Map.Entry<String, String> item : EPARegMaps.EPASourceMap.entrySet()) {
			String key = item.getKey();
			String value = item.getValue();

			if (FolderName.contains(key))
				return value;

		}

		return "NO SOURCE DEFINED";
	}

	/*
	 * Get the reg number from the folderpath
	 * 
	 * Patterns are:
	 * 
	 * 
	 * 
	 * APA - EXXXX-YY Article11 - XXXX Article27 - ART27-XXXX Article28 - ART28-XXXX
	 * COR - RXXXXX-YY DaS - S0000-YY Emissions - GHG012-06 Extractive- QSXXXX-YY
	 * GMO - GXXXX-YY Historic L HXXXX-YY IPPC - PXXXX-YY VOC - VXXXX-YY Waste -
	 * WXXXX-YY WWC - AXXXX-YY WWD - DXXXX-YY EU-BP - EU-BP-XXXX EU-SOW -
	 * EU-SOW-XXXX N-BP N-BP-XXXX N-EoW N-EOW-XXXX SC-BP - SC-BP-XXXX SC-EoW
	 * SC-EOW-XXXX
	 */
	public static RegInfo getRegfromFolder(String FolderName)

	{

		RegInfo rt = new RegInfo();

		Pattern pattern = null;
		Matcher matcher = null;

		// Pattern 1 IPPC
		// Compile regular expression PXXXX-YY
		pattern = Pattern.compile("P\\d\\d\\d\\d-\\d\\d", Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(FolderName);

		if (matcher.find()) {
			logger.debug("Found matching pattern 1 - IPPC");
			rt.EPAType = "IPPC";
			rt.RegNumber = matcher.group(0);
			return rt;
		}

		// Waste
		pattern = Pattern.compile("W\\d\\d\\d\\d-\\d\\d", Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(FolderName);

		if (matcher.find()) {
			logger.debug("Found matching pattern 2 - Waste");
			rt.EPAType = "Waste";
			rt.RegNumber = matcher.group(0);
			return rt;
		}

		// APA
		pattern = Pattern.compile("E\\d\\d\\d\\d-\\d\\d", Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(FolderName);

		if (matcher.find()) {
			logger.debug("Found matching pattern 3 - APA");
			rt.EPAType = "APA";
			rt.RegNumber = matcher.group(0);
			return rt;
		}

		// Article 11 - four numbers
		pattern = Pattern.compile("Article11/\\d\\d\\d\\d/", Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(FolderName);

		if (matcher.find()) {
			logger.debug("Found matching pattern 4 - Article 11");
			String a11Number = matcher.group(0).substring(10, 14);
			rt.EPAType = "Article 11";

			rt.RegNumber = a11Number;
			return rt;
		}

		// Article 27 - complex pattern
		pattern = Pattern.compile("ART27-\\d\\d\\d\\d", Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(FolderName);

		if (matcher.find()) {
			logger.debug("Found matching pattern 5 - Article 27");

			rt.EPAType = "Article 27";
			rt.RegNumber = matcher.group(0);

			return rt;
		}

		// Article 28 - complex pattern
		pattern = Pattern.compile("ART28-\\d\\d\\d\\d", Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(FolderName);

		if (matcher.find()) {
			logger.debug("Found matching pattern 6 - Article 28");

			rt.EPAType = "Article 28";
			rt.RegNumber = matcher.group(0);

			return rt;
		}
		// COR with 5 numbers RXXXXX-YY
		pattern = Pattern.compile("R\\d\\d\\d\\d\\d-\\d\\d", Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(FolderName);

		if (matcher.find()) {
			logger.debug("Found matching pattern 7 - COR");

			rt.EPAType = "COR";
			rt.RegNumber = matcher.group(0);

			return rt;
		}

		// Dumping at Sea
		pattern = Pattern.compile("/S\\d\\d\\d\\d-\\d\\d", Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(FolderName);

		if (matcher.find()) {
			logger.debug("Found matching pattern 8 - DaS");
			rt.EPAType = "DaS";
			String reg = matcher.group(0).substring(1, 9);
			rt.EPAType = "DaS";
			rt.RegNumber = reg;
			return rt;
		}

		// Emmissions Trading
		pattern = Pattern.compile("GHG\\d\\d\\d-\\d\\d", Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(FolderName);

		if (matcher.find()) {
			logger.debug("Found matching pattern 9 - ET");
			rt.EPAType = "Emissions Trading";
			rt.RegNumber = matcher.group(0);
			return rt;
		}

		// Extractive Industries
		pattern = Pattern.compile("QS\\d\\d\\d\\d-\\d\\d", Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(FolderName);

		if (matcher.find()) {
			logger.debug("Found matching pattern 10 - EI");
			rt.EPAType = "Extractive Industries";
			rt.RegNumber = matcher.group(0);
			return rt;
		}

		// GMO
		pattern = Pattern.compile("/G\\d\\d\\d\\d-\\d\\d", Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(FolderName);

		if (matcher.find()) {
			logger.debug("Found matching pattern 11 - GMO");
			rt.EPAType = "GMO";
			String reg = matcher.group(0).substring(1, 9);
			rt.RegNumber = reg;
			return rt;
		}

		// Historic Landfill
		pattern = Pattern.compile("/H\\d\\d\\d\\d-\\d\\d", Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(FolderName);

		if (matcher.find()) {
			logger.debug("Found matching pattern 12 - HL");
			rt.EPAType = "Historic Landfill";
			String reg = matcher.group(0).substring(1, 9);
			rt.RegNumber = reg;
			return rt;
		}

		// VOC
		pattern = Pattern.compile("/V\\d\\d\\d\\d-\\d\\d", Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(FolderName);

		if (matcher.find()) {
			logger.debug("Found matching pattern 13 - HL");
			rt.EPAType = "VOC";
			String reg = matcher.group(0).substring(1, 9);
			rt.RegNumber = reg;
			return rt;
		}

		// WWC
		pattern = Pattern.compile("/A\\d\\d\\d\\d-\\d\\d", Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(FolderName);

		if (matcher.find()) {
			logger.debug("Found matching pattern 14 - WWC");
			rt.EPAType = "WWC";
			String reg = matcher.group(0).substring(1, 9);
			rt.RegNumber = reg;
			return rt;

		}

		// WWD
		pattern = Pattern.compile("/D\\d\\d\\d\\d-\\d\\d", Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(FolderName);

		if (matcher.find()) {
			logger.debug("Found matching pattern 15 - WWD");
			rt.EPAType = "WWD";
			String reg = matcher.group(0).substring(1, 9);
			rt.RegNumber = reg;
			return rt;
		}
		/*
		 * EU-BP - EU-BP-XXXX EU-SOW - EU-SOW-XXXX N-BP N-BP-XXXX N-EoW N-EOW-XXXX SC-BP
		 * - SC-BP-XXXX SC-EoW SC-EOW-XXXX
		 */
		// EU-BP-0001
		pattern = Pattern.compile("EU-BP-\\d\\d\\d\\d", Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(FolderName);

		if (matcher.find()) {
			logger.debug("Found matching pattern 16 - EU-BP");
			rt.EPAType = "EU-BP";
			String reg = matcher.group(0);
			rt.RegNumber = reg;
			return rt;
		}

		// EU-SOW-0001
		pattern = Pattern.compile("EU-SOW-\\d\\d\\d\\d", Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(FolderName);

		if (matcher.find()) {
			logger.debug("Found matching pattern 17 - EU-SOW");
			rt.EPAType = "EU-SoW";
			String reg = matcher.group(0);
			rt.RegNumber = reg;
			return rt;
		}

		// N-BP-0001
		pattern = Pattern.compile("N-BP-\\d\\d\\d\\d", Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(FolderName);

		if (matcher.find()) {
			logger.debug("Found matching pattern 18 - N-BP");
			rt.EPAType = "N-BP";
			String reg = matcher.group(0);
			rt.RegNumber = reg;
			return rt;
		}

		// N-SOW-0001
		pattern = Pattern.compile("N-SOW-\\d\\d\\d\\d", Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(FolderName);

		if (matcher.find()) {
			logger.debug("Found matching pattern 19 - N-SOW");
			rt.EPAType = "N-SoW";
			String reg = matcher.group(0);
			rt.RegNumber = reg;
			return rt;
		}

		// SC-BP-0001
		pattern = Pattern.compile("SC-BP-\\d\\d\\d\\d", Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(FolderName);

		if (matcher.find()) {
			logger.debug("Found matching pattern 20 - SC-BP");
			rt.EPAType = "SC-BP";
			String reg = matcher.group(0);
			rt.RegNumber = reg;
			return rt;
		}

		// N-SOW-0001
		pattern = Pattern.compile("SC-SOW-\\d\\d\\d\\d", Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(FolderName);

		if (matcher.find()) {
			logger.debug("Found matching pattern 21 - SC-SOW");
			rt.EPAType = "SC-SoW";
			String reg = matcher.group(0);
			rt.RegNumber = reg;
			return rt;
		}

		return rt;
	}

	public static void main(String[] args) throws Exception {

		Properties loadProps = new Properties();
		loadProps.loadFromXML(new FileInputStream("settings.xml"));
		String docbase = loadProps.getProperty("docbase");
		String username = loadProps.getProperty("username");
		String password = loadProps.getProperty("password");
		String dfc_properties = loadProps.getProperty("dfc");

		// Test cases
		// String result = object.getRegfromFolder("Cabinets/Licence
		// Applications/IPPC/P0001-01/Applicant");

		System.setProperty("dfc.properties.file", dfc_properties);

		App object = new App(username, password, docbase);

		String Source = EPARegMaps.EPASourceMap.get("Site Closure Documents/EPA").toString();

		logger.info("Sourtce Maps: " + Source);

		// App object = new App(args[0], args[1], args[2]);

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
 * 
 * END
 * 
 */
