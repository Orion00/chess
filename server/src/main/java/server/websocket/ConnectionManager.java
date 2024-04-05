package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    // HashMap<GameId, Hashmap<authToken, Connection>
    public final ConcurrentHashMap<String, ConcurrentHashMap<String,Connection>> connections = new ConcurrentHashMap<>();

    public void add(String gameId, String authToken, Session session) {
        // Retrieve or create the inner map for the given gameId
        ConcurrentHashMap<String, Connection> innerMap = connections.computeIfAbsent(gameId, i -> new ConcurrentHashMap<>());
        // Put the connection into the inner map
        innerMap.put(authToken, new Connection(authToken, session));
    }

    public void remove(String gameId) {
        connections.remove(visitorName);
    }

    public void broadcast(String excludeAuthToken, ServerMessage notification) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.authToken.equals(excludeAuthToken)) {
                    c.send(notification.toString());
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.authToken);
        }
    }
}