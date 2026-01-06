package de.kfru.ml;

import de.kfru.ml.action.PlayerActions;
import de.kfru.ml.action.PlayerReset;
import de.kfru.ml.state.PlayerState;
import de.kfru.ml.ws.AgentWebsocketServer;
import de.kfru.ml.ws.messages.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class McMazeAgentStateExtractorModClient implements ClientModInitializer {

    private static final Logger logger = LoggerFactory.getLogger("mc-maze-agent-state-extractor-mod-client");
    private static final PlayerActions actions = new PlayerActions();

    private AgentWebsocketServer ws;

    private Long latestActionsStartedTick;
    private IncomingMessage latestAction;

    private final List<Consumer<MinecraftClient>> nextTickCallbacks = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        ws = new AgentWebsocketServer("127.0.0.1", 8081);
        ws.start();

        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
        ServerTickEvents.END_SERVER_TICK.register(this::killPlayersIfBelow0);

        disablePauseMenuWhenInBackground();

        logger.info("McMazeAgentStateExtractorModClient initialized successfully.");
    }

    private void killPlayersIfBelow0(final MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (player.isAlive() && !player.getGameMode().isCreative() && player.getY() < 0) {
                // Kill player properly
                player.damage(server.getOverworld(), player.getDamageSources().outOfWorld(), Float.MAX_VALUE);
            }
        }
    }

    private void disablePauseMenuWhenInBackground() {
        MinecraftClient.getInstance().execute(() -> {
            MinecraftClient.getInstance().options.pauseOnLostFocus = false;
            logger.info("Disabled pause on lost focus.");
        });
    }

    private void onTick(final MinecraftClient client) {
        if (client.player == null || client.world == null) return;

        this.runNextTickCallbacks(client);

        if (client.player.isDead()) {
            logger.info("Player is dead, respawning...");
            client.player.requestRespawn();
            actions.clear();
            onNextTick(this::respondToDeath);
            return;
        }

        final IncomingMessage message = ws.consumeLatestAction();
        if (message == null) {
            if (latestAction instanceof ActionMessage action) {
                boolean allCompleted = actions.perform(client);
                if (allCompleted && latestAction != null) {
                    onActionCompleted(client, action);
                    latestAction = null;
                }
            } else if (latestAction instanceof ResetMessage reset) {
                // resets take exactly one tick
                onResetCompleted(client, reset);
                latestAction = null;
            }
        }

        if (message instanceof ResetMessage) {
            latestAction = message;
            latestActionsStartedTick = client.world.getTime();
            onReset(client);
        }

        if (message instanceof ActionMessage actionMessage) {
            latestAction = actionMessage;
            latestActionsStartedTick = client.world.getTime();
            actions.perform(actionMessage.toPlayerActions(), client);
        }
    }

    public void respondToDeath(final MinecraftClient client) {
        actions.clear();
        final var stateMessage = buildStateMessage(client, latestAction, MessageType.STATE_AFTER_ACTION, true);
        ws.broadcast(stateMessage.toJson());
        latestAction = null;
        latestActionsStartedTick = null;
    }

    private void onNextTick(final Consumer<MinecraftClient> callback) {
        this.nextTickCallbacks.add(callback);
    }

    private void runNextTickCallbacks(final MinecraftClient client) {
        for (Consumer<MinecraftClient> callback : nextTickCallbacks) {
            try {
                callback.accept(client);
            } catch (Exception e) {
                logger.warn("Failed to run next tick callback. {}", e.getMessage());
            }
        }
        nextTickCallbacks.clear();
    }

    private void onActionCompleted(final MinecraftClient client, final ActionMessage action) {
        final StateMessage stateMessage = buildStateMessage(client, action, MessageType.STATE_AFTER_ACTION, false);
        ws.broadcast(stateMessage.toJson());
    }

    @SuppressWarnings("DataFlowIssue") // client.player and client.world have already been checked to be not null
    private StateMessage buildStateMessage(final MinecraftClient client, final IncomingMessage message, final MessageType type, final boolean died) {
        final PlayerState state = PlayerState.of(client);

        final long tick = client.world.getTime();

        if (latestActionsStartedTick == null) {
            throw new IllegalStateException("Latest Action was cleared but something lead to building a StateMessage...");
        }

        return StateMessage.builder()
                .type(type)
                .episode(message.getEpisode())
                .step(message.getStep())
                .tickStart(latestActionsStartedTick)
                .tickEnd(tick)
                .playerState(state)
                .died(died)
                .build();
    }

    private void onReset(final MinecraftClient client) {
        actions.clear();
        PlayerReset.perform(client);
        logger.info("Reset executed.");
    }

    private void onResetCompleted(final MinecraftClient client, final ResetMessage reset) {
        final StateMessage stateMessage = buildStateMessage(client, reset, MessageType.STATE_AFTER_RESET, false);
        ws.broadcast(stateMessage.toJson());
        logger.info("Reset completed and state sent to agent.");
    }
}
