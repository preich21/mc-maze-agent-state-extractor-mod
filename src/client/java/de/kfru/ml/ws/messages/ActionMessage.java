package de.kfru.ml.ws.messages;

import com.google.gson.Gson;
import de.kfru.ml.action.JumpAction;
import de.kfru.ml.action.MoveAction;
import de.kfru.ml.action.PlayerAction;
import de.kfru.ml.action.RotateCameraAction;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class ActionMessage extends IncomingMessage {

    private static final Gson GSON = new Gson();

    private boolean moveForward;
    private boolean moveBackward;
    private boolean moveLeft;
    private boolean moveRight;

    private boolean jump;

    private float yawDelta;
    private float pitchDelta;

    public static ActionMessage fromJson(final String json){
        return GSON.fromJson(json, ActionMessage.class);
    }

    public List<PlayerAction> toPlayerActions() {
        final List<PlayerAction> actions = new ArrayList<>();

        if (moveForward || moveBackward) {
            if (moveForward && moveBackward) throw new IllegalArgumentException("Cannot move forward and backward at the same time.");
            final MoveAction.Direction direction = moveForward ? MoveAction.Direction.FORWARD : MoveAction.Direction.BACKWARD;
            actions.add(MoveAction.builder()
                .direction(direction)
                .build());
        }

        if (moveLeft || moveRight) {
            if (moveLeft && moveRight) throw new IllegalArgumentException("Cannot move left and right at the same time.");
            final MoveAction.Direction direction = moveLeft ? MoveAction.Direction.LEFT : MoveAction.Direction.RIGHT;
            actions.add(MoveAction.builder()
                .direction(direction)
                .build());
        }

        if (jump) {
            actions.add(JumpAction.builder()
                .build());
        }

        if (yawDelta != 0 || pitchDelta != 0) {
            actions.add(RotateCameraAction.builder()
                .yawDeltaDegrees(yawDelta)
                .pitchDeltaDegrees(pitchDelta)
                .build());
        }

        return actions;
    }
}
