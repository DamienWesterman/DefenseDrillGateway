# DefenseDrillGateway
Spring Cloud API Gateway for the [DefenseDrillWeb backend](https://github.com/DamienWesterman/DefenseDrillWeb/).

# Purpose
This microservice is responsible for routing incoming requests to the appropriate microservices and performing request authentication/authorization.

# Design Considerations
A few filters are offered for configuration in the [config-server](https://github.com/DamienWesterman/DefenseDrillConfigServer):
- AuthenticationFilter: This verifies a JWT and makes sure that the user's role is appropriate to access the endpoint. The config-server configures what roles are allowed per endpoint.
- GetMethodOnlyFilter: Ensures that only get methods are passed downstream. Mostly used to restrict users from altering the [rest-api](https://github.com/DamienWesterman/DefenseDrillRestAPI)'s database.
- LocalNetworkFilter: Limits endpoint access from only within a local private network using IP addresses such as 10.xxx, 192.168.xxx, etc.
