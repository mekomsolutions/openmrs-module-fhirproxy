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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.openmrs.module.fhirproxy.ProxyWebConstants;
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
		final String uri = request.getRequestURI();
		servletRequest.setAttribute(ProxyWebConstants.ATTRIB_RESOURCE_NAME, uri.substring(uri.lastIndexOf("/") + 1));
		servletRequest.getRequestDispatcher(ProxyWebConstants.PATH_FORWARD).forward(servletRequest, servletResponse);
	}

	@Override
	public void destroy() {
	}
}
