package de.kfru.ml.commands;

import com.mojang.brigadier.context.CommandContext;
import lombok.Getter;
import net.minecraft.server.command.ServerCommandSource;

@Getter
public abstract class AbstractCommandHandler {

  protected final String commandName;

  public AbstractCommandHandler(String commandName) {
    this.commandName = commandName;
  }

  public abstract int handle(final CommandContext<ServerCommandSource> context);

}
