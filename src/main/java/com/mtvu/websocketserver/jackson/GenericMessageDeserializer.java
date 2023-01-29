package com.mtvu.websocketserver.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.mtvu.websocketserver.domain.GenericMessage;
import com.mtvu.websocketserver.domain.message.ChatMessage;

import java.io.IOException;
import java.util.Objects;

public class GenericMessageDeserializer extends StdDeserializer<GenericMessage> {
    protected GenericMessageDeserializer(Class<?> vc) {
        super(vc);
    }

    protected GenericMessageDeserializer() {
        super(GenericMessage.class);
    }

    @Override
    public GenericMessage deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        ObjectCodec mapper = p.getCodec();
        JsonNode node = mapper.readTree(p);
        String channel = node.get("channel").textValue();
        if (Objects.equals(channel, "message")) {
            return mapper.treeToValue(node, ChatMessage.class);
        }
        throw new RuntimeException("Unable to decode message " + p.getValueAsString());
    }
}
