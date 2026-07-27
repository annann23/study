package com.example.echo_server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {
    private ServerSocket serverSocket;
    private final ConnectionManager manager = new ConnectionManager();

    public static void main(String[] args) {
        EchoServer server = new EchoServer();
        try {
            server.init();
        } finally {
            server.close();
        }
    }

    public void init() {
        int port = 9234;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("에코 서버: " + port );

            while(true) {
                Socket clientSocket = serverSocket.accept();
                manager.connect(clientSocket);

                System.out.println("클라이언트 연결: " + clientSocket.getRemoteSocketAddress());
                Thread worker = new Thread(() -> echo(clientSocket));
                worker.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void echo(Socket socket) {
        try {
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            byte[] buffer = new byte[1024];

            while(true) {
                int len = in.read(buffer);
                if(len <= 0) break;
                System.out.println("수신된 메시지: " + new String(buffer, 0, len));
                out.write(buffer, 0, len);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            manager.disconnect(socket);
            try { socket.close(); } catch(IOException ignored) {}
        }
    }

    public void close() {
        try {
            manager.closeAll();
            if(serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

