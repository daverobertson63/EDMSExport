package com.dhr.EDMSExport;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EPAProperties  {
	
	private static final Logger logger = LogManager.getLogger(App.class);
	
	public EPAProperties() throws Exception {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args) throws Exception {
	{
	   // Save Settings
   /*
		Properties saveProps = new Properties();
    saveProps.setProperty("path1", "/somethingpath1");
    saveProps.setProperty("path2", "/somethingpath2");
    saveProps.storeToXML(new FileOutputStream("settings.xml"), "");
    */

    // Load Settings
    Properties loadProps = new Properties();
    loadProps.loadFromXML(new FileInputStream("settings.xml"));
    String docbase = loadProps.getProperty("docbase");
    String username = loadProps.getProperty("username");
    String password = loadProps.getProperty("password");
    
    
    
    
	}
	}

}
