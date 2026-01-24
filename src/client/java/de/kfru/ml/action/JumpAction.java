package de.kfru.ml.action;

import lombok.experimental.SuperBuilder;
import net.minecraft.client.MinecraftClient;

@SuperBuilder
public class JumpAction extends PlayerAction {

    @Override
    public void performUntilStop(final MinecraftClient client) {
        client.options.jumpKey.setPressed(true);
    }

    @Override
    public void stop(MinecraftClient client) {
        client.options.jumpKey.setPressed(false);
    }
}
