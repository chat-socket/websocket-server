package com.mtvu.websocketserver.handler.health;

import io.smallrye.health.api.AsyncHealthCheck;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import javax.enterprise.context.ApplicationScoped;
import java.time.Duration;

@Liveness
@ApplicationScoped
public class LivenessHandler implements AsyncHealthCheck {

    @Override
    public Uni<HealthCheckResponse> call() {
        return Uni.createFrom().item(HealthCheckResponse.up("liveness-reactive"))
                .onItem().delayIt().by(Duration.ofMillis(10));
    }
}
