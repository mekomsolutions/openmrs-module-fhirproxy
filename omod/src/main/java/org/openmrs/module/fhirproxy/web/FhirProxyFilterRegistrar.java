/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.fhirproxy.web;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.ServletContext;

import org.openmrs.module.fhirproxy.web.filter.FhirProxyFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

@Component
public class FhirProxyFilterRegistrar implements ServletContextAware {
	
	private static final Logger LOG = LoggerFactory.getLogger(FhirProxyFilterRegistrar.class);
	
	private static final String[] MAPPINGS = { "/ws/fhir2/R4/ChargeItemDefinition/*", "/ws/fhir2/R4/InventoryItem/*" };
	
	@Override
	public void setServletContext(ServletContext servletContext) {
		LOG.info("Adding FHIR Proxy Filter");
		
		try {
			Dynamic filter = servletContext.addFilter("FHIR Proxy", new FhirProxyFilter());
			filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, MAPPINGS);
		}
		catch (Exception ex) {
			//Ignore known issue, this happens when running openmrs the first time.
		}
	}
	
}
