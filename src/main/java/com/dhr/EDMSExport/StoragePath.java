package com.dhr.EDMSExport;

public class StoragePath {

	public StoragePath() {
		// TODO Auto-generated constructor stub
	}

	String OriginalName;
	String StoragePath;
	Long DataTicket;
	String DocbaseID;
	String DosExtension;
	
	
	public String getDosExtension() {
		return DosExtension;
	}
	public void setDosExtension(String dosExtension) {
		DosExtension = dosExtension;
	}
	public String getOriginalName() {
		return OriginalName;
	}
	public void setOriginalName(String originalName) {
		OriginalName = originalName;
	}
	public String getStoragePath() {
		return StoragePath;
	}
	public void setStoragePath(String storagePath) {
		StoragePath = storagePath;
	}
	public Long getDataTicket() {
		return DataTicket;
	}
	public void setDataTicket(Long dataTicket) {
		DataTicket = dataTicket;
	}
	public String getDocbaseID() {
		return DocbaseID;
	}
	public void setDocbaseID(String docbaseID) {
		DocbaseID = docbaseID;
	}
	
	

	
}
