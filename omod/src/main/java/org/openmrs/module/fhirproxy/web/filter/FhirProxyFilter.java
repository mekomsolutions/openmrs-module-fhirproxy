/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.fhirproxy.web.filter;

import static org.openmrs.module.fhirproxy.web.ProxyWebConstants.PATH_DELEGATE;
import static org.openmrs.module.fhirproxy.web.ProxyWebConstants.REQ_ROOT_PATH;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.fhirproxy.Constants;
import org.openmrs.module.fhirproxy.FhirProxyUtils;
import org.openmrs.module.fhirproxy.web.ProxyWebConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * If an external API is configured, this filter intercepts GET requests for ChargeItemDefinition
 * and InventoryItem FHIR resources and forwards them to it.
 */
public class FhirProxyFilter implements Filter {
	
	private static final Logger LOG = LoggerFactory.getLogger(FhirProxyFilter.class);
	
	@Override
	public void init(FilterConfig filterConfig) {
	}
	
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
	        throws IOException, ServletException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Intercepting request {}", ((HttpServletRequest) servletRequest).getRequestURI());
		}
		
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		if (FhirProxyUtils.getConfig().isExternalApiEnabled() && Context.isAuthenticated()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Delegating to external API to process FHIR request -> {}", request.getRequestURI());
			}
			
			//TODO Possibly forward some useful headers too e.g. Content-Type and Accept
			final String uri = request.getRequestURI();
			servletRequest.setAttribute(ProxyWebConstants.ATTRIB_QUERY_STR, request.getQueryString());
			String[] resAndId = uri.substring(uri.lastIndexOf(REQ_ROOT_PATH) + REQ_ROOT_PATH.length()).split("/");
			final String resource = resAndId[0];
			List<String> requiredPrivileges = new ArrayList<>();
			if (Constants.RES_CHARGE_ITEM.equals(resource)) {
				if (CollectionUtils.isEmpty(FhirProxyUtils.getConfig().getChargeItemPrivileges())) {
					throw new ContextAuthenticationException(
					        "Fhir Proxy module requires privileges for charge item definition when "
					                + "external FHIR API is enabled");
				}
				requiredPrivileges.addAll(FhirProxyUtils.getConfig().getChargeItemPrivileges());
			} else if (Constants.RES_INVENTORY_ITEM.equals(resource)) {
				if (CollectionUtils.isEmpty(FhirProxyUtils.getConfig().getInventoryItemPrivileges())) {
					throw new ContextAuthenticationException("Fhir Proxy module requires privileges for inventory "
					        + "item when external FHIR API is enabled");
				}
				requiredPrivileges.addAll(FhirProxyUtils.getConfig().getInventoryItemPrivileges());
			}
			
			requiredPrivileges.forEach(privilege -> Context.requirePrivilege(privilege));
			servletRequest.setAttribute(ProxyWebConstants.ATTRIB_RESOURCE_NAME, resource);
			if (resAndId.length == 2) {
				servletRequest.setAttribute(ProxyWebConstants.ATTRIB_RESOURCE_ID, resAndId[1]);
			}
			
			servletRequest.getRequestDispatcher(PATH_DELEGATE).forward(servletRequest, servletResponse);
			return;
		} else if (!FhirProxyUtils.getConfig().isExternalApiEnabled()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Delegation to external FHIR API is disabled");
			}
		}
		
		filterChain.doFilter(servletRequest, servletResponse);
	}
	
	@Override
	public void destroy() {
	}
}
