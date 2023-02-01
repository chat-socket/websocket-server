package com.mtvu.websocketserver.domain.message;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum MessageContentType {
    TEXT("text"), RECORDING("recording");

    private String type;

    MessageContentType(String type) {
        this.type = type;
    }

    @JsonValue
    public String getType() {
        return type;
    }

    private static final Map<String, MessageContentType> ENUM_MAP;

    static {
        Map<String, MessageContentType> map = new ConcurrentHashMap<>();
        for (MessageContentType instance : MessageContentType.values()) {
            map.put(instance.getType().toLowerCase(), instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static MessageContentType get(String type) {
        return ENUM_MAP.get(type.toLowerCase());
    }
}
