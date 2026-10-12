package com.example.omok_server;

import com.example.omok_server.game.GameRoom;
import com.example.omok_server.protocol.PacketDecoder;

import java.nio.channels.SocketChannel;

public class Session {
    private final SocketChannel channel;
    private final PacketDecoder decoder = new PacketDecoder();
    private int stone;
    private GameRoom room;

    public Session(SocketChannel channel) {
        this.channel = channel;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public PacketDecoder getDecoder() {
        return decoder;
    }

    public void setStone(int stone) {
        this.stone = stone;
    }

    public int getStone() {
        return stone;
    }

    public void setRoom(GameRoom room) {
        this.room = room;
    }

    public GameRoom getRoom() {
        return room;
    }
}
