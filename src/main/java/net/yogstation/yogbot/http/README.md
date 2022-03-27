# API Endpoints
This package and its subpackages contain all the API endpoints used by other applications to communicate with yogbot.

##`POST /byond/{method}`
This group of endpoints is used for receiving data from dream daemon. Each method has its own class, and defines the 
data to be sent as JSON. At minimum all requests must have the webhook key.

## `GET /api/health`
Basic endpoint that returns status code 200 if the server is probably functioning as intended, 
and not 200 if this is not the case.

## `POST /github`
Endpoint for listening to one or more GitHub webhooks. Ensure the webhook is provided with the key stored in the config, 
as it is used to verify the source of the updates.

## `GET /api/verify`
Redirects a user to the configured endpoint for verification, attaching necessary additional information

## `GET /api/callback` and `POST /api/callback`
Handles the callback from the authentication endpoint
