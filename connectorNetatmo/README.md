Connector Netatmo
=================================================

The Netatmo fetch the measurements from the Netatmo-Cloud, where the personal
weather data is stored. To connect to the Netatmo-Cloud a special authentication
method OAuth2 is required.

Depending on the outdoor temperature the connector sends a message to the connectorHue
to set the color according the outdoor temperature.

OAuth2-Configuration
------------------------------------------
To get the mesurenments from the Neatmo-Cloud:

1. create account / sign in on http://dev.netatmo.com/
2. klick => create App. To connect only name and description is needed.
3. copy the src/main/resources/application_template.conf to src/main/resources/application.conf
4. Set the provided client_id and client_secret in the src/main/resources/application.conf

### HTTPS
The OAuth2 authentication use the https protocol. https and Java is not always a funny thing. Netatmo
use a certificate from a authority which is not in the java ```cacerts``` store.
