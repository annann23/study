package com.example.omok_server.game;

import com.example.omok_server.Session;
import com.example.omok_server.protocol.Packet;
import com.example.omok_server.protocol.PacketEncoder;
import com.example.omok_server.protocol.PacketType;

import java.io.IOException;
import java.nio.ByteBuffer;

public class GameRoom {
    private OmokBoard board = new OmokBoard();
    private static Session black;
    private static Session white;

    public GameRoom(Session black, Session white) {
        this.black = black;
        this.white = white;
    }

    public void start() throws IOException {
        black.setStone(1);
        white.setStone(2);
        black.setRoom(this);
        white.setRoom(this);

        send(black, new Packet(PacketType.GAME_STARTED, new byte[]{1}));
        send(white, new Packet(PacketType.GAME_STARTED, new byte[]{2}));
    }

    private static void send(Session session, Packet packet) throws IOException {
        byte[] bytes = PacketEncoder.encode(packet);
        session.getChannel().write(ByteBuffer.wrap(bytes));
    }

    public void placeStone(Session session, int x, int y) throws  IOException {
        int stone = session.getStone();
        if (!board.canPlace(x, y, stone)) return;
        boolean win = board.placeStone(x, y, stone);

        broadcast(new Packet(PacketType.STONE_PLACED, new byte[]{(byte) x, (byte) y, (byte) stone}));
        if (win) broadcast(new Packet(PacketType.GAME_OVER, new byte[]{(byte) stone}));
    }

    private static void broadcast(Packet packet) throws IOException {
        byte[] bytes = PacketEncoder.encode(packet);
        black.getChannel().write(ByteBuffer.wrap(bytes));
        white.getChannel().write(ByteBuffer.wrap(bytes));
    }
}
