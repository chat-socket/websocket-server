package com.mtvu.websocketserver.handler;

import com.mtvu.websocketserver.service.SessionManagementService;
import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;

@ApplicationScoped
public class NotificationHandler {

    private static final Logger LOGGER = Logger.getLogger(NotificationHandler.class);

    @Inject
    SessionManagementService sessionManagementService;

    /**
     * Currently we capture all messages and filter the one that this service is currently managing
     * However, this is a costly approach and do not scalable. Further work should address this problem
     * by (somehow) sending the message to the correct server.
     * @param record <String, String> with first parameter indicates the user ID
     *               and the second parameter is about the payload (JSON String). Since we trust our messaging-service,
     *               therefore, we don't need to verify this payload but send it directly back to our user.
     */
    @Incoming("messaging-topic-in")
    public void notifyUser(Record<String, String> record) {
        var session = sessionManagementService.getSession(record.key());
        if (session == null) {
            return;
        }
        try {
            session.getSession().getBasicRemote().sendText(record.value());
        } catch (IOException e) {
            LOGGER.debugv("Exception occurred when sending a message to user", e);
        }
    }
}
