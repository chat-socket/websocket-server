package com.mtvu.websocketserver.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 * @author mvu
 * @project chat-socket
 **/
@Controller
public class NotificationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationController.class);

    @MessageMapping("/notifications")
    public void getNotifications(Principal principal) {

    }
}
