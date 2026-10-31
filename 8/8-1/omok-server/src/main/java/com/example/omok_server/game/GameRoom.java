package com.example.omok_server.game;

import com.example.omok_server.Session;
import com.example.omok_server.protocol.Packet;
import com.example.omok_server.protocol.PacketType;

import java.io.IOException;

public class GameRoom {
    private OmokBoard board = new OmokBoard();
    private Session black;
    private Session white;
    private int id;
    private Session rematchOffered;

    public GameRoom(Session player, int roomId) {
        this.black = player;
        this.id = roomId;
        player.setRoom(this);
    }

    public boolean isFull() {
        return white != null && black != null;
    }
    public boolean isEmpty() { return white == null && black == null; }

    public void join(Session player) throws IOException {
        if(isFull()) return;

        if(black != null) this.white = player;
        else this.black = player;
        player.setRoom(this);
        start();
    }

    public void start() throws IOException {
        black.setStone(1);
        white.setStone(2);

        black.send(new Packet(PacketType.GAME_STARTED, new byte[]{1}));
        white.send(new Packet(PacketType.GAME_STARTED, new byte[]{2}));
    }

    public void offerRematch(Session session) throws IOException {
        if (!board.isFinished()) return;

        if(rematchOffered == null) {
            rematchOffered = session;
        }else if(rematchOffered != session) {
            rematchOffered = null;
            board = new OmokBoard();
            start();
        }
    }

    public void placeStone(Session session, int x, int y) throws  IOException {
        int stone = session.getStone();
        if (!board.canPlace(x, y, stone)) return;
        boolean win = board.placeStone(x, y, stone);

        broadcast(new Packet(PacketType.STONE_PLACED, new byte[]{(byte) x, (byte) y, (byte) stone}));
        if (win) broadcast(new Packet(PacketType.GAME_OVER, new byte[]{(byte) stone}));
    }

    private void broadcast(Packet packet) throws IOException {
        black.send(packet);
        white.send(packet);
    }

    public void leave(Session session) throws IOException {
        session.setRoom(null);
        if (session == black) black = null;
        else white = null;

        board = new OmokBoard();
        rematchOffered = null;

        Session opponent = (black != null) ? black : white;
        if(opponent != null) {
            opponent.send(new Packet(PacketType.OPPONENT_LEAVED, new byte[0]));
        }
    }

    public int getId() {
        return this.id;
    }
}
