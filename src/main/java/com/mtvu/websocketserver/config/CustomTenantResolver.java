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
        String tenantId = context.get("tenant-id");
        if (tenantId != null) {
            // When the tenant-id attribute exists in the RoutingContext, this means that
            // The tenant-id has been resolved, so we don't need to do it again
            return tenantId;
        }
        var accessToken = context.request().getHeader("Authorization");
        if (accessToken == null) {
            // In case of no accessToken available, fall back to default tenant identifier
            return null;
        }
        var token = OidcUtils.decodeJwtContent(accessToken);
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