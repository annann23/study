package com.example.echo_server;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Set;

public class EchoServer {
    private ServerSocketChannel serverSocket;
    private Selector selector;
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
            serverSocket = ServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress(port));
            serverSocket.configureBlocking(false);

            selector = Selector.open();
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);

            while(true) {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();

                keys.forEach(key -> {
                    try{
                        if (key.isAcceptable()) { handleAccept(key, selector); }
                        if (key.isReadable()) { handleRead(key); }
                    } catch (IOException e) {
                        System.out.println("처리 중 오류가 발생했습니다: " + e.getMessage());
                        closeChannel(key);
                    }

                });

                keys.clear();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void handleAccept(SelectionKey key, Selector selector) throws  IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel client = serverChannel.accept();
        client.configureBlocking(false);

        try{
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            client.register(selector, SelectionKey.OP_READ, buffer);
        } catch (ClosedChannelException e) {
            throw new RuntimeException(e);
        }

    }

    private static void handleRead(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();

        try {
            int len = client.read(buffer);

            if (len == -1) {
                closeChannel(key);
                return;
            }

            buffer.flip();
            client.write(buffer);
            buffer.clear();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            if (selector != null) selector.close();
        } catch (IOException e) {
            System.out.println("selector close 중 오류가 발생했습니다: " + e.getMessage());
        }

        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            System.out.println("serverSocket close 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private static void closeChannel(SelectionKey key) {
        key.cancel();

        try {
            key.channel().close();
        } catch (IOException e) {
            System.out.println("채널 close 중 오류가 발생했습니다: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

