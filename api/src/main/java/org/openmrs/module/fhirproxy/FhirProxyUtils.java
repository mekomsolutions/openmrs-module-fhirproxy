package org.openmrs.module.fhirproxy;

import java.io.File;
import java.io.IOException;

import org.openmrs.util.OpenmrsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Provides utility methods for the module.
 */
public class FhirProxyUtils {
	
	private static final Logger LOG = LoggerFactory.getLogger(FhirProxyUtils.class);
	
	private static Config config;
	
	/**
	 * Loads and returns the module configuration .
	 *
	 * @return the {@link Config instance}
	 * @throws IOException
	 */
	public static final Config getConfig() throws IOException {
		if (config == null) {
			LOG.info("Loading config for FHIR proxy module");
			
			File configDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(Constants.MODULE_ID);
			File configFile = new File(configDir, Constants.CONFIG_FILE);
			config = new ObjectMapper().readValue(configFile, Config.class);
		}
		
		return config;
	}
	
}
