package de.kfru.ml;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.registry.Registries;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgenticStateExtractorClient implements ClientModInitializer {


	private static final Logger LOGGER = LoggerFactory.getLogger("agentic-state-extractor");

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null || client.world == null) return;

			final var player = client.player;
			final var world = client.world;

			final double playerX = player.getX();
			final double playerY = player.getY();
			final double playerZ = player.getZ();

			String lookingAt = "N/A";
			String lookingPos = "N/A";

			final HitResult hit = client.crosshairTarget;
			if (hit instanceof BlockHitResult block) {
				final var state = world.getBlockState(block.getBlockPos());
				lookingAt = Registries.BLOCK.getId(state.getBlock()).toString() + block.getSide();
				lookingPos = block.getBlockPos().toShortString();
			}

			LOGGER.info("Player Position: ({}, {}, {}), Looking at [{}] at position [{}]", playerX, playerY, playerZ, lookingAt, lookingPos);
		});
	}
}