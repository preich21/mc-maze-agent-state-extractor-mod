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
import net.minecraft.util.math.Vec3d;
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
        resetPlayer(context);
        resetMaze(payload, context);
        logger.info("Server-side reset successful.");
    }

    private void resetPlayer(ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            context.player().setVelocity(Vec3d.ZERO);
            context.player().getAbilities().flying = false;
            context.player().setSprinting(false);
            context.player().setSneaking(false);
            context.player().setPose(net.minecraft.entity.EntityPose.STANDING);

            context.player().teleport(1.5f, 1.0f, 1.5f, false);

            context.player().extinguish();
            context.player().clearStatusEffects();
            context.player().setHealth(context.player().getMaxHealth());
            context.player().getHungerManager().setFoodLevel(20);
            context.player().getHungerManager().setSaturationLevel(5.0f);

            context.player().getInventory().clear();
            context.player().getInventory().setSelectedSlot(0);
        });
    }

    private void resetMaze(ResetMazePayload payload, ServerPlayNetworking.Context context) {
        final ServerWorld world = context.player().getEntityWorld();
        final Maze maze = mazeGenerator.getMaze(payload.size());
        logger.info(maze.toString());
        final boolean[][] mazeWalls = mazePlacer.placeMazeInWorld(world, new BlockPos(0, 0, 0), 0, 3, maze);
        ServerPlayNetworking.send(context.player(), new ResetSuccessfulPayload(mazeWalls));
    }
}
