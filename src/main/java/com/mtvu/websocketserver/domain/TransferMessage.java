package com.mtvu.websocketserver.domain;

import java.io.Serializable;
import java.time.Instant;

/**
 * @author mvu
 * @project chat-socket
 **/
public class TransferMessage implements Serializable {
    private final String from;
    private final String groupId;
    private final long timestamp;
    private final String message;

    public TransferMessage(String from, String groupId, long timestamp, String message) {
        this.from = from;
        this.groupId = groupId;
        this.timestamp = timestamp;
        this.message = message;
    }

    public TransferMessage(Message message, String from) {
        this.from = from;
        this.groupId = message.getGroupId();
        this.message = message.getMessage();
        this.timestamp = Instant.now().getEpochSecond();
    }

    public String getFrom() {
        return from;
    }

    public String getGroupId() {
        return groupId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "TransferMessage{" +
            "from='" + from + '\'' +
            ", groupId='" + groupId + '\'' +
            ", timestamp=" + timestamp +
            ", message='" + message + '\'' +
            '}';
    }
}
