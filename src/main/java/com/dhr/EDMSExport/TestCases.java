package com.dhr.EDMSExport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestCases {
	
	 private static final Logger logger = LogManager.getLogger(TestCases.class);

	public TestCases() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		App app = new App();
		
		logger.info("Logger active for test cases");
		
		//String Source = EPARegMaps.EPASourceMap.get("Site Closure Documents/EPA").toString();
		
		RegInfo result = app.getRegfromFolder("Cabinets/Licence Applications/IPPC/P0001-01/Applicant/P0001-01 - the file.pdf");		
		logger.info("Reg Type is: {} Reg  Number is: {}",result.EPAType ,result.RegNumber);
		
		result = app.getRegfromFolder("Cabinets/Licence Applications/Article11/0021/Applicant");	
		logger.info("Reg Type is: {} Reg  Number is: {}",result.EPAType ,result.RegNumber);

		result = app.getRegfromFolder("Cabinets/Licence Applications/APA/E0001-01/Applicant");	
		logger.info("Reg Type is: {} Reg  Number is: {}",result.EPAType ,result.RegNumber);

		
		result = app.getRegfromFolder("Cabinets/Licence Applications/Waste/W0001-01/Applicant");	
		logger.info("Reg Type is: {} Reg  Number is: {}",result.EPAType ,result.RegNumber);

		result = app.getRegfromFolder("Cabinets/Licence Applications/Article27/ART27-1234/Applicant");	
		logger.info("Reg Type is: {} Reg  Number is: {}",result.EPAType ,result.RegNumber);

		result = app.getRegfromFolder("Cabinets/Licence Applications/Article28/ART28-1234/Applicant");	
		logger.info("Reg Type is: {} Reg  Number is: {}",result.EPAType ,result.RegNumber);
		
		result = app.getRegfromFolder("Cabinets/icence Applications/COR/Carlow County Council/R00617-01/Applicant");	
		logger.info("Reg Type is: {} Reg  Number is: {}",result.EPAType ,result.RegNumber);
		
		result = app.getRegfromFolder("Cabinets/Licence Applications/DaS/S0001-01/Applicant");	
		logger.info("Reg Type is: {} Reg  Number is: {}",result.EPAType ,result.RegNumber);

		result = App.getRegfromFolder("Cabinets/Licence Applications/Emissions Trading/GHG012-06/Applicant");	
		logger.info("Reg Type is: {} Reg  Number is: {}",result.EPAType ,result.RegNumber);

		result = app.getRegfromFolder("Cabinets/Licence Applications/Extractive Industries/QS0001-01/Applicant");	
		logger.info("Reg Type is: {} Reg  Number is: {}",result.EPAType ,result.RegNumber);
		
		result = app.getRegfromFolder("Cabinets/Licence Applications/GMO/G0001-01/Applicant");	
		logger.info("Reg Type is: {} Reg  Number is: {}",result.EPAType ,result.RegNumber);

		result = app.getRegfromFolder("Cabinets/Licence Applications/Historic Landfill/H0001-01/Applicant");	
		logger.info("Reg Type is: {} Reg  Number is: {}",result.EPAType ,result.RegNumber);

		result = app.getRegfromFolder("Cabinets/Licence Applications/VOC/V0001-01/Applicant");	
		logger.info("Reg Type is: {} Reg  Number is: {}",result.EPAType ,result.RegNumber);
		
		result = app.getRegfromFolder("Cabinets/Licence Applications/WWC/A0001-01/Applicant");	
		logger.info("Reg Type is: {} Reg  Number is: {}",result.EPAType ,result.RegNumber);
		
		result = app.getRegfromFolder("Cabinets/Licence Applications/WWD/D0001-01/Applicant");	
		logger.info("Reg Type is: {} Reg  Number is: {}",result.EPAType ,result.RegNumber);

		result = app.getRegfromFolder("Cabinets/Licence Applications/EU-BP/EU-BP-0023/Applicant");	
		logger.info("Reg Type is: {} Reg  Number is: {}",result.EPAType ,result.RegNumber);

		result = app.getRegfromFolder("Cabinets/Licence Applications/EU-SoW/EU-SOW-0023/Applicant");	
		logger.info("Reg Type is: {} Reg  Number is: {}",result.EPAType ,result.RegNumber);

		result = app.getRegfromFolder("Cabinets/Licence Applications/N-BP/N-BP-0023/Applicant");	
		logger.info("Reg Type is: {} Reg  Number is: {}",result.EPAType ,result.RegNumber);

		result = app.getRegfromFolder("Cabinets/Licence Applications/N-SoW/N-SOW-0023/Applicant");	
		logger.info("Reg Type is: {} Reg  Number is: {}",result.EPAType ,result.RegNumber);

		result = app.getRegfromFolder("Cabinets/Licence Applications/SC-BP/SC-BP-0023/Applicant");	
		logger.info("Reg Type is: {} Reg  Number is: {}",result.EPAType ,result.RegNumber);

		result = app.getRegfromFolder("Cabinets/Licence Applications/SC-SoW/SC-SOW-0023/Applicant");	
		logger.info("Reg Type is: {} Reg  Number is: {}",result.EPAType ,result.RegNumber);
		
		String source =App.getSourcefromFolder("Cabinets/Licence Applications/SC-SoW/SC-SOW-0023/Applicant");	
		logger.info("Source Name is: {}:" , source);
		
		source =App.getSourceCodefromFolder("Cabinets/Licence Applications/SC-SoW/SC-SOW-0023/Applicant");	
		logger.info("Source Code is: {}:" , source);
		

	}

}

