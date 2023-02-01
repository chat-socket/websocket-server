package com.mtvu.websocketserver.handler.health;

import io.smallrye.health.api.AsyncHealthCheck;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import javax.enterprise.context.ApplicationScoped;
import java.time.Duration;

@Readiness
@ApplicationScoped
public class ReadinessHandler implements AsyncHealthCheck {
    @Override
    public Uni<HealthCheckResponse> call() {
        return Uni.createFrom().item(HealthCheckResponse.up("readiness-reactive"))
                .onItem().delayIt().by(Duration.ofMillis(10));
    }
}
