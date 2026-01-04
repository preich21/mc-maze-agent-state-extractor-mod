package de.kfru.ml.action;

import lombok.experimental.SuperBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

@SuperBuilder
public class RotateCameraAction extends PlayerAction {

    private final float yawDeltaDegrees;
    private final float pitchDeltaDegrees;

    @Override
    public boolean perform(final MinecraftClient client) {
        final boolean stillRotating = ticksRemaining > 0;
        ticksRemaining--;

        if (stillRotating && client.player != null) {
            final float yaw = client.player.getYaw() + yawDeltaDegrees;
            final float pitch = MathHelper.clamp(client.player.getPitch() + pitchDeltaDegrees, -90.0f, 90.0f);

            client.player.setYaw(yaw);
            client.player.setPitch(pitch);
        }

        return !stillRotating;
    }
}
