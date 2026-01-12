package de.kfru.ml.ws.messages;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public enum MessageType {
    HELLO,
    ACTION_REQUEST,
    RESET_REQUEST,
    STATE_AFTER_RESET,
    STATE_AFTER_ACTION,
    ;

    private static final Gson GSON = new Gson();

    public static MessageType of(final String incomingMessage) {
        final JsonObject json = GSON.fromJson(incomingMessage, JsonObject.class);
        return valueOf(json.get("type").getAsString());
    }
}
