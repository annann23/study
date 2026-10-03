package com.example.omok_server;

import com.example.omok_server.protocol.PacketDecoder;

import java.nio.channels.SocketChannel;

public class Session {
    private final SocketChannel channel;
    private final PacketDecoder decoder = new PacketDecoder();
    private int stone;

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
}
