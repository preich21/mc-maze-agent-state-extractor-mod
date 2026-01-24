package de.kfru.ml.action;

import de.kfru.ml.util.RespawnUtil;
import de.kfru.ml.util.StartPointsData;
import de.kfru.ml.ws.messages.ResetMessage;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldProperties;

public class PlayerReset {

    final static int SPAWN_POINT_RADIUS = 50;

    final static BlockState AIR = Blocks.AIR.getDefaultState();

    @SuppressWarnings("DataFlowIssue") // client.player is never null when this method is called
    public static void perform(final MinecraftClient client, ResetMessage resetMessage) {
        final StartPointsData.StartPoint startPoint = resetMessage.getStartPoint();
        final BlockPos spawnPoint = new BlockPos(0, 0, 0);
        final float yaw;
        float pitch;
//        if (startPoint != null) {
            copyStartPointToRespawn(client, startPoint, spawnPoint, resetMessage.getStartPointRotation());
            pitch = startPoint.pitch();
            yaw = startPoint.yaw() + switch (resetMessage.getStartPointRotation()) {
                case KEEP_POSITION -> 0;
                case ROTATE_90 -> -90;
                case ROTATE_180 -> 180;
                case ROTATE_270 -> 90;
            };
//        } else {
//          pitch = spawnPoint.pitch();
//          yaw = spawnPoint.yaw();
//        }
      System.out.println("Resetting player to spawn point at " + spawnPoint + " with yaw " + yaw + " and pitch " + pitch);
        IntegratedServer server = client.getServer();
        RespawnUtil.setSpawnPoint(client, spawnPoint, yaw, pitch);
        client.player.requestRespawn();
        client.player.refreshPositionAndAngles(spawnPoint, yaw, pitch);
        client.player.setYaw(yaw);
        client.player.setPitch(pitch);
//        server.executeSync(() -> {
//            final ServerPlayerEntity sp = server.getPlayerManager().getPlayer(client.player.getUuid());
////            server.getPlayerManager().respawnPlayer(sp, false, Entity.RemovalReason.DISCARDED);
////            sp.setHealth(20.0f);
////            sp.teleport(spawnPoint.getPos().getX() + 0.5, spawnPoint.getPos().getY() + 0.5, spawnPoint.getPos().getZ() + 0.5, true);
////            sp.setYaw(yaw);
////            sp.setPitch(pitch);
//            sp.setVelocity(Vec3d.ZERO);
//            sp.getAbilities().flying = false;
//            sp.setSprinting(false);
//            sp.setSneaking(false);
//            sp.setPose(EntityPose.STANDING);
//            sp.extinguish();
//            sp.clearStatusEffects();
//            sp.setHealth(client.player.getMaxHealth());
//            sp.getHungerManager().setFoodLevel(20);
//            sp.getHungerManager().setSaturationLevel(5.0f);
//            sp.getInventory().clear();
//            sp.getInventory().setSelectedSlot(0);
//        });
    }

    private static void copyStartPointToRespawn(final MinecraftClient client, final StartPointsData.StartPoint startPoint, final BlockPos dstCenter, final ResetMessage.StartPointRotation rotation) {
        final BlockPos srcCenter = startPoint.toBlockPos();
        final IntegratedServer server = client.getServer();
        final ServerWorld world = server.getOverworld();
        // get all surrounding blocks in 50 block range around the spawn point and copy them to 0 0 0

        server.executeSync(() -> {
            // TODO load relevant chunks
            for (int x = -SPAWN_POINT_RADIUS; x < SPAWN_POINT_RADIUS; x++) {
                for (int y = -1; y < 20; y++) {
                    for (int z = -SPAWN_POINT_RADIUS; z < SPAWN_POINT_RADIUS; z++) {
                        int[] rotated = rotateXZ(x, z, rotation);
                        int rx = rotated[0];
                        int rz = rotated[1];

                        final BlockPos srcBlock = srcCenter.add(rx, y, rz);
                        final BlockPos destBlock = dstCenter.add(x, y, z);

                        final BlockState srcState = world.getBlockState(srcBlock);
                        final BlockState destState = world.getBlockState(destBlock);
                        if (!srcState.getBlock().equals(destState.getBlock())) {
//                            System.out.println("source and dest block differ");
                            world.setBlockState(destBlock, srcState);
                        }
                    }
                }
            }
        });

    }

    private static int[] rotateXZ(int x, int z, final ResetMessage.StartPointRotation rotation) {
        return switch (rotation) {
            case KEEP_POSITION -> new int[]{x, z};
            case ROTATE_90 -> new int[]{-z, x};
            case ROTATE_180 -> new int[]{-x, -z};
            case ROTATE_270 -> new int[]{z, -x};
        };
    }
}
