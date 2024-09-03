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

	public static final String REQUEST_ROOT_PATH = "/ws/fhir/R4/";

	public static final String PATH_FORWARD = "/module/" + MODULE_ID + "/forward.htm";

	public static final String ATTRIB_RESOURCE_NAME = "resourceName";

	public static final String ATTRIB_RESOURCE_ID = "resourceId";

	public static final String GP_BASE_URL = MODULE_ID + ".external.api.base.url";

}
