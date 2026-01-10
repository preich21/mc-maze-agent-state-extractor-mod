package de.kfru.ml.commands;

import com.mojang.brigadier.context.CommandContext;
import de.kfru.ml.util.StartPointsData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class GetStartPoints extends AbstractCommandHandler {

  public GetStartPoints() {
    super("startpoints");
  }

  @Override
  public int handle(final CommandContext<ServerCommandSource> context) {
    final ServerCommandSource source = context.getSource();
    final MinecraftServer server = source.getServer();
    if (server == null) {
      source.sendError(Text.literal("Server not available."));
      return 0;
    }
    StartPointsData data = StartPointsData.getSavedBlockData(server);
    source.sendFeedback(() -> Text.literal(String.format("Start points (%s): \n%s", data.getStartPoints().size(), data.toString())), true);
    return 1;
  }
}
