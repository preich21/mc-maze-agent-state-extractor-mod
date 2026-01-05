package de.kfru.ml;

import de.kfru.ml.action.PlayerActions;
import de.kfru.ml.action.PlayerReset;
import de.kfru.ml.state.PlayerState;
import de.kfru.ml.ws.AgentWebsocketServer;
import de.kfru.ml.ws.messages.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class McMazeAgentStateExtractorModClient implements ClientModInitializer {

    private static final Logger logger = LoggerFactory.getLogger("mc-maze-agent-state-extractor-mod");
    private static final PlayerActions actions = new PlayerActions();

    private AgentWebsocketServer ws;

    private long latestActionsStartedTick = 0;
    private IncomingMessage latestAction = null;

    @Override
    public void onInitializeClient() {
        ws = new AgentWebsocketServer("127.0.0.1", 8081);
        ws.start();

        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);

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

    private void onActionCompleted(final MinecraftClient client, final ActionMessage action) {
        final StateMessage stateMessage = buildStateMessage(client, action, MessageType.STATE_AFTER_ACTION);
        ws.broadcast(stateMessage.toJson());
    }

    @SuppressWarnings("DataFlowIssue") // client.player and client.world have already been checked to be not null
    private StateMessage buildStateMessage(final MinecraftClient client, final IncomingMessage message, final MessageType type) {
        final PlayerState state = PlayerState.of(client);

        final long tick = client.world.getTime();

        return StateMessage.builder()
                .type(type)
                .episode(message.getEpisode())
                .step(message.getStep())
                .tickStart(latestActionsStartedTick)
                .tickEnd(tick)
                .playerState(state)
                .build();
    }

    private void onReset(final MinecraftClient client) {
        actions.clear();
        PlayerReset.perform(client);
        logger.info("Reset executed.");
    }

    private void onResetCompleted(final MinecraftClient client, final ResetMessage reset) {
        final StateMessage stateMessage = buildStateMessage(client, reset, MessageType.STATE_AFTER_RESET);
        ws.broadcast(stateMessage.toJson());
        logger.info("Reset completed and state sent to agent.");
    }
}