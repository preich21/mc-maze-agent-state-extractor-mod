package de.kfru.ml.commands;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.kfru.ml.util.StartPointsData;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AddStartPoint extends AbstractCommandHandler {

  public static final Logger LOGGER = LoggerFactory.getLogger("AddStartPointCommand");

  private static final String ARG_WEIGHT = "weight";
  final static int SPAWN_POINT_RADIUS = 50;

  public AddStartPoint() {
    super("startpoint", List.of(CommandManager.argument(ARG_WEIGHT, FloatArgumentType.floatArg())));
  }

  @Override
  public int handle(final CommandContext<ServerCommandSource> context) {
    final Float weight = this.getArgumentValue(context, ARG_WEIGHT, FloatArgumentType::getFloat, false);

    final ServerCommandSource source = context.getSource();
    final ServerPlayerEntity player;
    try {
      player = source.getPlayer();
    } catch (Exception e) {
      source.sendError(Text.literal("This command can only be used by a player."));
      return 0;
    }

    final MinecraftServer server = source.getServer();
    if (server == null) {
      source.sendError(Text.literal("Server not available."));
      return 0;
    }

    final BlockPos pos = player.getBlockPos();
    StartPointsData data = StartPointsData.getSavedBlockData(server);
    if (data == null) {
      source.sendError(Text.literal("Could not access StartPointsData."));
      return 0;
    }

    final BlockPos goalPoint = this.getGoalPoint(pos, server.getOverworld());
    final StartPointsData.StartPoint startPoint = new StartPointsData.StartPoint(weight, pos, player.getYaw(), player.getPitch(), goalPoint);
    if (data.containsStartPoint(pos.getX(), pos.getY(), pos.getZ())) {
      data.updateStartPoint(startPoint);
      source.sendFeedback(() -> Text.literal("Updated start point at " + pos.toShortString() + " (weight=" + weight + ")"), true);
      LOGGER.info("Updated start point {} (weight={}) for player {}", pos.toShortString(), weight, player.getName().getString());
    } else {
      data.addStartPoint(startPoint);
      source.sendFeedback(() -> Text.literal("Saved start point at " + pos.toShortString() + " (weight=" + weight + ")"), true);
      LOGGER.info("Saved start point {} (weight={}) for player {}", pos.toShortString(), weight, player.getName().getString());
    }
    return 1;
  }

  private BlockPos getGoalPoint(final BlockPos startPoint, final ServerWorld world) {
    for (int x = -SPAWN_POINT_RADIUS; x < SPAWN_POINT_RADIUS; x++) {
      for (int y = -1; y < 20; y++) {
        for (int z = -SPAWN_POINT_RADIUS; z < SPAWN_POINT_RADIUS; z++) {
          final BlockPos block = startPoint.add(x, y, z);
          if (world.getBlockState(block).getBlock().equals(Blocks.DIAMOND_BLOCK)) {
            return block;
          }
        }
      }
    }
    throw new IllegalStateException("No goal block found in vicinity of start point " + startPoint.toShortString());
  }
}
