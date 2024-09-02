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

import static org.openmrs.module.fhirproxy.ProxyWebConstants.GP_BASE_URL;
import static org.openmrs.module.fhirproxy.ProxyWebConstants.GP_RES_NAME_CHARGE;
import static org.openmrs.module.fhirproxy.ProxyWebConstants.GP_RES_NAME_INVENTORY;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.GlobalPropertyListener;
import org.openmrs.module.fhirproxy.ProxyWebConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Provides a proxy mechanism for all GET requests for ChargeItemDefinition and InventoryItem FHIR
 * resources by forwarding them to the configured external API.
 */
@RestController("fhirProxyController")
public class FhirProxyController implements GlobalPropertyListener {

	private static final Logger LOG = LoggerFactory.getLogger(FhirProxyController.class);

	private RestTemplate restTemplate;

	private AdministrationService adminService;

	private String baseUrl;

	private String chargeResource;

	private String inventoryResource;

	public FhirProxyController(AdministrationService adminService) {
		this.adminService = adminService;
	}

	@GetMapping(ProxyWebConstants.PATH_FORWARD)
	public Object forward(HttpServletRequest request) throws Exception {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Forward FHIR request -> {}", request.getRequestURI());
		}

		if (restTemplate == null) {
			restTemplate = new RestTemplate();
		}

		if (baseUrl == null) {
			baseUrl = adminService.getGlobalProperty(GP_BASE_URL);
		}

		if (chargeResource == null) {
			chargeResource = adminService.getGlobalProperty(GP_RES_NAME_CHARGE);
		}

		if (inventoryResource == null) {
			inventoryResource = adminService.getGlobalProperty(GP_RES_NAME_INVENTORY);
		}

		final String resource = request.getAttribute(ProxyWebConstants.ATTRIB_RESOURCE_NAME).toString();
		String targetResource;
		if (ProxyWebConstants.RES_NAME_CHARGE.equals(resource)) {
			targetResource = chargeResource;
		} else if (ProxyWebConstants.RES_NAME_INVENTORY.equals(resource)) {
			targetResource = inventoryResource;
		} else {
			throw new Exception("Unsupported resource " + resource);
		}

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/" + targetResource);
		return restTemplate.getForObject(builder.encode().toUriString(), Object.class);
	}

	@Override
	public boolean supportsPropertyName(String s) {
		return s.equals(GP_BASE_URL) || s.equals(GP_RES_NAME_CHARGE) || s.equals(GP_RES_NAME_INVENTORY);
	}

	@Override
	public void globalPropertyChanged(GlobalProperty globalProperty) {
		if (GP_BASE_URL.equals(globalProperty.getProperty())) {
			baseUrl = null;
		} else if (GP_RES_NAME_CHARGE.equals(globalProperty.getProperty())) {
			chargeResource = null;
		} else if (GP_RES_NAME_INVENTORY.equals(globalProperty.getProperty())) {
			inventoryResource = null;
		}
	}

	@Override
	public void globalPropertyDeleted(String s) {
		if (GP_BASE_URL.equals(s)) {
			baseUrl = null;
		} else if (GP_RES_NAME_CHARGE.equals(s)) {
			chargeResource = null;
		} else if (GP_RES_NAME_INVENTORY.equals(s)) {
			inventoryResource = null;
		}
	}
}
