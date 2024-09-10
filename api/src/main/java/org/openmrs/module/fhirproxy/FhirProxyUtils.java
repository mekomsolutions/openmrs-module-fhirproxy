package org.openmrs.module.fhirproxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.openmrs.util.OpenmrsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
			Properties props = new Properties();
			props.load(new FileInputStream(new File(configDir, Constants.CONFIG_FILE)));
			final boolean enabled = Boolean.valueOf(props.getProperty("external.api.enabled", "false"));
			final String baseUrl = props.getProperty("base.url");
			final String username = props.getProperty("username");
			final String password = props.getProperty("password");
			config = new Config(enabled, baseUrl, username, password);
		}
		
		return config;
	}
	
}
