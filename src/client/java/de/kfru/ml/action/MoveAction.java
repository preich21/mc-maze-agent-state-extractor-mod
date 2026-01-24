package de.kfru.ml.action;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

@SuperBuilder
public class MoveAction extends PlayerAction {

    @Getter
    private final Direction direction;

    @Override
    public void performUntilStop(final MinecraftClient client) {
        KeyBinding keyBinding = switch (direction) {
            case FORWARD -> client.options.forwardKey;
            case BACKWARD -> client.options.backKey;
            case LEFT -> client.options.leftKey;
            case RIGHT -> client.options.rightKey;
        };
        keyBinding.setPressed(true);
    }

    @Override
    public void stop(MinecraftClient client) {
        KeyBinding keyBinding = switch (direction) {
            case FORWARD -> client.options.forwardKey;
            case BACKWARD -> client.options.backKey;
            case LEFT -> client.options.leftKey;
            case RIGHT -> client.options.rightKey;
        };
        keyBinding.setPressed(false);
    }

    public enum Direction {
        FORWARD,
        BACKWARD,
        LEFT,
        RIGHT
    }
}
