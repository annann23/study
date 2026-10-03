package com.example.omok_server.protocol;

import java.nio.ByteBuffer;

public class PacketEncoder {
    public static byte[] encode(Packet packet) {
        byte[] payload = packet.getPayload();
        int totalSize = 4 + payload.length;
        ByteBuffer buffer = ByteBuffer.allocate(totalSize);

        buffer.putShort((short) payload.length)
                .putShort((short) packet.getPacketType().getValue())
                .put(payload);

        return buffer.array();
    }
}
