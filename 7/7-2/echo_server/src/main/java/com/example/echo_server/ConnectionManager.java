package com.example.echo_server;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConnectionManager {
    private final List<Socket> connections = new CopyOnWriteArrayList<>();
    public void connect(Socket socket) {
        connections.add(socket);
    }

    public void disconnect(Socket socket) {
        connections.remove(socket);
    }
    public void closeAll() {
        for (int i = connections.size() - 1; i >= 0; i--) {
            Socket socket = connections.remove(i);
            try{ socket.close(); } catch(IOException ignore){};
        }
    }
}
