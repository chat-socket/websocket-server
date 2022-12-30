package com.mtvu.websocketserver.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author mvu
 * @project chat-socket
 **/
@Data
@NoArgsConstructor
public class Message implements Serializable {
    private String groupId;
    private String message;
    private String messageParent;

    public Message(String groupId, String message) {
        this.groupId = groupId;
        this.message = message;
        this.messageParent = null;
    }
}
