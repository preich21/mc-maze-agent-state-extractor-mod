package de.kfru.ml;

import de.kfru.ml.action.PlayerActions;
import de.kfru.ml.action.PlayerReset;
import de.kfru.ml.state.PlayerState;
import de.kfru.ml.ws.AgentWebsocketServer;
import de.kfru.ml.ws.messages.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.integrated.IntegratedServerLoader;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class McMazeAgentStateExtractorModClient implements ClientModInitializer {

    private static final Logger logger = LoggerFactory.getLogger("mc-maze-agent-state-extractor-mod-client");
    private static final PlayerActions activeActions = new PlayerActions();

    private AgentWebsocketServer ws;


    private final List<Consumer<MinecraftClient>> nextTickCallbacks = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        String websocketPortEnv = System.getenv("WS_PORT");
        int websocketPort = websocketPortEnv != null ? Integer.parseInt(websocketPortEnv) : 8081;
        ws = new AgentWebsocketServer("127.0.0.1", websocketPort);
        ws.start();

        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
        ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register(ws::onWorldChange);
        String joinWorldOnStart = System.getenv("JOIN_WORLD_ON_START");
        if (joinWorldOnStart != null) {
          logger.info("Configured to join world '{}' on client start.", joinWorldOnStart);
          ClientLifecycleEvents.CLIENT_STARTED.register(client -> this.joinWorld(client, joinWorldOnStart));
        }

        disablePauseMenuWhenInBackground();

        logger.info("McMazeAgentStateExtractorModClient initialized successfully.");
    }

    private void joinWorld(MinecraftClient client, String worldName) {
      IntegratedServerLoader integratedServerLoader = client.createIntegratedServerLoader();
      integratedServerLoader.start(worldName, () -> logger.info("Cancelled joining world {}", worldName));
    }

    private void disablePauseMenuWhenInBackground() {
        MinecraftClient.getInstance().execute(() -> {
            MinecraftClient.getInstance().options.pauseOnLostFocus = false;
            logger.info("Disabled pause on lost focus.");
        });
    }

    private void onTick(final MinecraftClient client) {
        if (client.player == null || client.world == null) return;

        if (!this.ws.isConnected()) {
            activeActions.clear(client);
        }

        final ResetMessage resetMessage = ws.resetMessage.getAndSet(null);
        if (resetMessage != null) {
            long start = System.currentTimeMillis();
            activeActions.clear(client);
            PlayerReset.perform(client, resetMessage);
            client.player.sendMessage(Text.of("Starting episode " + resetMessage.getEpisode() + ". Resetting player to start point at " + resetMessage.getStartPoint()), false);
            logger.info("Reset performed in {} ms.", System.currentTimeMillis() - start);
            logger.info("Reset executed.");
            ws.broadcast(buildStateMessage(client).toBytes());
            return;
        }
        final ActionMessage actionMessage = ws.latestAction.getAndSet(null);
        if (actionMessage != null) {
            activeActions.updateActions(actionMessage, client);
        }
        ws.broadcast(buildStateMessage(client).toBytes());
        activeActions.perform(client);
    }


//    private void onNextTick(final Consumer<MinecraftClient> callback) {
//        this.nextTickCallbacks.add(callback);
//    }
//
//    private void runNextTickCallbacks(final MinecraftClient client) {
//        for (Consumer<MinecraftClient> callback : nextTickCallbacks) {
//            try {
//                callback.accept(client);
//            } catch (Exception e) {
//                logger.warn("Failed to run next tick callback. {}", e.getMessage());
//            }
//        }
//        nextTickCallbacks.clear();
//    }

    @SuppressWarnings("DataFlowIssue") // client.player and client.world have already been checked to be not null
    private StateMessage buildStateMessage(final MinecraftClient client) {
        final PlayerState state = PlayerState.of(client);

        return StateMessage.builder()
                .tick(client.world.getTime())
                .playerState(state)
                .activeActions(activeActions)
                .build();
    }
}
