package com.mtvu.websocketserver.domain;

import java.io.Serializable;

/**
 * @author mvu
 * @project chat-socket
 **/
public class Message implements Serializable {

    private String message;
    private String groupId;

    public Message() {
    }

    public Message(String groupId, String message) {
        this.message = message;
        this.groupId = groupId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "Message{" +
            "message='" + message + '\'' +
            ", groupId='" + groupId + '\'' +
            '}';
    }
}
