package org.openmrs.module.fhirproxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.openmrs.module.fhirproxy.Constants.GP_PRIV_CHARGE_ITEM;
import static org.openmrs.module.fhirproxy.Constants.GP_PRIV_INVENTORY;
import static org.openmrs.module.fhirproxy.Constants.MODULE_ID;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.Privilege;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsClassLoader;
import org.openmrs.util.OpenmrsUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.core.env.Environment;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ OpenmrsUtil.class, Context.class })
@PowerMockIgnore({ "javax.management.*", "javax.script.*" })
public class FhirProxyUtilsTest {
	
	@Mock
	private Config mockConfig;
	
	@Mock
	private AdministrationService mockAdminService;
	
	@Mock
	private UserService mockUserService;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(OpenmrsUtil.class);
		PowerMockito.mockStatic(Context.class);
		final String testCfgFile = OpenmrsClassLoader.getInstance().getResource(Constants.CONFIG_FILE).getFile();
		File testCfgDir = new File(testCfgFile).getParentFile();
		when(OpenmrsUtil.getDirectoryInApplicationDataDirectory(MODULE_ID)).thenReturn(testCfgDir);
		when(Context.getAdministrationService()).thenReturn(mockAdminService);
		when(Context.getUserService()).thenReturn(mockUserService);
	}
	
	@After
	public void tearDown() {
		Whitebox.setInternalState(FhirProxyUtils.class, "config", (Object) null);
	}
	
	@Test
	public void getConfig_shouldLoadTheConfig() throws Exception {
		final String privName1 = "priv-1";
		final String privName2 = "priv-2";
		final String privName3 = "priv-3";
		final String privName4 = "priv-4";
		Privilege priv1 = Mockito.mock(Privilege.class);
		Privilege priv2 = Mockito.mock(Privilege.class);
		Privilege priv3 = Mockito.mock(Privilege.class);
		Privilege priv4 = Mockito.mock(Privilege.class);
		when(mockAdminService.getGlobalProperty(GP_PRIV_CHARGE_ITEM)).thenReturn(privName1 + ", " + privName2);
		when(mockAdminService.getGlobalProperty(GP_PRIV_INVENTORY)).thenReturn(privName3 + ", " + privName4);
		when(mockUserService.getPrivilege(privName1)).thenReturn(priv1);
		when(mockUserService.getPrivilege(privName2)).thenReturn(priv2);
		when(mockUserService.getPrivilege(privName3)).thenReturn(priv3);
		when(mockUserService.getPrivilege(privName4)).thenReturn(priv4);
		
		Config cfg = FhirProxyUtils.getConfig();
		
		Assert.assertTrue(cfg.isExternalApiEnabled());
		Assert.assertEquals("http://test.test", cfg.getBaseUrl());
		Assert.assertEquals("test-user", cfg.getUsername());
		Assert.assertEquals("test-password", cfg.getPassword());
		Assert.assertEquals(Arrays.asList(privName1, privName2), cfg.getChargeItemPrivileges());
		Assert.assertEquals(Arrays.asList(privName3, privName4), cfg.getInventoryItemPrivileges());
	}
	
	@Test
	public void getConfig_shouldGetCachedConfig() throws Exception {
		Whitebox.setInternalState(FhirProxyUtils.class, "config", mockConfig);
		Assert.assertEquals(mockConfig, FhirProxyUtils.getConfig());
	}
	
	@Test
	public void loadPrivileges_shouldFailIfNoPrivilegeIsFoundMatchingTheName() {
		final String privName = "priv";
		when(mockAdminService.getGlobalProperty(GP_PRIV_CHARGE_ITEM)).thenReturn(privName);
		Exception e = Assert.assertThrows(APIException.class, () -> FhirProxyUtils.getConfig());
		Assert.assertEquals("No privilege found with name " + privName, e.getMessage());
	}
	
	@Test
	public void resolveEnvVariables_shouldResolveSystemProperty() {
		System.setProperty("TEST_PROP", "systemValue");
		
		String result = FhirProxyUtils.resolveEnvVariables("${TEST_PROP}");
		
		assertNotNull(result);
		assertEquals("systemValue", result);
	}
	
	@Test
	public void resolveEnvVariables_shouldResolveEnvironmentVariable() {
		setEnv("TEST_ENV", "envValue");

		String result = FhirProxyUtils.resolveEnvVariables("${TEST_ENV}");
		
		assertNotNull(result);
		assertEquals("envValue", result);
	}
	
	@Test
	public void resolveEnvVariables_shouldReturnOriginalStringIfNoMatch() {
		String result = FhirProxyUtils.resolveEnvVariables("noPlaceholdersHere");
		
		assertNotNull(result);
		assertEquals("noPlaceholdersHere", result);
	}
	
	@Test
	public void resolveEnvVariables_shouldHandleMultiplePlaceholders() {
		System.setProperty("PROP1", "value1");
		setEnv("ENV1", "value2");
		
		String result = FhirProxyUtils.resolveEnvVariables("${PROP1} and ${ENV1}");
		
		assertNotNull(result);
		assertEquals("value1 and value2", result);
	}
	
	@Test
	public void resolveEnvVariables_shouldLeaveUnresolvedPlaceholdersAsIs() {
		String result = FhirProxyUtils.resolveEnvVariables("${PROP1} and ${ENV1}");
		
		assertNotNull(result);
		assertEquals("${PROP1} and ${ENV1}", result);
	}
	
	// Helper method to set environment variables
	@SuppressWarnings("unchecked")
	private void setEnv(String key, String value) {
		try {
			Map<String, String> env = System.getenv();
			Class<?> cl = env.getClass();
			java.lang.reflect.Field field = cl.getDeclaredField("m");
			field.setAccessible(true);
			Map<String, String> writableEnv = (Map<String, String>) field.get(env);
			if (value == null) {
				writableEnv.remove(key);
			} else {
				writableEnv.put(key, value);
			}
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to set environment variable", e);
		}
	}
}
