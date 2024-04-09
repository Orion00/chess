package server.websocket;

import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    // HashMap<gameId, Hashmap<authToken, Connection>
    public final ConcurrentHashMap<Integer, ConcurrentHashMap<String,Connection>> connections = new ConcurrentHashMap<>();

    public void add(Integer gameId, String authToken, Session session) throws IOException {
        ConcurrentHashMap<String, Connection> innerMap = getInnerMap(gameId);
        innerMap.put(authToken, new Connection(authToken, session));
    }

    public void remove(Integer gameId, String authToken) throws IOException {
        ConcurrentHashMap<String, Connection> innerMap = getInnerMap(gameId);
        innerMap.remove(authToken);

    }

    public void broadcast(Integer gameId, String excludeAuthToken, ServerMessage notification) throws IOException {
        var removeList = new ArrayList<Connection>();
        ConcurrentHashMap<String, Connection> innerMap = getInnerMap(gameId);
        for (var c : innerMap.values()) {
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

    private ConcurrentHashMap<String, Connection> getInnerMap(Integer gameId) throws IOException {
        ConcurrentHashMap<String, Connection> innerMap = connections.get(gameId);
        if (innerMap == null) {
            throw new IOException("gameID doesn't exist");
        }
        return innerMap;
    }
}