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
    private int episode;
    private int step;
    private long tickStart;
    private long tickEnd;

    private int x;
    private int y;
    private int z;

    private double yaw; // horizontal rotation - 360° to left and right possible
    private double pitch; // vertical rotation - max 90° up and down

    private boolean died;
    private int standingOn;

    private List<Double> fovDistances;
    private List<Integer> fovBlocks;

    private boolean[][] maze; // only in STATE_AFTER_RESET messages

    @SuppressWarnings("unused")
    public static class StateMessageBuilder {
        public StateMessageBuilder playerState(final PlayerState state) {
            this.x = (int) state.position().x();
            this.y = (int) state.position().y();
            this.z = (int) state.position().z();
            this.yaw = state.facing().yaw();
            this.pitch = state.facing().pitch();
            this.standingOn = state.standingOn().id;
            final FieldOfView fov = state.fieldOfView();
            this.fovDistances = fov.getDistances();
            this.fovBlocks = fov.getBlocks();
            return this;
        }
    }

    public String toJson() {
        return GSON.toJson(this);
    }
}
