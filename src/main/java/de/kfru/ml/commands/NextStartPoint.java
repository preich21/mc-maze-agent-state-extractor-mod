package de.kfru.ml.commands;

import com.mojang.brigadier.context.CommandContext;
import de.kfru.ml.util.StartPointsData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.WorldProperties;

import java.util.List;
import java.util.Optional;

public class NextStartPoint extends AbstractCommandHandler {

  public NextStartPoint() {
    super("nextstartpoint");
  }

  @Override
  public int handle(final CommandContext<ServerCommandSource> context) {
    final ServerCommandSource source = context.getSource();
    final MinecraftServer server = source.getServer();
    final ServerWorld world = source.getWorld();
    if (server == null) {
      source.sendError(Text.literal("Server not available."));
      return 0;
    }
    final List<StartPointsData.StartPoint> startPoints = StartPointsData.getSavedBlockData(server).getStartPoints();
    final ServerPlayerEntity player = server.getPlayerManager().getPlayer(source.getPlayer().getUuid());
    BlockPos pos = player.getRespawn().respawnData().getPos();
    Optional<StartPointsData.StartPoint> current = startPoints.stream().filter(startPoint -> startPoint.x() == pos.getX() && startPoint.y() == pos.getY() && startPoint.z() == pos.getZ()).findFirst();

    int nextPointIndex = 0;
    if (current.isPresent()) {
      nextPointIndex = (startPoints.indexOf(current.get()) + 1) % startPoints.size();
    } else {
      source.sendFeedback(() -> Text.literal("Couldn't find current start point, starting at 0"), true);
    }
    final StartPointsData.StartPoint newStartPoint = startPoints.get(nextPointIndex);
    final WorldProperties.SpawnPoint spawnPoint = new WorldProperties.SpawnPoint(new GlobalPos(world.getRegistryKey(), newStartPoint.toBlockPos()), newStartPoint.yaw(), newStartPoint.pitch());
    player.setSpawnPoint(new ServerPlayerEntity.Respawn(spawnPoint, true), true);
    player.kill(world);
    source.sendFeedback(() -> Text.literal(String.format("Teleported to next start point: \n%s", newStartPoint)), true);
    return 1;
  }
}
