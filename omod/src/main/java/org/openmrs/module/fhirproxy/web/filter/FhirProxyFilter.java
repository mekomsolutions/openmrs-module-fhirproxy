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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

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
		if (FhirProxyUtils.getConfig().isExternalApiEnabled()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Delegating to external API to process FHIR request -> {}", request.getRequestURI());
			}

			//TODO Possibly forward some useful headers too e.g. Content-Type and Accept
			final String uri = request.getRequestURI();
			servletRequest.setAttribute(ProxyWebConstants.ATTRIB_QUERY_STR, request.getQueryString());
			String[] resAndId = uri.substring(uri.lastIndexOf(REQ_ROOT_PATH) + REQ_ROOT_PATH.length()).split("/");
			servletRequest.setAttribute(ProxyWebConstants.ATTRIB_RESOURCE_NAME, resAndId[0]);
			if (resAndId.length == 2) {
				servletRequest.setAttribute(ProxyWebConstants.ATTRIB_RESOURCE_ID, resAndId[1]);
			}

			servletRequest.getRequestDispatcher(PATH_DELEGATE).forward(servletRequest, servletResponse);
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Delegation to external FHIR API is disabled");
			}

			filterChain.doFilter(servletRequest, servletResponse);
		}
	}

	@Override
	public void destroy() {
	}
}
