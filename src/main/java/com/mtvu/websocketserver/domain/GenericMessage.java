package com.mtvu.websocketserver.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public abstract class GenericMessage {

    @JsonProperty("c")
    private String channel;

    private MessageAction messageAction;
}
