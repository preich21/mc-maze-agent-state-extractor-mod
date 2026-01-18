package de.kfru.ml.ws.messages;

import de.kfru.ml.action.PlayerActions;
import de.kfru.ml.state.FieldOfView;
import de.kfru.ml.state.PlayerState;
import lombok.Builder;

import java.util.List;

@Builder(builderClassName = "StateMessageBuilder")
public class StateMessage extends OutgoingMessage {

    private final MessageType type = MessageType.OBSERVATION;
    private long tick;

    private Long actionStartedTick;
    private ActionMessage activeActionRequest;

    private int x;
    private int y;
    private int z;

    private double yaw; // horizontal rotation - 360° to left and right possible
    private double pitch; // vertical rotation - max 90° up and down

    private boolean died;
    private int standingOn;

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
            this.standingOn = state.standingOn().id;
            final FieldOfView fov = state.fieldOfView();
            this.fovDistances = fov.getDistances();
            this.fovBlocks = fov.getBlocks();
//            System.out.println("FOV blocks contains goal: " + this.fovBlocks.contains(BlockType.GOAL_BLOCK.id));
            this.died = state.died().died();
            return this;
        }

        public StateMessageBuilder activeActions(final PlayerActions activeActions) {
            this.actionStartedTick = activeActions.getActionStartTick();
            this.activeActionRequest = activeActions.getActiveActionRequest();
            return this;
        }
    }
}
