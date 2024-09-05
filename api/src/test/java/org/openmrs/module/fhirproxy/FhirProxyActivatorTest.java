package org.openmrs.module.fhirproxy;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.module.ModuleException;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FhirProxyUtils.class)
@PowerMockIgnore({ "javax.management.*", "javax.script.*" })
public class FhirProxyActivatorTest {
	
	@Mock
	private Config mockConfig;
	
	private FhirProxyActivator activator;
	
	@Before
	public void setup() throws Exception {
		PowerMockito.mockStatic(FhirProxyUtils.class);
		Mockito.when(FhirProxyUtils.getConfig()).thenReturn(mockConfig);
		activator = new FhirProxyActivator();
	}
	
	@Test
	public void started_shouldPassIfTheExternalApiIsNotEnabled() {
		activator.started();
	}
	
	@Test
	public void started_shouldPassIfTheExternalApiIsEnabledAndConfigIsValid() {
		Mockito.when(mockConfig.isExternalApiEnabled()).thenReturn(true);
		Mockito.when(mockConfig.getBaseUrl()).thenReturn("http://test.test");
		Mockito.when(mockConfig.getUsername()).thenReturn("test-user");
		Mockito.when(mockConfig.getPassword()).thenReturn("test-password");
		activator.started();
	}
	
	@Test
	public void started_shouldFailIfBaseUrlIsMissing() {
		Mockito.when(mockConfig.isExternalApiEnabled()).thenReturn(true);
		
		Exception ex = Assert.assertThrows(ModuleException.class, () -> activator.started());
		
		assertEquals("Fhir Proxy module requires baseUrl when external FHIR API is enabled", ex.getMessage());
	}
	
	@Test
	public void started_shouldFailIfUsernameIsMissing() {
		Mockito.when(mockConfig.isExternalApiEnabled()).thenReturn(true);
		Mockito.when(mockConfig.getBaseUrl()).thenReturn("http://test.test");
		
		Exception ex = Assert.assertThrows(ModuleException.class, () -> activator.started());
		
		assertEquals("Fhir Proxy module requires username when external FHIR API is enabled", ex.getMessage());
	}
	
	@Test
	public void started_shouldFailIfPasswordIsMissing() {
		Mockito.when(mockConfig.isExternalApiEnabled()).thenReturn(true);
		Mockito.when(mockConfig.getBaseUrl()).thenReturn("http://test.test");
		Mockito.when(mockConfig.getUsername()).thenReturn("test-user");
		
		Exception ex = Assert.assertThrows(ModuleException.class, () -> activator.started());
		
		assertEquals("Fhir Proxy module requires password when external FHIR API is enabled", ex.getMessage());
	}
}
