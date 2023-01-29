package com.mtvu.websocketserver.domain.message;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum MessageType {
    COUPLE("couple"), GROUP("group");

    private String type;

    MessageType(String type) {
        this.type = type;
    }

    @JsonValue
    public String getType() {
        return type;
    }

    private static final Map<String, MessageType> ENUM_MAP;

    static {
        Map<String, MessageType> map = new ConcurrentHashMap<>();
        for (MessageType instance : MessageType.values()) {
            map.put(instance.getType().toLowerCase(), instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static MessageType get(String type) {
        return ENUM_MAP.get(type.toLowerCase());
    }
}