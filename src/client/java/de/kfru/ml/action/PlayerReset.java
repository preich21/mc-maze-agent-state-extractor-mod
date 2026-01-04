package de.kfru.ml.action;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.math.Vec3d;

public class PlayerReset {

    @SuppressWarnings("DataFlowIssue") // client.player is never null when this method is called
    public static void perform(final MinecraftClient client) {
        client.player.refreshPositionAndAngles(new Vec3d(7.5, 1.0, -6.5), 90.0f, 0.0f);
        client.player.setVelocity(Vec3d.ZERO);
        client.player.getAbilities().flying = false;
        client.player.setSprinting(false);
        client.player.setSneaking(false);
        client.player.setPose(EntityPose.STANDING);

        client.player.extinguish();
        client.player.clearStatusEffects();
        client.player.setHealth(client.player.getMaxHealth());
        client.player.getHungerManager().setFoodLevel(20);
        client.player.getHungerManager().setSaturationLevel(5.0f);

        client.player.getInventory().clear();
        client.player.getInventory().setSelectedSlot(0);
    }
}
