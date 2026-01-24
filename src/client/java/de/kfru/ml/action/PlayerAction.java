package de.kfru.ml.action;

import lombok.experimental.SuperBuilder;
import net.minecraft.client.MinecraftClient;

@SuperBuilder
public abstract class PlayerAction {

    public abstract void performUntilStop(MinecraftClient client);
    public abstract void stop(MinecraftClient client);

}
