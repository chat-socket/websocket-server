package com.mtvu.websocketserver.domain.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(value={ "type" }, allowGetters=true)
public class TextMessageContent implements MessageContent {
    private String content;
    @Override
    public MessageContentType getType() {
        return MessageContentType.TEXT;
    }
}
