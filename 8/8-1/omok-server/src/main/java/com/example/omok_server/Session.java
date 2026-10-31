package com.example.omok_server;

import com.example.omok_server.game.GameRoom;
import com.example.omok_server.protocol.Packet;
import com.example.omok_server.protocol.PacketDecoder;
import com.example.omok_server.protocol.PacketEncoder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Session {
    private final Socket channel;
    private final PacketDecoder decoder = new PacketDecoder();
    private int stone;
    private GameRoom room;

    private final InputStream inputStream;
    private final OutputStream outputStream;

    public Session(Socket channel) throws IOException {
        this.channel = channel;
        this.inputStream = channel.getInputStream();
        this.outputStream = channel.getOutputStream();
    }

    public Socket getChannel() {
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

    public void send(Packet packet) throws IOException {
        outputStream.write(PacketEncoder.encode(packet));
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void close() throws IOException {
        inputStream.close();
        outputStream.close();
    }
}
