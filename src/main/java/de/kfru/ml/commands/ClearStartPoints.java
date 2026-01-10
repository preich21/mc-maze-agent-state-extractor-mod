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

public class ClearStartPoints extends AbstractCommandHandler {

  public ClearStartPoints() {
    super("clearstartpoints");
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
    data.clear();

    source.sendFeedback(() -> Text.literal("Cleared start points."), true);
    return 1;
  }
}
