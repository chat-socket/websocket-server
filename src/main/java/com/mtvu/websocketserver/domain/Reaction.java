package com.mtvu.websocketserver.domain;

/**
 * @author mvu
 * @project chat-socket
 **/
public class Reaction {
    public String from;
    public ReactionType reactionType;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public ReactionType getReactionType() {
        return reactionType;
    }

    public void setReactionType(ReactionType reactionType) {
        this.reactionType = reactionType;
    }
}
