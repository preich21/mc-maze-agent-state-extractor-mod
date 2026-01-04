package de.kfru.ml.action;

import lombok.experimental.SuperBuilder;
import net.minecraft.client.MinecraftClient;

@SuperBuilder
public class JumpAction extends PlayerAction {

    @Override
    public boolean perform(final MinecraftClient client) {
        final boolean stillPressing = ticksRemaining > 0;
        ticksRemaining--;

        client.options.jumpKey.setPressed(stillPressing);

        return !stillPressing;
    }
}
