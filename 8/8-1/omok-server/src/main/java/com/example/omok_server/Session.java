package com.example.omok_server;

import com.example.omok_server.protocol.PacketDecoder;

import java.nio.channels.SocketChannel;

public class Session {
    private final SocketChannel channel;
    private final PacketDecoder decoder = new PacketDecoder();

    public Session(SocketChannel channel) {
        this.channel = channel;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public PacketDecoder getDecoder() {
        return decoder;
    }
}
