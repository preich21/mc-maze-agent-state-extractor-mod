package de.kfru.ml.ws.messages;

import com.google.gson.Gson;
import de.kfru.ml.state.FieldOfView;
import de.kfru.ml.state.PlayerState;
import lombok.Builder;

import java.util.List;

@Builder(builderClassName = "StateMessageBuilder")
public class StateMessage {

    private static final Gson GSON = new Gson();

    private MessageType type;
    private boolean died;
    private int episode;
    private int step;
    private long tickStart;
    private long tickEnd;

    private int x;
    private int y;
    private int z;

    private double yaw;
    private double pitch;

    private String standingOn;

    private List<FieldOfView.BlockInFOV> fieldOfView;

    @SuppressWarnings("unused")
    public static class StateMessageBuilder {
        public StateMessageBuilder playerState(final PlayerState state) {
            this.x = (int) state.position().x();
            this.y = (int) state.position().y();
            this.z = (int) state.position().z();
            this.yaw = state.facing().yaw();
            this.pitch = state.facing().pitch();
            this.standingOn = state.standingOn().toString();
            this.fieldOfView = state.fieldOfView().blocksInFOV();
            return this;
        }
    }

    public String toJson() {
        return GSON.toJson(this);
    }
}
