package com.mtvu.websocketserver.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.mtvu.websocketserver.domain.GenericMessage;
import com.mtvu.websocketserver.handler.GenericMessageHandler;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Objects;

public class GenericMessageDeserializer extends StdDeserializer<GenericMessage> {
    protected GenericMessageDeserializer(Class<?> vc) {
        super(vc);
    }

    protected GenericMessageDeserializer() {
        super(GenericMessage.class);
    }

    @Override
    public GenericMessage deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectCodec mapper = p.getCodec();
        JsonNode node = mapper.readTree(p);
        String channel = node.get("c").textValue();
        for (var kv : GenericMessageHandler.HANDLERS.entrySet()) {
            if (Objects.equals(channel, kv.getKey())) {
                return mapper.treeToValue(node, kv.getValue().handleMessageType());
            }
        }
        throw new InvalidPropertiesFormatException("Unable to decode message " + p.getValueAsString());
    }
}
