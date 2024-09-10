# FHIR Proxy Module
An OpenMRS module that intercepts and redirects fetch requests for `ChargeItemDefinition` and `InventoryItem` FHIR 
resources to an external API, with support for basic authentication.

## Configuration
The module reads the configuration from a properties file named `config.properties` in a directory named `fhirproxy` 
which is located in the application data directory, below is an example configuration file.
```
external.api.enabled=true
base.url=http://your-fhirserver/fhir/R4
username=test-user
password=test-password
```
#### Configuration Properties
`external.api.enabled`: When set to true, the module will delegate `GET` FHIR requests for `ChargeItemDefinition` and 
 `InventoryItem` to the configured external API.

`base.url`: The root URL for the external FHIR API

`username`: The username to authenticate to the external FHIR API

`password`: The password to authenticate to the external FHIR API
