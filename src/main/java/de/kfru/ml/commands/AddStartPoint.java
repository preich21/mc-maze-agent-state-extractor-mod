package de.kfru.ml.commands;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.kfru.ml.util.StartPointsData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AddStartPoint extends AbstractCommandHandler {

  public static final Logger LOGGER = LoggerFactory.getLogger("AddStartPointCommand");

  private static final String ARG_WEIGHT = "weight";

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

    data.addStartPoint(new StartPointsData.StartPoint(weight, pos.getX(), pos.getY(), pos.getZ(), player.getYaw(), player.getPitch()));
    source.sendFeedback(() -> Text.literal("Saved start point at " + pos.toShortString() + " (weight=" + weight + ")"), true);
    LOGGER.info("Saved start point {} (weight={}) for player {}", pos.toShortString(), weight, player.getName().getString());
    return 1;
  }
}
