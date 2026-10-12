package com.example.omok_server;

import com.example.omok_server.game.GameRoom;
import com.example.omok_server.protocol.Packet;
import com.example.omok_server.protocol.PacketEncoder;
import com.example.omok_server.protocol.PacketType;
import com.example.omok_server.game.OmokBoard;

import java.io.*;
import java.net.InetSocketAddress;

import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.List;
import java.util.Set;

public class OmokServer {
    private static Session waitingSession;
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

        if (waitingSession == null) {
            waitingSession = session;
            return;
        }

        GameRoom room = new GameRoom(waitingSession, session);
        waitingSession = null;
        room.start();
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
            if (packet.getPacketType() == PacketType.PLACE_STONE) {
                GameRoom room = session.getRoom();
                if(room == null) continue;

                ByteBuffer payload = ByteBuffer.wrap(packet.getPayload());

                int x = payload.get();
                int y = payload.get();
                room.placeStone(session, x, y);
            }
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
        }
    }
}
