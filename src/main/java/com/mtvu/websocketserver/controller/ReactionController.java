package com.mtvu.websocketserver.controller;

import com.mtvu.websocketserver.domain.Reaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 * @author mvu
 * @project chat-socket
 **/
@Controller
public class ReactionController {

    @Autowired
    JmsTemplate jmsTemplate;

    @Value("${jms.messaging.topic}")
    String messagingTopic;


    @MessageMapping("/reaction")
    public void messageCreate(Principal principal, @Payload Reaction message) {
        jmsTemplate.convertAndSend(messagingTopic, message);
    }
}
