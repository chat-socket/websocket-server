package com.mtvu.websocketserver.config;

import io.quarkus.oidc.TenantResolver;
import io.quarkus.oidc.runtime.OidcUtils;
import io.vertx.ext.web.RoutingContext;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;


/**
 * @author mvu
 * @project chat-socket
 **/
@ApplicationScoped
public class CustomTenantResolver implements TenantResolver {

    @ConfigProperty(name = "quarkus.oidc.public-client.token.issuer")
    String externalIssuerUri;

    @Override
    public String resolve(RoutingContext context) {
        var accessToken = context.request().getHeader("Authorization");
        var token = OidcUtils.decodeJwtContent(accessToken);
        if (token == null) {
            // In case token is null, fall back to default tenant identifier
            return null;
        }
        var tokenIss = token.getString("iss");
        if (Objects.equals(tokenIss, externalIssuerUri)) {
            // If the current iss is the one that we have declared in our configuration file
            // Then return the public tenant identifier
            return "public-client";
        }

        // Otherwise, return the default identifier
        return null;
    }
}