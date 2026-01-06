package de.kfru.ml.action;

import de.kfru.ml.util.RespawnUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.math.Vec3d;

public class PlayerReset {

    @SuppressWarnings("DataFlowIssue") // client.player is never null when this method is called
    public static void perform(final MinecraftClient client) {
        Vec3d spawnPos = RespawnUtil.getPlayerRespawnPosSingleplayer(client);
        client.player.refreshPositionAndAngles(spawnPos, 0.0F, 0.0F);
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
