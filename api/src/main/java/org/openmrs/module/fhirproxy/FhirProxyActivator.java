/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.fhirproxy;

import java.io.IOException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.ModuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FhirProxyActivator extends BaseModuleActivator {
	
	private static final Logger LOG = LoggerFactory.getLogger(FhirProxyActivator.class);
	
	/**
	 * @see BaseModuleActivator#started()
	 */
	@Override
	public void started() {
		Config cfg;
		try {
			cfg = FhirProxyUtils.getConfig();
		}
		catch (IOException e) {
			throw new ModuleException("Failed to load configuration file for the Fhir Proxy module", e);
		}
		
		if (cfg.isExternalApiEnabled()) {
			if (StringUtils.isBlank(cfg.getBaseUrl())) {
				throw new ModuleException("Fhir Proxy module requires base.url when external FHIR API is enabled");
			} else if (StringUtils.isBlank(cfg.getUsername())) {
				throw new ModuleException("Fhir Proxy module requires username when external FHIR API is enabled");
			} else if (StringUtils.isBlank(cfg.getPassword())) {
				throw new ModuleException("Fhir Proxy module requires password when external FHIR API is enabled");
			} else if (CollectionUtils.isEmpty(cfg.getChargeItemPrivileges())) {
				throw new ModuleException("Fhir Proxy module requires privileges for change item definition when "
				        + "external FHIR API is enabled");
			} else if (CollectionUtils.isEmpty(cfg.getInventoryItemPrivileges())) {
				throw new ModuleException(
				        "Fhir Proxy module requires privileges for inventory item when external FHIR API is enabled");
			}
		}
		
		LOG.info("FHIR Proxy module started");
	}
	
	/**
	 * @see BaseModuleActivator#stopped()
	 */
	@Override
	public void stopped() {
		LOG.info("FHIR Proxy module stopped");
	}
	
}
