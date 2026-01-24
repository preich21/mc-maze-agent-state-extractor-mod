package de.kfru.ml;

import de.kfru.ml.commands.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

		CommandRegistrationCallback.EVENT.register(CommandRegistry::registerCommandHandlers);
		ServerTickEvents.END_SERVER_TICK.register(this::killPlayersIfBelow0);

		LOGGER.info("McMazeAgentStateExtractorMod initialized.");
	}

	private void killPlayersIfBelow0(final MinecraftServer server) {
		for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
			if (player.isAlive() && !player.getGameMode().isCreative() && player.getY() < 0) {
				// Kill player properly
				player.damage(server.getOverworld(), player.getDamageSources().outOfWorld(), Float.MAX_VALUE);
			}
		}
	}


}
