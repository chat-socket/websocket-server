package com.mtvu.websocketserver.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * @author mvu
 * @project chat-socket
 **/
@Data
@NoArgsConstructor
public class TransferMessage implements Serializable {
    private String from;
    private String groupId;
    private OffsetDateTime timestamp;
    private String message;

    public TransferMessage(Message message, String from) {
        this.from = from;
        this.groupId = message.getGroupId();
        this.message = message.getMessage();
        this.timestamp = OffsetDateTime.now();
    }
}
