package de.kfru.ml.action;

import lombok.experimental.SuperBuilder;
import net.minecraft.client.MinecraftClient;

@SuperBuilder
public abstract class PlayerAction {

    protected int ticksRemaining;

    public static PlayerAction of(final String actionType) {
        // TODO
        return switch (actionType) {
            case "MOVE" -> MoveAction.builder()
                    .direction(MoveAction.Direction.FORWARD)
                    .ticksRemaining(100)
                    .build();
            case "JUMP" -> JumpAction.builder()
                    .ticksRemaining(40)
                    .build();
            case "CAMERA" -> RotateCameraAction.builder()
                    .yawDeltaDegrees(10.0f)
                    .pitchDeltaDegrees(0.0f)
                    .ticksRemaining(20)
                    .build();
            default -> throw new IllegalArgumentException("Unknown action type: " + actionType);
        };
    }

    public abstract boolean perform(MinecraftClient client);

    public void cancel() {
        ticksRemaining = 0;
    }
}
