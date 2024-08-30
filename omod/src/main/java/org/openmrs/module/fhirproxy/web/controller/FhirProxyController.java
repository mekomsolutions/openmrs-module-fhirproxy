/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.fhirproxy.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.module.fhirproxy.ProxyWebConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provides a proxy mechanism for all GET requests for ChargeItemDefinition and InventoryItem FHIR
 * resources by forwarding them to the configured external API.
 */
@RestController
public class FhirProxyController {
	
	private static final Logger LOG = LoggerFactory.getLogger(FhirProxyController.class);
	
	@GetMapping(ProxyWebConstants.PATH_FORWARD)
	public Object forward(HttpServletRequest request) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Forward FHIR request -> {}", request.getRequestURI());
		}
		
		return null;
	}
	
}
