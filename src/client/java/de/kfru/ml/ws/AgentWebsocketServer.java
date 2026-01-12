package de.kfru.ml.ws;

import de.kfru.ml.util.StartPointsData;
import de.kfru.ml.ws.messages.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AgentWebsocketServer extends WebSocketServer {

    private static final Logger logger = LoggerFactory.getLogger("mc-maze-agent-state-extractor-mod");

    private final AtomicReference<IncomingMessage> latestAction = new AtomicReference<>();

    private List<StartPointsData.StartPoint> startPoints = new ArrayList<>();

    public AgentWebsocketServer(final String host, final int port) {
        super(new java.net.InetSocketAddress(host, port));
        setReuseAddr(true);
        setTcpNoDelay(true);
    }

    public void onWorldChange(MinecraftClient client, ClientWorld world) {
        logger.info("World changed to, pulling saved start points.");
        StartPointsData savedBlockData = StartPointsData.getSavedBlockData(client.getServer());
        if (savedBlockData != null) {
            this.startPoints = savedBlockData.getStartPoints();
        }
    }

    public IncomingMessage consumeLatestAction() {
        return latestAction.getAndSet(null);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        logger.info("WebSocket connection opened from [{}]", conn.getRemoteSocketAddress());
        String message = HelloMessage.builder().startPoints(startPoints).build().toJson();
        conn.send(message);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        logger.info("WebSocket connection closed from [{}]: code={}, reason={}, remote={}",
                conn.getRemoteSocketAddress(), code, reason, remote);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        final MessageType type = MessageType.of(message);

        switch (type) {
            case ACTION_REQUEST -> {
                final ActionMessage actionMessage = ActionMessage.fromJson(message);
                latestAction.set(actionMessage);
            }
            case RESET_REQUEST -> {
                final ResetMessage resetMessage = ResetMessage.fromJson(message);
                latestAction.set(resetMessage);
            }
            default -> logger.warn("Received unknown message type: {}", type);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        logger.warn("WebSocket error with connection from [{}]", conn.getRemoteSocketAddress(), ex);
    }

    @Override
    public void onStart() {
        logger.info("WebSocket server started on port: {}", getPort());
    }
}
