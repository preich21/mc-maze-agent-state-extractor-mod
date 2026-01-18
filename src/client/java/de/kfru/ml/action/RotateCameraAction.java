package de.kfru.ml.action;

import lombok.experimental.SuperBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

@SuperBuilder
public class RotateCameraAction extends PlayerAction {

    private boolean done;

    private final float yawDeltaDegrees; // horizontal rotation - 360° to left and right possible
    private final float pitchDeltaDegrees; // vertical rotation - max 90° up and down

    @Override
    public void performUntilStop(final MinecraftClient client) {

        if (!done && client.player != null) {
            final float yaw = client.player.getYaw() + yawDeltaDegrees;
            final float pitch = MathHelper.clamp(client.player.getPitch() + pitchDeltaDegrees, -90.0f, 90.0f);

            client.player.setYaw(yaw);
            client.player.setPitch(pitch);

            done = true;
        }
    }

    @Override
    public void stop(MinecraftClient client) {
        // No continuous action to stop
    }
}
