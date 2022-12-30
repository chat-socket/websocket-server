package com.mtvu.websocketserver.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author mvu
 * @project chat-socket
 **/
@Data
@NoArgsConstructor
public class Reaction {
    public String from;
    public String groupId;
    public String messageId;
    public ReactionType reactionType;
}
