package de.kfru.ml.ws.messages;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResetMessage extends IncomingMessage {

    private boolean mazeGeneration;
    private Integer mazeSize;

    private static final Gson GSON = new Gson();

    public static ResetMessage fromJson(final String json) {
        return GSON.fromJson(json, ResetMessage.class);
    }
}
