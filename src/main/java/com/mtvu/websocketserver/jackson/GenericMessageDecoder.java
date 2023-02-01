package com.mtvu.websocketserver.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mtvu.websocketserver.domain.GenericMessage;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class GenericMessageDecoder implements Decoder.Text<GenericMessage>  {

    private static final ObjectMapper MAPPER;

    static {
        final SimpleModule module = new SimpleModule();
        MAPPER = new ObjectMapper();
        module.addDeserializer(GenericMessage.class, new GenericMessageDeserializer());
        MAPPER.registerModule(module);
        MAPPER.registerModule(new JavaTimeModule());
    }

    @Override
    public GenericMessage decode(String s) throws DecodeException {
        try {
            return MAPPER.readValue(s, GenericMessage.class);
        } catch (JsonProcessingException e) {
            throw new DecodeException(s, s, e);
        }
    }

    @Override
    public boolean willDecode(String s) {
        return true;
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}
