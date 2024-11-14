package org.openmrs.module.fhirproxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides utility methods for the module.
 */
public class FhirProxyUtils {
	
	private static final Logger LOG = LoggerFactory.getLogger(FhirProxyUtils.class);
	
	private static final Pattern ENV_PATTERN = Pattern.compile("\\$\\{([^}]+)}");
	
	private static Config config;
	
	/**
	 * Loads and returns the module configuration.
	 *
	 * @return the {@link Config instance}
	 * @throws IOException
	 */
	public static Config getConfig() throws IOException {
		if (config == null) {
			LOG.info("Loading config for FHIR proxy module");
			
			File configDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(Constants.MODULE_ID);
			Properties props = new Properties();
			props.load(Files.newInputStream(new File(configDir, Constants.CONFIG_FILE).toPath()));
			// Resolve environment variables in the properties
			props.forEach((key, value) -> props.setProperty((String) key, resolveEnvVariables((String) value)));
			
			final boolean enabled = Boolean.parseBoolean(props.getProperty("external.api.enabled", "false"));
			final String baseUrl = props.getProperty("base.url");
			final String username = props.getProperty("username");
			final String password = props.getProperty("password");
			List<String> chargeItemPrivileges = loadPrivileges(Constants.GP_PRIV_CHARGE_ITEM);
			List<String> inventoryPrivileges = loadPrivileges(Constants.GP_PRIV_INVENTORY);
			config = new Config(enabled, baseUrl, username, password, chargeItemPrivileges, inventoryPrivileges);
		}
		
		return config;
	}
	
	/**
	 * Loads and verifies the privileges defined by the specified global property name
	 *
	 * @return list of privileges names
	 */
	protected static List<String> loadPrivileges(String gpName) {
		List<String> privileges = new ArrayList<>();
		AdministrationService adminService = Context.getAdministrationService();
		final String privStr = adminService.getGlobalProperty(gpName);
		if (StringUtils.isNotBlank(privStr)) {
			UserService userService = Context.getUserService();
			String[] privilegeNames = StringUtils.split(privStr.trim(), ",");
			Arrays.stream(privilegeNames).map(p -> p.trim()).forEach(name -> {
				if (userService.getPrivilege(name) == null) {
					throw new APIException("No privilege found with name " + name);
				}
				privileges.add(name);
			});
		}
		
		return privileges;
	}
	
	/**
	 * Resolves environment variables in the given string.
	 * <p>
	 * This method searches for placeholders in the format ${ENV_VAR} within the input string and
	 * replaces them with the corresponding environment variable values. It first checks system
	 * properties and then environment variables for the value of each placeholder.
	 *
	 * @param value the input string containing placeholders for environment variables
	 * @return the input string with all environment variables resolved
	 */
	public static String resolveEnvVariables(String value) {
		Matcher matcher = ENV_PATTERN.matcher(value);
		StringBuffer buffer = new StringBuffer();
		while (matcher.find()) {
			String envVar = matcher.group(1);
			String envValue = System.getProperty(envVar, System.getenv(envVar));
			matcher.appendReplacement(buffer, Matcher.quoteReplacement(envValue != null ? envValue : matcher.group(0)));
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}
}
