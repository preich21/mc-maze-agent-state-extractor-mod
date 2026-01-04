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

    private int episode;
    private int step;

    private int applyForTicks;

    private float moveForward;
    private float moveSidewards;

    private boolean jump;

    private float yawDelta;
    private float pitchDelta;

    public static ActionMessage fromJson(final String json){
        return GSON.fromJson(json, ActionMessage.class);
    }

    public List<PlayerAction> toPlayerActions() {
        final List<PlayerAction> actions = new ArrayList<>();

        if (moveForward != 0) {
            final MoveAction.Direction direction = moveForward > 0 ? MoveAction.Direction.FORWARD : MoveAction.Direction.BACKWARD;
            actions.add(MoveAction.builder()
                    .direction(direction)
                    .ticksRemaining(applyForTicks)
                    .build());
        }

        if (moveSidewards != 0) {
            final MoveAction.Direction direction = moveSidewards > 0 ? MoveAction.Direction.RIGHT : MoveAction.Direction.LEFT;
            actions.add(MoveAction.builder()
                    .direction(direction)
                    .ticksRemaining(applyForTicks)
                    .build());
        }

        if (jump) {
            actions.add(JumpAction.builder()
                    .ticksRemaining(applyForTicks)
                    .build());
        }

        if (yawDelta != 0 || pitchDelta != 0) {
            actions.add(RotateCameraAction.builder()
                    .yawDeltaDegrees(yawDelta)
                    .pitchDeltaDegrees(pitchDelta)
                    .ticksRemaining(applyForTicks)
                    .build());
        }

        return actions;
    }
}
