package com.mtvu.websocketserver.domain.message;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mtvu.websocketserver.domain.GenericMessage;
import com.mtvu.websocketserver.jackson.MessageContentDeserializer;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ChatMessage extends GenericMessage {
    private Integer id;
    private MessageType messageType;
    @JsonDeserialize(using = MessageContentDeserializer.class)
    private MessageContent content;
    private OffsetDateTime date;
    private String sender;
    private Integer replyTo;
    private String receiver;
    private List<Attachment> attachments;
}
