package org.openmrs.module.fhirproxy.web.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.openmrs.module.fhirproxy.Config;
import org.openmrs.module.fhirproxy.FhirProxyUtils;
import org.openmrs.module.fhirproxy.web.ProxyWebConstants;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FhirProxyUtils.class)
@PowerMockIgnore({ "javax.management.*", "javax.script.*" })
public class DelegatingControllerTest {
	
	private static final String BASE_URL = "http://test.test";
	
	private static final String USERNAME = "test-user";
	
	private static final String PASSWORD = "test-password";
	
	private static final String RESOURCE = "InventoryItem";
	
	@Mock
	private RestTemplate mockTemplate;
	
	@Mock
	private Config mockConfig;
	
	@Mock
	private HttpServletRequest mockRequest;
	
	private DelegatingController controller;
	
	@Before
	public void setup() throws IOException {
		PowerMockito.mockStatic(FhirProxyUtils.class);
		when(mockConfig.getBaseUrl()).thenReturn(BASE_URL);
		when(FhirProxyUtils.getConfig()).thenReturn(mockConfig);
		when(mockConfig.getUsername()).thenReturn(USERNAME);
		when(mockConfig.getPassword()).thenReturn(PASSWORD);
		controller = new DelegatingController();
		Whitebox.setInternalState(controller, RestTemplate.class, mockTemplate);
	}
	
	@Test
	public void delegate_shouldGetTheResourceFromTheExternalApi() throws Exception {
		final String expectedJson = "{}";
		final String url = BASE_URL + "/" + RESOURCE;
		ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
		when(mockTemplate.exchange(eq(url), eq(GET), httpEntityCaptor.capture(), eq(Object.class)))
		        .thenReturn(ResponseEntity.ok(expectedJson));
		when(mockRequest.getAttribute(ProxyWebConstants.ATTRIB_RESOURCE_NAME)).thenReturn(RESOURCE);
		Assert.assertEquals(expectedJson, ((HttpEntity) controller.delegate(mockRequest)).getBody());
	}
	
	@Test
	public void delegate_shouldGetTheResourceByIdFromTheExternalApi() throws Exception {
		final String resId = "abcde";
		final String expectedJson = "{}";
		final String url = BASE_URL + "/" + RESOURCE + "/" + resId;
		ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
		when(mockTemplate.exchange(eq(url), eq(GET), httpEntityCaptor.capture(), eq(Object.class)))
		        .thenReturn(ResponseEntity.ok(expectedJson));
		when(mockRequest.getAttribute(ProxyWebConstants.ATTRIB_RESOURCE_NAME)).thenReturn(RESOURCE);
		when(mockRequest.getAttribute(ProxyWebConstants.ATTRIB_RESOURCE_ID)).thenReturn(resId);
		Assert.assertEquals(expectedJson, ((HttpEntity) controller.delegate(mockRequest)).getBody());
	}
	
	@Test
	public void delegate_shouldIncludeTheQueryString() throws Exception {
		final String queryString = "key1=val1&key2=val2";
		final String expectedJson = "{}";
		final String url = BASE_URL + "/" + RESOURCE + "?" + queryString;
		ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
		when(mockTemplate.exchange(eq(url), eq(GET), httpEntityCaptor.capture(), eq(Object.class)))
		        .thenReturn(ResponseEntity.ok(expectedJson));
		when(mockRequest.getAttribute(ProxyWebConstants.ATTRIB_RESOURCE_NAME)).thenReturn(RESOURCE);
		when(mockRequest.getAttribute(ProxyWebConstants.ATTRIB_QUERY_STR)).thenReturn(queryString);
		Assert.assertEquals(expectedJson, ((HttpEntity) controller.delegate(mockRequest)).getBody());
	}
	
}
