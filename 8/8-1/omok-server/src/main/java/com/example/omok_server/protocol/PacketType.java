package com.example.omok_server.protocol;

import java.util.HashMap;
import java.util.Map;

public enum PacketType {
    REQUEST_ROOM_LIST(1),
    CREATE_ROOM(2),
    JOIN_ROOM(3),
    PLACE_STONE(4),
    RETIRE(5),
    REMATCH_OFFER(6),
    LEAVE_ROOM(7),
    ROOM_LIST(101),
    ROOM_CREATED_RESULT(102),
    JOIN_ROOM_RESULT(103),
    OPPONENT_JOINED(104),
    GAME_STARTED(105),
    STONE_PLACE_RESULT(106),
    STONE_PLACED(107),
    TIME_OVER(108),
    TURN_CHANGE(109),
    GAME_OVER(110),
    OPPONENT_LEAVED(111),
    OPPONENT_REMATCH_OFFER(112);

    private final int value;
    private static final Map<Integer, PacketType> codemap = new HashMap<>();

    static {
        for (PacketType type : values()) {
            codemap.put(type.getValue(), type);
        }
    }

    PacketType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PacketType getType(int code){
        PacketType type = codemap.get(code);
        if(type == null){
            throw new IllegalArgumentException("알 수 없는 코드입니다" + code);
        }
        return type;
    }
}
