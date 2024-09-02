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

import static org.openmrs.module.fhirproxy.Constants.MODULE_ID;

public class ProxyWebConstants {

	public static final String PATH_FORWARD = "/module/fhirproxy.htm";

	public static final String ATTRIB_RESOURCE_NAME = "resourceName";

	public static final String RES_NAME_CHARGE = "ChargeItemDefinition";

	public static final String RES_NAME_INVENTORY = "InventoryItem";

	public static final String GP_BASE_URL = MODULE_ID + ".external.api.base.url";

	public static final String GP_RES_NAME_CHARGE = MODULE_ID + ".resource.charge.item";

	public static final String GP_RES_NAME_INVENTORY = MODULE_ID + ".resource.inventory.item";

}
