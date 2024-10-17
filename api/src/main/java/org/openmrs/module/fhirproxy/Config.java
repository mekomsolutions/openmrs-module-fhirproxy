package org.openmrs.module.fhirproxy;

import java.util.List;

import lombok.Getter;

public class Config {
	
	@Getter
	private boolean externalApiEnabled;
	
	@Getter
	private String baseUrl;
	
	@Getter
	private String username;
	
	@Getter
	private String password;
	
	@Getter
	private List<String> chargeItemPrivileges;
	
	@Getter
	private List<String> inventoryItemPrivileges;
	
	public Config(boolean externalApiEnabled, String baseUrl, String username, String password,
	    List<String> chargeItemPrivileges, List<String> inventoryItemPrivileges) {
		this.externalApiEnabled = externalApiEnabled;
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
		this.chargeItemPrivileges = chargeItemPrivileges;
		this.inventoryItemPrivileges = inventoryItemPrivileges;
	}
	
}
