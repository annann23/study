package com.example.omok_server.protocol;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class PacketDecoder {
    private final ByteBuffer buffer = ByteBuffer.allocate(1024); //쓰기모드

    public List<Packet> decoder(byte[] data) {
        buffer.put(data).flip(); //flip하면 읽기모드로 바뀜(말그대로 뒤집는단의미, limit을 현재 위치로 바꾸고 현재 위치는 맨처음이 됨)

        List<Packet> result = new ArrayList<Packet>();

        while(true) {
            buffer.mark();

            if(buffer.remaining() < 4) break;

            int length = buffer.getShort();
            int typeCode = buffer.getShort();

            if(buffer.remaining() < length) {
                buffer.reset();
                break;
            }

            byte[] payload = new byte[length];
            buffer.get(payload);

            result.add(new Packet(PacketType.getType(typeCode), payload));
        }

        buffer.compact();

        return result;
    }
}
