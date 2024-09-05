package org.openmrs.module.fhirproxy;

import static org.openmrs.module.fhirproxy.Constants.MODULE_ID;

import java.io.File;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.util.OpenmrsClassLoader;
import org.openmrs.util.OpenmrsUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest(OpenmrsUtil.class)
@PowerMockIgnore({ "javax.management.*", "javax.script.*" })
public class FhirProxyUtilsTest {
	
	@Mock
	private Config mockConfig;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(OpenmrsUtil.class);
		final String testCfgFile = OpenmrsClassLoader.getInstance().getResource(Constants.CONFIG_FILE).getFile();
		File testCfgDir = new File(testCfgFile).getParentFile();
		Mockito.when(OpenmrsUtil.getDirectoryInApplicationDataDirectory(MODULE_ID)).thenReturn(testCfgDir);
	}
	
	@After
	public void tearDown() {
		Whitebox.setInternalState(FhirProxyUtils.class, "config", (Object) null);
	}
	
	@Test
	public void getConfig_shouldLoadTheConfig() throws Exception {
		Config cfg = FhirProxyUtils.getConfig();
		Assert.assertTrue(cfg.isExternalApiEnabled());
		Assert.assertEquals("http://test.test", cfg.getBaseUrl());
		Assert.assertEquals("test-user", cfg.getUsername());
		Assert.assertEquals("test-password", cfg.getPassword());
	}
	
	@Test
	public void getConfig_shouldGetCachedConfig() throws Exception {
		Whitebox.setInternalState(FhirProxyUtils.class, "config", mockConfig);
		Assert.assertEquals(mockConfig, FhirProxyUtils.getConfig());
	}
	
}
