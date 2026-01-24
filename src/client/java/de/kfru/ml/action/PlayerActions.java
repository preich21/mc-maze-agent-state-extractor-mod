package de.kfru.ml.action;

import de.kfru.ml.ws.messages.ActionMessage;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class PlayerActions {

    private final Map<Type, PlayerAction> activeActions = new ConcurrentHashMap<>();
    private ActionMessage activeActionRequest;
    private Long actionStartTick;

    public void perform(final MinecraftClient client) {
        activeActions.forEach((key, value) -> value.performUntilStop(client));
    }

    public void updateActions(final ActionMessage actionMessage, final MinecraftClient client) {
        this.clear(client);
        this.activeActionRequest = actionMessage;
        this.actionStartTick = client.world.getTime();
        for (PlayerAction action : actionMessage.toPlayerActions()) {
            final PlayerAction previousAction = activeActions.put(Type.of(action), action);
            if (previousAction != null) {
                throw new IllegalArgumentException("Cannot use two actions of the same type at the same time: " + action.getClass().getName());
            }
        }
    }

    public void clear(final MinecraftClient client) {
        this.activeActions.forEach((type, action) -> action.stop(client));
        this.activeActions.clear();
        this.actionStartTick = null;
        this.activeActionRequest = null;
    }

    public enum Type {
        MOVE_FORWARD,
        MOVE_SIDEWARDS,
        JUMP,
        ROTATE_CAMERA,
        ;

        public static Type of(final PlayerAction action) {
            return switch (action) {
                case MoveAction moveAction -> switch (moveAction.getDirection()) {
                    case FORWARD, BACKWARD -> MOVE_FORWARD;
                    case LEFT, RIGHT -> MOVE_SIDEWARDS;
                };
                case JumpAction ignored -> JUMP;
                case RotateCameraAction ignored -> ROTATE_CAMERA;
                default -> throw new IllegalArgumentException("Unknown action type: " + action.getClass().getName());
            };
        }
    }
}
