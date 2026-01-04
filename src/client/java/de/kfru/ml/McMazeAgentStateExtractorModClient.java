package de.kfru.ml;

import de.kfru.ml.action.PlayerActions;
import de.kfru.ml.state.PlayerState;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class McMazeAgentStateExtractorModClient implements ClientModInitializer {

    private static final Logger logger = LoggerFactory.getLogger("mc-maze-agent-state-extractor-mod");

    private static long tickCounter = 0;

    private static final PlayerActions actions = new PlayerActions();

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;

            tickCounter++;
            if (tickCounter % 100 != 0) {
                actions.perform(List.of(), client);
                return; // Log every 5 seconds for now
            }

            final PlayerState state = PlayerState.of(client);
            logger.info("Player State: {}", state);

            actions.perform(List.of("MOVE", "JUMP", "CAMERA"), client);
            logger.info("Registered new actions.");
        });
    }
}