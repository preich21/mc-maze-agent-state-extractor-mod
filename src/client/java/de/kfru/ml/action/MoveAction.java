package de.kfru.ml.action;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import net.minecraft.client.MinecraftClient;

@SuperBuilder
public class MoveAction extends PlayerAction {

    @Getter
    private final Direction direction;

    @Override
    public boolean perform(final MinecraftClient client) {
        final boolean stillPressing = ticksRemaining > 0;
        ticksRemaining--;

        switch (direction) {
            case FORWARD -> client.options.forwardKey.setPressed(stillPressing);
            case BACKWARD -> client.options.backKey.setPressed(stillPressing);
            case LEFT -> client.options.leftKey.setPressed(stillPressing);
            case RIGHT -> client.options.rightKey.setPressed(stillPressing);
        }

        return !stillPressing;
    }

    public enum Direction {
        FORWARD,
        BACKWARD,
        LEFT,
        RIGHT
    }
}
