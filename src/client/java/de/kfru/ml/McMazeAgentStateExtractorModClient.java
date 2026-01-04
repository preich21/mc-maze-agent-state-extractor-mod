package de.kfru.ml;

import de.kfru.ml.action.PlayerActions;
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

        logger.info("McMazeAgentStateExtractorModClient initialized successfully.");
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
            }
        }

        if (message instanceof ResetMessage resetMessage) {
            onReset(client, resetMessage);
        }

        if (message instanceof ActionMessage actionMessage) {
            latestAction = actionMessage;
            latestActionsStartedTick = client.world.getTime();
            actions.perform(actionMessage.toPlayerActions(), client);
        }
    }

    @SuppressWarnings("DataFlowIssue") // client.player and client.world have already been checked to be not null
    private void onActionCompleted(final MinecraftClient client, final ActionMessage action) {
        final PlayerState state = PlayerState.of(client);

        final long tick = client.world.getTime();

        StateMessage stateMessage = StateMessage.builder()
                .type(MessageType.STATE_AFTER_ACTION)
                .episode(action.getEpisode())
                .step(action.getStep())
                .tickStart(latestActionsStartedTick)
                .tickEnd(tick)
                .playerState(state)
                .build();

        ws.broadcast(stateMessage.toJson());
    }

    private void onReset(final MinecraftClient client, final ResetMessage resetMessage) {
        // TODO: kill all current actions
        // TODO: port player back to the starting position & reset their camera angle
        // TODO: notify agent when finished & waited one tick for everything to settle
    }
}