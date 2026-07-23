package com.example.echo_server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class EchoServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;

    public static void main(String[] args) {
        EchoServer server = new EchoServer();
        try {
            server.init();
            server.echo();
        } finally {
            server.close();  // 예외 처리 신경 안 써도 됨 (내부에서 이미 처리)
        }
    }

    public void init() {
        int port = 9234;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("에코 서버: " + port );

            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void echo() {
        try {
            InputStream in = clientSocket.getInputStream();
            OutputStream out = clientSocket.getOutputStream();
            byte[] buffer = new byte[1024];

            while(true) {
                int len = in.read(buffer);
                if(len <= 0) break;
                System.out.println("수신된 메시지: " + new String(buffer, 0, len));
                out.write(buffer, 0, len);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            if(clientSocket != null) clientSocket.close();
            if(serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

