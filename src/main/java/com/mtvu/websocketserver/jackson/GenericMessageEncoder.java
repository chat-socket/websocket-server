package com.mtvu.websocketserver.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mtvu.websocketserver.domain.GenericMessage;

import javax.websocket.*;

public class GenericMessageEncoder implements Encoder.Text<GenericMessage>  {

    private static final ObjectMapper MAPPER;

    static {
        final SimpleModule module = new SimpleModule();
        MAPPER = new ObjectMapper();
        module.addDeserializer(GenericMessage.class, new GenericMessageDeserializer());
        MAPPER.registerModule(new JavaTimeModule());
        MAPPER.registerModule(module);
    }

    @Override
    public String encode(GenericMessage object) throws EncodeException {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new EncodeException(object, "Unable to encode", e);
        }
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}
