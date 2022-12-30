package com.mtvu.websocketserver.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author mvu
 * @project chat-socket
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {
    private String groupId;
    private String message;
}
