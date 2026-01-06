package de.kfru.ml.ws.messages;

import com.google.gson.Gson;
import de.kfru.ml.state.BlockType;
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

    private double yaw;
    private double pitch;

    private boolean died;
    private String standingOn;

    private List<Double> fovDistances;
    private List<Integer> fovBlocks;

    @SuppressWarnings("unused")
    public static class StateMessageBuilder {
        public StateMessageBuilder playerState(final PlayerState state) {
            this.x = (int) state.position().x();
            this.y = (int) state.position().y();
            this.z = (int) state.position().z();
            this.yaw = state.facing().yaw();
            this.pitch = state.facing().pitch();
            this.standingOn = state.standingOn().toString();
            final FieldOfView fov = state.fieldOfView();
            this.fovDistances = fov.getDistances();
            this.fovBlocks = fov.getBlocks();
//            System.out.println("FOV blocks contains goal: " + this.fovBlocks.contains(BlockType.GOAL_BLOCK.id));
            return this;
        }
    }

    public String toJson() {
        return GSON.toJson(this);
    }
}
