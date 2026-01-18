package de.kfru.ml;

import de.kfru.ml.action.PlayerActions;
import de.kfru.ml.action.PlayerReset;
import de.kfru.ml.state.PlayerState;
import de.kfru.ml.ws.AgentWebsocketServer;
import de.kfru.ml.ws.messages.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.minecraft.client.MinecraftClient;
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
        ws = new AgentWebsocketServer("127.0.0.1", 8081);
        ws.start();

        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
        ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register(ws::onWorldChange);
//        ClientLifecycleEvents.CLIENT_STARTED.register() // TODO enter world base on env var

        disablePauseMenuWhenInBackground();

        logger.info("McMazeAgentStateExtractorModClient initialized successfully.");
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

        StateMessage stateMessage = buildStateMessage(client);
        ws.broadcast(stateMessage.toJson());

        checkForNewMessage(client);

        activeActions.perform(client);
    }

    private void checkForNewMessage(final MinecraftClient client) {
        final IncomingMessage message = ws.consumeLatestAction();
      switch (message) {
        case null -> {} // No new action request - keep doing active action
        case ResetMessage resetMessage -> {
          activeActions.clear(client);
          PlayerReset.perform(client, resetMessage.getStartPoint());
          client.player.sendMessage(Text.of("Starting episode " + message.getEpisode() + ". Resetting player to start point at " + resetMessage.getStartPoint()), false);
          logger.info("Reset executed.");
        }
        case ActionMessage actionMessage -> {
            activeActions.updateActions(actionMessage, client);
        }
        default -> throw new IllegalArgumentException("Unknown message type: " + message.getClass().getName());
      }
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
