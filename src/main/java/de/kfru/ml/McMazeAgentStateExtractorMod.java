package de.kfru.ml;

import de.kfru.ml.commands.AbstractCommandHandler;
import de.kfru.ml.commands.AddStartPoint;
import de.kfru.ml.commands.ClearStartPoints;
import de.kfru.ml.commands.GetStartPoints;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class McMazeAgentStateExtractorMod implements ModInitializer {
	public static final String MOD_ID = "mc-maze-agent-state-extractor-mod";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		List<AbstractCommandHandler> commandHandlers = List.of(
				new AddStartPoint(),
				new GetStartPoints(),
				new ClearStartPoints()
		);

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			commandHandlers.forEach(commandHandler -> dispatcher.register(CommandManager.literal(commandHandler.getCommandName()).executes(ctx -> commandHandler.handle(ctx))));
		});

		LOGGER.info("McMazeAgentStateExtractorMod initialized.");
	}


}
