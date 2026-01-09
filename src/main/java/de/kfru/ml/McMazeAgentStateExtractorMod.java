package de.kfru.ml;

import de.kfru.ml.communication.ResetMazePayload;
import de.kfru.ml.communication.ResetSuccessfulPayload;
import de.kfru.ml.maze.MazeGenerator;
import de.kfru.ml.maze.MazePlacer;
import de.kfru.ml.maze.entity.Maze;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class McMazeAgentStateExtractorMod implements ModInitializer {
    public static final String MOD_ID = "mc-maze-agent-state-extractor-mod";

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger logger = LoggerFactory.getLogger(MOD_ID);

    private final MazeGenerator mazeGenerator = new MazeGenerator();
    private final MazePlacer mazePlacer = new MazePlacer();

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        PayloadTypeRegistry.playC2S().register(ResetMazePayload.ID, ResetMazePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ResetSuccessfulPayload.ID, ResetSuccessfulPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ResetMazePayload.ID, (payload, context) -> {
            logger.info("Received RESET packet from player.");

            @SuppressWarnings("resource") // if we autoclose the server it will shut down
            final MinecraftServer server = context.server();
            server.execute(() -> {
                try {
                    onReset(payload, context);
                } catch (final Throwable e) {
                    logger.error("Error during RESET:", e);
                }
            });
        });

        logger.info("Initialized McMazeAgentStateExtractorMod successfully.");
    }

    private void onReset(final ResetMazePayload payload, final ServerPlayNetworking.Context context) {
        final ServerWorld world = context.player().getEntityWorld();
        final Maze maze = mazeGenerator.getMaze(payload.size());
        logger.info(maze.toString());
		mazePlacer.placeMazeInWorld(world, new BlockPos(0, 0, 0), 0, 3, maze);
		ServerPlayNetworking.send(context.player(), new ResetSuccessfulPayload());
        logger.info("Reset successful.");
    }
}
