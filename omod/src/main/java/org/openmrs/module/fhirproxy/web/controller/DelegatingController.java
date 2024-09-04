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
import static java.util.Base64.getEncoder;
import static org.springframework.http.HttpMethod.GET;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.module.fhirproxy.Config;
import org.openmrs.module.fhirproxy.FhirProxyUtils;
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
 * resources by delegating to a configured external API to process the request.
 */
@RestController("delegatingController")
public class DelegatingController {

	private static final Logger LOG = LoggerFactory.getLogger(DelegatingController.class);

	private RestTemplate restTemplate;

	@GetMapping(ProxyWebConstants.PATH_DELEGATE)
	public Object delegate(HttpServletRequest request) throws IOException {
		if (restTemplate == null) {
			restTemplate = new RestTemplate();
		}

		final String resource = request.getAttribute(ProxyWebConstants.ATTRIB_RESOURCE_NAME).toString();
		final Config cfg = FhirProxyUtils.getConfig();
		String url = cfg.getBaseUrl() + "/" + resource;
		final Object id = request.getAttribute(ProxyWebConstants.ATTRIB_RESOURCE_ID);
		if (id != null) {
			url += ("/" + id);
		}

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		final String auth = getEncoder().encodeToString((cfg.getUsername() + ":" + cfg.getPassword()).getBytes(UTF_8));
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Basic " + auth);
		return restTemplate.exchange(builder.encode().toUriString(), GET, new HttpEntity<>(headers), Object.class);
	}
}