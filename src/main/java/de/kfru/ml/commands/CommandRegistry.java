package de.kfru.ml.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

public class CommandRegistry {

  public static List<AbstractCommandHandler> commandHandlers = List.of(
      new AddStartPoint(),
      new GetStartPoints(),
      new ClearStartPoints()
  );

  public static void registerCommandHandlers(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
    commandHandlers.forEach(commandHandler -> {
      dispatcher.register(commandHandler.getCommand());
    });
  }
}
