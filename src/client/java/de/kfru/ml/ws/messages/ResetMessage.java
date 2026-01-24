package de.kfru.ml.ws.messages;

import com.google.gson.Gson;
import de.kfru.ml.action.*;
import de.kfru.ml.util.StartPointsData;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResetMessage extends IncomingMessage {

    private static final Gson GSON = new Gson();
    private long seed;
    private StartPointsData.StartPoint startPoint;
    private StartPointRotation startPointRotation;

    public static ResetMessage fromJson(final String json) {
        return GSON.fromJson(json, ResetMessage.class);
    }

    public enum StartPointRotation {
        KEEP_POSITION,
        ROTATE_90,
        ROTATE_180,
        ROTATE_270
    }
}
