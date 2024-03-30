package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import webSocketMessages.serverMessages.NotificationMessages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String authtoken, Session session) {
        var connection = new Connection(authtoken, session);
        connections.put(authtoken, connection);
    }

    public void remove(String authtoken) {
        connections.remove(authtoken);
    }

    public void broadcast(String authtoken, NotificationMessages notification) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.authtoken.equals(authtoken)) {
                    c.send(notification.toString());
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.authtoken);
        }
    }

}
