package com.mtvu.websocketserver.domain;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum MessageAction {

    CREATE("create"), UPDATE("update");
    private String action;

    MessageAction(String action) {
        this.action = action;
    }

    @JsonValue
    public String getAction() {
        return action;
    }

    private static final Map<String, MessageAction> ENUM_MAP;

    static {
        Map<String, MessageAction> map = new ConcurrentHashMap<>();
        for (MessageAction instance : MessageAction.values()) {
            map.put(instance.getAction().toLowerCase(), instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static MessageAction get(String type) {
        return ENUM_MAP.get(type.toLowerCase());
    }
}
