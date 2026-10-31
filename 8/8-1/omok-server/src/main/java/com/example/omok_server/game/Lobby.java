package com.example.omok_server.game;

import com.example.omok_server.Session;
import com.example.omok_server.protocol.Packet;
import com.example.omok_server.protocol.PacketType;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lobby {
    private final Map<Integer, GameRoom> rooms = new HashMap<>();
    private int roomId = 1;

    public  void getWaitingRoomList(Session session)  throws IOException {
        List<GameRoom> waiting = rooms.values().stream().filter(r -> !r.isFull()).toList();

        byte[] payload = new byte[waiting.size() + 1];
        payload[0]= (byte) waiting.size();
        for(int i=0; i<waiting.size(); i++ ){
            payload[i+1] = (byte) waiting.get(i).getId();
        }

        send(session, new Packet(PacketType.ROOM_LIST, payload));
    }

    public void createRoom(Session session) throws IOException {
        if(session.getRoom() != null) return;

        GameRoom room = new GameRoom(session, roomId++);
        rooms.put(room.getId(), room);
        send(session, new Packet(PacketType.ROOM_CREATED_RESULT, new byte[]{(byte) room.getId()}));
    }

    public void joinRoom(Session session, int roomId) throws IOException {
        if(session.getRoom() != null) return;

        GameRoom room = rooms.get(roomId);
        if (room == null || room.isFull()) {
            send(session, new Packet(PacketType.JOIN_ROOM_RESULT, new byte[]{0}));
            return;
        }
        send(session, new Packet(PacketType.JOIN_ROOM_RESULT, new byte[]{1}));
        room.join(session);   // 백 지정 + start()
    }

    private void send(Session session, Packet packet) throws IOException {
        session.send(packet);
    }

    public void removeRoom(GameRoom room) {
        rooms.remove(room.getId());
    }
}
