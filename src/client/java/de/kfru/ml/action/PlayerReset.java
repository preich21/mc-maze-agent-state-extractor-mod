package de.kfru.ml.action;

import de.kfru.ml.util.RespawnUtil;
import de.kfru.ml.util.StartPointsData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class PlayerReset {

//    private static List<Integer> startPoints = new ArrayList<>();

    @SuppressWarnings("DataFlowIssue") // client.player is never null when this method is called
    public static void perform(final MinecraftClient client, final int startPointNonce) {
        final StartPointsData startPoints = StartPointsData.getSavedBlockData(client.getServer());
        final StartPointsData.StartPoint startPoint = PlayerReset.pickStartPoint(startPoints.getStartPoints(), startPointNonce);
//        Vec3d spawnPos = RespawnUtil.getPlayerRespawnPosSingleplayer(client);
        BlockPos spawnPoint = new BlockPos(startPoint.x(), startPoint.y(), startPoint.z());
        RespawnUtil.setSpawnPoint(client, spawnPoint);

        client.player.refreshPositionAndAngles(spawnPoint, 0.0F, 0.0F);
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

    public static StartPointsData.StartPoint pickStartPoint(final List<StartPointsData.StartPoint> startPoints, int startPointNonce) {
        if (startPoints == null || startPoints.isEmpty()) {
            throw new IllegalStateException("startPoints is empty");
        }

        int n = startPoints.size();
        int idx = Math.floorMod(startPointNonce, n); // deterministic, handles negatives
        return startPoints.get(idx);
    }
}
