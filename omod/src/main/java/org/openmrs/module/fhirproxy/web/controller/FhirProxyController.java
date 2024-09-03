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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.openmrs.module.fhirproxy.ProxyWebConstants.GP_BASE_URL;
import static org.springframework.http.HttpMethod.GET;

import java.util.Base64;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.GlobalPropertyListener;
import org.openmrs.module.fhirproxy.ProxyWebConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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

	public FhirProxyController(AdministrationService adminService) {
		this.adminService = adminService;
	}

	@GetMapping(ProxyWebConstants.PATH_FORWARD)
	public Object forward(HttpServletRequest request) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Forward FHIR request -> {}", request.getRequestURI());
		}

		if (restTemplate == null) {
			restTemplate = new RestTemplate();
		}

		if (baseUrl == null) {
			baseUrl = adminService.getGlobalProperty(GP_BASE_URL);
		}

		final String resource = request.getAttribute(ProxyWebConstants.ATTRIB_RESOURCE_NAME).toString();
		String url = baseUrl + "/" + resource;
		final Object id = request.getAttribute(ProxyWebConstants.ATTRIB_RESOURCE_ID);
		if (id != null) {
			url += ("/" + id);
		}

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		HttpHeaders headers = new HttpHeaders();
		String auth = Base64.getEncoder().encodeToString(":".getBytes(UTF_8));
		headers.add(HttpHeaders.AUTHORIZATION, "Basic " + auth);
		return restTemplate.exchange(builder.encode().toUriString(), GET, new HttpEntity<>(headers), Object.class);
	}

	@Override
	public boolean supportsPropertyName(String s) {
		return s.equals(GP_BASE_URL);
	}

	@Override
	public void globalPropertyChanged(GlobalProperty globalProperty) {
		if (GP_BASE_URL.equals(globalProperty.getProperty())) {
			baseUrl = null;
		}
	}

	@Override
	public void globalPropertyDeleted(String s) {
		if (GP_BASE_URL.equals(s)) {
			baseUrl = null;
		}
	}
}
