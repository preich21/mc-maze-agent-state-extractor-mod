package de.kfru.ml.action;

import net.minecraft.client.MinecraftClient;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerActions {

    private final Map<String, PlayerAction> actions = new ConcurrentHashMap<>();

    public void perform(final List<String> newActions,  final MinecraftClient client) {
        for (String newAction : newActions) {
            addNewAction(newAction);
        }

        for (Entry<String, PlayerAction> action : actions.entrySet()) {
            performAction(action, client);
        }
    }

    private void addNewAction(final String action) {
        final PlayerAction previousAction = actions.put(action, PlayerAction.of(action));
        if (previousAction != null) {
            previousAction.cancel();
        }
    }

    private void performAction(final Entry<String, PlayerAction> action, final MinecraftClient client) {
        if (action.getValue() == null) {
            return;
        }

        final boolean finished = action.getValue().perform(client);

        if (finished) {
            actions.remove(action.getKey());
        }
    }
}
