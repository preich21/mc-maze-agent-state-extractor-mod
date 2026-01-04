package de.kfru.ml.action;

import net.minecraft.client.MinecraftClient;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerActions {

    private final Map<Type, PlayerAction> actions = new ConcurrentHashMap<>();

    public boolean perform(final MinecraftClient client) {
        return perform(List.of(), client);
    }

    public boolean perform(final List<PlayerAction> newActions,  final MinecraftClient client) {
        for (final PlayerAction newAction : newActions) {
            addNewAction(newAction);
        }

        for (Entry<Type, PlayerAction> action : actions.entrySet()) {
            performAction(action, client);
        }
        return actions.isEmpty();
    }

    private void addNewAction(final PlayerAction action) {
        final PlayerAction previousAction = actions.put(Type.of(action), action);
        if (previousAction != null) {
            previousAction.cancel();
        }
    }

    private void performAction(final Entry<Type, PlayerAction> action, final MinecraftClient client) {
        if (action.getValue() == null) {
            return;
        }

        final boolean finished = action.getValue().perform(client);

        if (finished) {
            actions.remove(action.getKey());
        }
    }

    public void clear() {
        for (final PlayerAction action : actions.values()) {
            action.cancel();
        }
        actions.clear();
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
