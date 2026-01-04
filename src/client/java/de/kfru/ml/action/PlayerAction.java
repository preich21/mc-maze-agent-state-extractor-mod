package de.kfru.ml.action;

import lombok.experimental.SuperBuilder;
import net.minecraft.client.MinecraftClient;

@SuperBuilder
public abstract class PlayerAction {

    protected int ticksRemaining;

    public abstract boolean perform(MinecraftClient client);

    public void cancel() {
        ticksRemaining = 0;
    }
}
