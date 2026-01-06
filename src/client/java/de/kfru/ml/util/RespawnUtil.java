package de.kfru.ml.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class RespawnUtil {
  private RespawnUtil() {}

  public static Vec3d getPlayerRespawnPosSingleplayer(final MinecraftClient client) {
    if (client == null || client.player == null) return Vec3d.ZERO;

    final MinecraftServer server = client.getServer();
    if (server == null) return getPlayerRespawnClientBased(client.world);

    final ServerPlayerEntity sp = server.getPlayerManager().getPlayer(client.player.getUuid());
    if (sp == null) return getPlayerRespawnClientBased(client.world);

    final ServerWorld spawnWorld = server.getOverworld();
    if (spawnWorld == null) return getPlayerRespawnClientBased(client.world);

    final BlockPos spawnPos = sp.getRespawn().respawnData().getPos();
    if (spawnPos != null) {
      // Player-specific spawn (bed/anchor position as stored on the server).
      return spawnPos.toCenterPos();
    }

    // Fallback: world spawn (not player-specific).
    return spawnWorld.getSpawnPoint().getPos().toCenterPos();
  }

  public static Vec3d getPlayerRespawnClientBased(ClientWorld world) {
    return world.getSpawnPoint().getPos().toCenterPos();
  }

//  public static void teleportPlayerToRespawnSingleplayer(final MinecraftClient client) {
//    if (client == null || client.player == null) return;
//
//    final Vec3d pos = getPlayerRespawnPosSingleplayer(client);
//
//    final MinecraftServer server = client.getServer();
//    if (server == null) return;
//
//    final ServerPlayerEntity sp = server.getPlayerManager().getPlayer(client.player.getUuid());
//    if (sp == null) return;
//
//    sp.teleport(sp.getServerWorld(), pos.x, pos.y, pos.z, sp.getYaw(), sp.getPitch());
//  }

  /**
   * If you want "where the player would respawn" (including validity checks),
   * use the server-side finder, which mimics vanilla respawn rules.
   */
//  public static Vec3d findFinalRespawnPosSingleplayer(final MinecraftClient client) {
//    if (client == null || client.player == null) return Vec3d.ZERO;
//
//    final MinecraftServer server = client.getServer();
//    if (server == null) return Vec3d.ofCenter(client.world.getSpawnPos());
//
//    final ServerPlayerEntity sp = server.getPlayerManager().getPlayer(client.player.getUuid());
//    if (sp == null) return Vec3d.ofCenter(client.world.getSpawnPos());
//
//    final ServerWorld spawnWorld = server.getWorld(sp.getSpawnPointDimension());
//    if (spawnWorld == null) return Vec3d.ofCenter(client.world.getSpawnPos());
//
//    final BlockPos spawnPos = sp.getSpawnPointPosition();
//    final float spawnAngle = sp.getSpawnAngle();
//    final boolean forced = sp.isSpawnForced();
//
//    if (spawnPos != null) {
//      final Optional<Vec3d> resolved = ServerPlayerEntity.findRespawnPosition(spawnWorld, spawnPos, spawnAngle, forced, true);
//      if (resolved.isPresent()) return resolved.get();
//    }
//
//    return Vec3d.ofCenter(spawnWorld.getSpawnPos());
//  }
}
