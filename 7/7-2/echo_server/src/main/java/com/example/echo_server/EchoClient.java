package com.example.echo_server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;

public class EchoClient {
    private Socket clientSocket;

    public static void main(String[] args) {
       EchoClient client = new EchoClient();

        try {
            client.init();
            client.echo();
        } finally {
            client.close();
        }
    }

    public void init() {
        int port = 9234;

        try {
            clientSocket = new Socket("localhost", port);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void echo() {
        try {
            InputStream in = clientSocket.getInputStream();
            OutputStream out = clientSocket.getOutputStream();
            byte[] buffer = new byte[1024];

            Scanner sc = new Scanner(System.in);
            while(true) {
                String message = sc.nextLine();

                if(Objects.equals(message, "quit")) break;
                out.write(message.getBytes(StandardCharsets.UTF_8));

                int len = in.read(buffer);
                System.out.println("응답 메시지: " + new String(buffer, 0, len));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            if (clientSocket != null) {
                clientSocket.close();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
