package com.mtvu.websocketserver.domain.message;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class RecordingMessageContent implements MessageContent {

    private int id;

    private long size;

    private String url;

    private int duration;

    @Override
    public MessageContentType getType() {
        return MessageContentType.RECORDING;
    }
}
