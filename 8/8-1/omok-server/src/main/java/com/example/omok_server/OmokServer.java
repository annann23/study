package com.example.omok_server;

import com.example.omok_server.protocol.Packet;
import com.example.omok_server.protocol.PacketEncoder;
import com.example.omok_server.protocol.PacketType;
import com.example.omok_server.service.GameEngine;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.List;
import java.util.Set;

public class OmokServer {
    private static final GameEngine engine = new GameEngine();
    private static Session black;
    private static Session white;

    private ServerSocketChannel serverSocket;
    private Selector selector;
    public static void main(String[] args) {
        OmokServer server = new OmokServer();
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
                        else if (key.isReadable()) { handleRead(key); }
                    } catch (Exception e) {
                        System.out.println("처리 중 오류가 발생했습니다: " + e.getMessage());

                        if (key.channel() instanceof SocketChannel) {
                            closeChannel(key);
                        }
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

        Session session = new Session(client);
        client.register(selector, SelectionKey.OP_READ,session);

        if (black == null) {
            black = session;
            session.setStone(1);
        } else if (white == null) {
            white = session;
            session.setStone(2);

            send(black, new Packet(PacketType.GAME_STARTED, new byte[]{1}));
            send(white, new Packet(PacketType.GAME_STARTED, new byte[]{2}));
        }
    }

    private static void handleRead(SelectionKey key) throws IOException{
        Session session = (Session) key.attachment();
        SocketChannel client = session.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int len = client.read(buffer);

        if (len == -1) {
            closeChannel(key);
            return;
        }

        buffer.flip();
        byte[] data = new byte[len];
        buffer.get(data);

        List<Packet> packets = session.getDecoder().decoder(data);
        for (Packet packet : packets) {
            if (packet.getPacketType() != PacketType.PLACE_STONE || white == null) continue;

            System.out.println("패킷 데이터: " + packet.getPacketType());

            ByteBuffer payload = ByteBuffer.wrap(packet.getPayload());
            int x = payload.get();
            int y = payload.get();
            int stone = session.getStone();

            if (!engine.canPlace(x, y, stone)) continue;
            boolean win = engine.placeStone(x, y, stone);

            broadcast(new Packet(PacketType.STONE_PLACED, new byte[]{(byte) x, (byte) y, (byte) stone}));
            if (win) broadcast(new Packet(PacketType.GAME_OVER, new byte[]{(byte) stone}));
        }
    }

    private static void broadcast(Packet packet) throws IOException {
        byte[] bytes = PacketEncoder.encode(packet);
        black.getChannel().write(ByteBuffer.wrap(bytes));
        white.getChannel().write(ByteBuffer.wrap(bytes));
    }

    private static void send(Session session, Packet packet) throws IOException {
        byte[] bytes = PacketEncoder.encode(packet);
        session.getChannel().write(ByteBuffer.wrap(bytes));
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
        }
    }
}
