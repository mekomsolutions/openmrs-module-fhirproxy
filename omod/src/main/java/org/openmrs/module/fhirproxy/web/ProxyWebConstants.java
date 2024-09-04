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

import static org.openmrs.module.fhirproxy.Constants.MODULE_ID;

public class ProxyWebConstants {

	public static final String REQ_ROOT_PATH = "/ws/fhir/R4/";

	public static final String PATH_DELEGATE = "/module/" + MODULE_ID + "/delegate.htm";

	public static final String ATTRIB_RESOURCE_NAME = "resourceName";

	public static final String ATTRIB_RESOURCE_ID = "resourceId";

	public static final String ATTRIB_QUERY_STR = "queryString";

}
