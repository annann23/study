package com.example.omok_server.protocol;

public class Packet {
    private final PacketType packetType;
    private final byte[] payload;

    public Packet(PacketType packetType, byte[] payload) {
        this.packetType = packetType;
        this.payload = payload.clone();
    }

    public PacketType getPacketType() {
        return packetType;
    }

    public byte[] getPayload() {
        return payload.clone();
    }
}
