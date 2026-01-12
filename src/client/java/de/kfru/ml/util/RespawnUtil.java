package de.kfru.ml.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.WorldProperties;

public final class RespawnUtil {
  private RespawnUtil() {}

  public static WorldProperties.SpawnPoint getPlayerRespawn(final MinecraftClient client) {
    final MinecraftServer server = client.getServer();
    final ServerPlayerEntity sp = server.getPlayerManager().getPlayer(client.player.getUuid());
    return sp.getRespawn().respawnData();
  }

  public static void setSpawnPoint(final MinecraftClient client, final BlockPos startPointPos, final float yaw, final float pitch) {
    if (client == null || client.player == null) throw new IllegalArgumentException("client or client.player is null");

    final MinecraftServer server = client.getServer();
    final ServerWorld spawnWorld = server.getOverworld();
    final ServerPlayerEntity sp = server.getPlayerManager().getPlayer(client.player.getUuid());
    final WorldProperties.SpawnPoint spawnPoint = new WorldProperties.SpawnPoint(new GlobalPos(spawnWorld.getSpawnPoint().getDimension(), startPointPos), yaw, pitch);
    sp.setSpawnPoint(new ServerPlayerEntity.Respawn(spawnPoint, true), true);
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
