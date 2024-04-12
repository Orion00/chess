package server.websocket;

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
        if (innerMap == null) {
            //gameId doesn't exist yet
            innerMap = new ConcurrentHashMap<>();
        }
        innerMap.put(authToken, new Connection(authToken, session));
        connections.put(gameId, innerMap);
    }

    public void remove(Integer gameId, String authToken) {
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

    public void send(Integer gameId,String authToken, ServerMessage notification) throws IOException {
        var removeList = new ArrayList<Connection>();
        ConcurrentHashMap<String, Connection> innerMap = getInnerMap(gameId);
        for (var c : innerMap.values()) {
            if (c.session.isOpen()) {
                if (c.authToken.equals(authToken)) {
                    c.send(notification.toString());
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var d : removeList) {
            connections.remove(d.authToken);
        }
    }

    private ConcurrentHashMap<String, Connection> getInnerMap(Integer gameId) {
        return connections.get(gameId);
    }
}