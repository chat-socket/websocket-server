## Simple Websocket authentication using OAuth2 access token

### The hack
Currently, websocket protocol doesn't allow us to send any custom header
parameter during the initial handshake request. However, we can modify the Sec-WebSocket-Protocol header to include
our access token, as following:

```javascript
const ws = new WebSocket(`ws://localhost:8080/ws`, ["access_token", "your-token"] );
```

Then, your WS handshake request header will look like:

```
Sec-WebSocket-Extensions: permessage-deflate; client_max_window_bits
Sec-WebSocket-Key: H5LOmJtnNZ4GeQw6hT77sw==
Sec-WebSocket-Protocol: access_token, eyJraWQiOiI4OTE0NjczNC04YjdkLTQwZWYtYTA1Ni0xYzlmYzhjNDE5M2EiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJnbG1hbmh0dUBnbWFpbC5jb20iLCJhdWQiOiJjaGF0LXdlYi1jbGllbnQtaWQiLCJuYmYiOjE2NzQ4NzAzMjYsInNjb3BlIjpbImdyb3Vwczp3cml0ZSIsInByb2ZpbGU6d3JpdGUiLCJtZXNzYWdlOnJlYWQiLCJvcGVuaWQiLCJwcm9maWxlIiwiZ3JvdXBzOnJlYWQiLCJtZXNzYWdlOndyaXRlIiwicHJvZmlsZTpyZWFkIl0sImlzcyI6Imh0dHA6Ly8xMjcuMC4wLjE6OTAwMCIsImV4cCI6MTY3NDg3MDkyNiwiaWF0IjoxNjc0ODcwMzI2fQ.W0Ds_3zD-mr8XhRjpQjT8_hCRbgnqt7I6Z7jV7iNkT2jwYa6e1H5p9VmSzNJcsHKEWV-bjuvfEyNsIZ1sRa03CIP3vc75_7mTn9tj6T9D2Jhkv7Bw0bMj8KJMeSL0yvYtCbpZVHJGL5y9Dv_vpZoIDqIuNHlQZtzixSB521s0RrX5AcjSci4Hcaf3zKvQbbNjzUgLdT3XijpmWiMjZbSHqjiMfVVFbdQ0E-sEPNLo5Rc30sMdPQ-O1nka4xqhKQ7Ue7siJ2h2OX9MQYUihDxVnubtBk0ZuzuCsGlpKeaJo8QJfopeiWdQep31ESta2GMrIULxJ2_S7UtpCt-nS28Hg
```

Now, in our server implementation, we just need to extract the access token and communicate with our oauth2 provider to validate this user.

For Quarkus, since we already have a built-in library that support Oidc authentication and authorization, 
we just need to utilise this library to validate our user by setting the token to the "Authorization" header of the current request!


### To run this demo
Step 1: Update the application.properties file with your Oidc provider:
```
quarkus.oidc.client-id=<your client id>
quarkus.oidc.credentials.secret=<your client secret>
quarkus.oidc.auth-server-url=<your auth server url>
```

Step 2: Grab your access token from your Oidc provider
See https://quarkus.io/guides/security-openid-connect#quarkus-oidc_quarkus.oidc.discovery-enabled

E.g.:
```
curl --insecure -X POST http://localhost:8180/realms/quarkus/protocol/openid-connect/token \
    --user backend-service:secret \
    -H 'content-type: application/x-www-form-urlencoded' \
    -d 'username=alice&password=alice&grant_type=password' | jq --raw-output '.access_token' \
```

Step 3: Go to http://localhost:8080/ and connect with your access token

Cheers !
