# FHIR Proxy Module
An OpenMRS module that intercepts and redirects fetch requests for `ChargeItemDefinition` and `InventoryItem` FHIR 
resources to an external API, with support for basic authentication.

## Configuration
The module reads the configuration from a json file named `config.json` in a directory name `fhirproxy` which is located 
in the application data directory, below is an example configuration file.
```
{
    "externalApiEnabled" : true,
    "baseUrl" : "http://your-fhirserver.com/fhir/R4",
    "username" : "test-user",
    "password" : "test-password"
}
```
#### Configuration Properties
`externalApiEnabled`: When set to true, the module will delegate `GET` FHIR requests for `ChargeItemDefinition` and 
 `InventoryItem` to the configured external API.

`baseUrl`: The root URL for the external FHIR API

`username`: The username to authenticate to the external FHIR API

`password`: The password to authenticate to the external FHIR API
