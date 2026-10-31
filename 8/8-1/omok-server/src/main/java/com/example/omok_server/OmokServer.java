package com.example.omok_server;

import com.example.omok_server.game.GameRoom;
import com.example.omok_server.game.Lobby;
import com.example.omok_server.protocol.Packet;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.net.ServerSocket;

public class OmokServer {
    private static final Lobby lobby = new Lobby();
    private ServerSocket serverSocket;

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
            serverSocket = new ServerSocket(port);

            while(true) {
                Socket socket = serverSocket.accept();
                handleAccept(socket);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void handleAccept(Socket client) throws  IOException {
        Session session = new Session(client);

        new Thread(() -> {
            try {
                handleRead(session);
            } catch (IOException e) {

            }

        }).start();
    }

    private static void handleRead(Session session) throws IOException{
        Socket client = session.getChannel();
        InputStream input = session.getInputStream();

        while(true) {
            byte[] buf = new byte[1024];
            int len = input.read(buf);

            if(len == -1) {
                session.close();
                return;
            }

            byte[] data = Arrays.copyOf(buf, len);

            List<Packet> packets = session.getDecoder().decoder(data);
            for (Packet packet : packets) {
                switch (packet.getPacketType()) {
                    case REQUEST_ROOM_LIST -> lobby.getWaitingRoomList(session);
                    case CREATE_ROOM -> lobby.createRoom(session);
                    case JOIN_ROOM -> {
                        ByteBuffer payload = ByteBuffer.wrap(packet.getPayload());
                        int id = payload.get();

                        lobby.joinRoom(session, id);
                    }
                    case REMATCH_OFFER -> {
                        GameRoom room = session.getRoom();
                        if (room == null) continue;
                        room.offerRematch(session);
                    }
                    case PLACE_STONE -> {
                        GameRoom room = session.getRoom();
                        if (room == null) continue;

                        ByteBuffer payload = ByteBuffer.wrap(packet.getPayload());

                        int x = payload.get();
                        int y = payload.get();
                        room.placeStone(session, x, y);
                    }
                    case LEAVE_ROOM -> {
                        GameRoom room = session.getRoom();
                        if (room == null) continue;

                        room.leave(session);
                    }
                }
            }
        }
    }

    public void close() {
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            System.out.println("serverSocket close 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
