package org.openmrs.module.fhirproxy.web.filter;

import static org.mockito.Mockito.when;
import static org.openmrs.module.fhirproxy.web.ProxyWebConstants.ATTRIB_QUERY_STR;
import static org.openmrs.module.fhirproxy.web.ProxyWebConstants.ATTRIB_RESOURCE_ID;
import static org.openmrs.module.fhirproxy.web.ProxyWebConstants.ATTRIB_RESOURCE_NAME;
import static org.openmrs.module.fhirproxy.web.ProxyWebConstants.PATH_DELEGATE;
import static org.openmrs.module.fhirproxy.web.ProxyWebConstants.REQ_ROOT_PATH;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.module.fhirproxy.Config;
import org.openmrs.module.fhirproxy.FhirProxyUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FhirProxyUtils.class)
@PowerMockIgnore({ "javax.management.*", "javax.script.*" })
public class FhirProxyFilterTest {
	
	@Mock
	private Config mockConfig;
	
	@Mock
	private HttpServletRequest mockRequest;
	
	@Mock
	private ServletResponse mockResponse;
	
	@Mock
	private FilterChain mockChain;
	
	@Mock
	private RequestDispatcher mockDispatcher;
	
	private FhirProxyFilter filter;
	
	@Before
	public void setup() throws IOException {
		PowerMockito.mockStatic(FhirProxyUtils.class);
		when(FhirProxyUtils.getConfig()).thenReturn(mockConfig);
		filter = new FhirProxyFilter();
	}
	
	@Test
	public void doFilter_shouldForwardTheRequestForDelegation() throws Exception {
		final String resource = "InventoryItem";
		final String queryString = "key1=val1&key2=val2";
		final String uri = REQ_ROOT_PATH + resource;
		Mockito.when(mockConfig.isExternalApiEnabled()).thenReturn(true);
		Mockito.when(mockRequest.getRequestURI()).thenReturn(uri);
		Mockito.when(mockRequest.getQueryString()).thenReturn(queryString);
		Mockito.when(mockRequest.getRequestDispatcher(PATH_DELEGATE)).thenReturn(mockDispatcher);
		
		filter.doFilter(mockRequest, mockResponse, mockChain);
		
		Mockito.verify(mockRequest).setAttribute(ATTRIB_RESOURCE_NAME, resource);
		Mockito.verify(mockRequest).setAttribute(ATTRIB_QUERY_STR, queryString);
		Mockito.verify(mockDispatcher).forward(mockRequest, mockResponse);
	}
	
	@Test
	public void doFilter_shouldForwardTheRequestWithForDelegation() throws Exception {
		final String resource = "InventoryItem";
		final String resId = "abcde";
		final String uri = REQ_ROOT_PATH + resource + "/" + resId;
		Mockito.when(mockConfig.isExternalApiEnabled()).thenReturn(true);
		Mockito.when(mockRequest.getRequestURI()).thenReturn(uri);
		Mockito.when(mockRequest.getRequestDispatcher(PATH_DELEGATE)).thenReturn(mockDispatcher);
		
		filter.doFilter(mockRequest, mockResponse, mockChain);
		
		Mockito.verify(mockRequest).setAttribute(ATTRIB_RESOURCE_NAME, resource);
		Mockito.verify(mockRequest).setAttribute(ATTRIB_RESOURCE_ID, resId);
		Mockito.verify(mockDispatcher).forward(mockRequest, mockResponse);
	}
	
	@Test
	public void doFilter_shouldSkipDelegationIfExternalApiIsNotEnabled() throws Exception {
		filter.doFilter(mockRequest, mockResponse, mockChain);
		Mockito.verifyNoInteractions(mockDispatcher);
	}
	
}
