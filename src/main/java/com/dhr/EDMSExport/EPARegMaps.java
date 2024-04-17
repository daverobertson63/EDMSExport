package com.dhr.EDMSExport;

import java.util.HashMap;
import java.util.Map;

public final class EPARegMaps {

	public static Map<String, String> EPASourceMap = new HashMap<String,String>();

	/*
	 * EPASourceMap.put("Site Closure Documents/EPA", "SE");
	 * EPASourceMap.put("Site Closure Documents/Licensee", "SL") ;
	 * EPASourceMap.put("Enforcement/EPA", "EE") ;
	 * EPASourceMap.put("Enforcement/Licensee", "EL") ;
	 * 
	 * 
	 * EPASourceMap.put("Applicant", "AX"); EPASourceMap.put("Third Party", "TX");
	 * EPASourceMap.put("Misc", "MX") ;
	 * 
	 * EPASourceMap.put("Reports from Facilities", "OA") ;
	 * 
	 * EPASourceMap.put("Site Closure Documents", "SC") ;
	 * EPASourceMap.put("Local Authority", "LA"); EPASourceMap.put("EPA", "EX");
	 * EPASourceMap.put("Internal", "IX");
	 * 
	 */

	// static initializer
	static {
		System.out.println("EPA Maps - static initializer called");

		EPASourceMap.put("Site Closure Documents/EPA", "SE");
		EPASourceMap.put("Site Closure Documents/Licensee", "SL");
		EPASourceMap.put("Enforcement/EPA", "EE");
		EPASourceMap.put("Enforcement/Licensee", "EL");

		EPASourceMap.put("Applicant", "AX");
		EPASourceMap.put("Third Party", "TX");
		EPASourceMap.put("Misc", "MX");

		EPASourceMap.put("Reports from Facilities", "OA");

		EPASourceMap.put("Site Closure Documents", "SC");
		EPASourceMap.put("Local Authority", "LA");
		EPASourceMap.put("EPA", "EX");
		EPASourceMap.put("Internal", "IX");

	}

	public EPARegMaps() {

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
