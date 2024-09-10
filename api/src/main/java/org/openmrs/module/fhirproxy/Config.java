package org.openmrs.module.fhirproxy;

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
	
	public Config(boolean externalApiEnabled, String baseUrl, String username, String password) {
		this.externalApiEnabled = externalApiEnabled;
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
	}
	
}
