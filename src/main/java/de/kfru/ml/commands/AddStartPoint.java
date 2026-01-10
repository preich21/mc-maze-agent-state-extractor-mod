package de.kfru.ml.commands;

import com.mojang.brigadier.context.CommandContext;
import de.kfru.ml.util.StartPointsData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddStartPoint extends AbstractCommandHandler {

  public static final Logger LOGGER = LoggerFactory.getLogger("AddStartPointCommand");

  public AddStartPoint() {
    super("startpoint");
  }

  @Override
  public int handle(final CommandContext<ServerCommandSource> context) {
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

    data.addStartPoint(new StartPointsData.StartPoint(pos.getX(), pos.getY(), pos.getZ()));
    source.sendFeedback(() -> Text.literal("Saved start point at " + pos.toShortString()), true);
    LOGGER.info("Saved start point {} for player {}", pos.toShortString(), player.getName().getString());
    return 1;
  }
}
