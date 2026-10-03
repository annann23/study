package com.example.omok_server;

import com.example.omok_server.protocol.Packet;
import com.example.omok_server.protocol.PacketDecoder;
import com.example.omok_server.protocol.PacketEncoder;
import com.example.omok_server.protocol.PacketType;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.List;

public class OmokClient {
    private static final int BOARD_SIZE = 15;

    private Socket clientSocket;
    private OutputStream out;
    private InputStream in;
    private final PacketDecoder decoder = new PacketDecoder();
    private final BoardCell[][] cells = new BoardCell[BOARD_SIZE][BOARD_SIZE];
    private final Icon blackStone = new StoneIcon(32, true);
    private final Icon whiteStone = new StoneIcon(32, false);
    private final JLabel statusLabel  = new JLabel("상대를 기다리는 중...", SwingConstants.CENTER);
    private int myStone;

    public static void main(String[] args) {
        OmokClient client = new OmokClient();
        client.init();
        client.startReaderThread();
        SwingUtilities.invokeLater(client::createAndShowBoard);
    }

    public void init() {
        int port = 9234;

        try {
            clientSocket = new Socket("localhost", port);
            out = clientSocket.getOutputStream();
            in = clientSocket.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createAndShowBoard() {
        JFrame frame = new JFrame("오목 클라이언트");
        JPanel board = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));

        for (int y = 0; y < BOARD_SIZE; y++) {
            for (int x = 0; x < BOARD_SIZE; x++) {
                int fx = x;
                int fy = y;
                BoardCell cell = new BoardCell(x, y, BOARD_SIZE);
                cell.addActionListener(e -> sendPlaceStone(fx, fy));
                cells[x][y] = cell;
                board.add(cell);
            }
        }
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD, 16f));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        frame.add(statusLabel, BorderLayout.NORTH);
        frame.add(board, BorderLayout.CENTER);
        frame.setSize(600, 660);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void sendPlaceStone(int x, int y) {
        try {
            byte[] payload = ByteBuffer.allocate(2)
                    .put((byte) x)
                    .put((byte) y)
                    .array();
            Packet packet = new Packet(PacketType.PLACE_STONE, payload);
            out.write(PacketEncoder.encode(packet));
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void startReaderThread() {
        Thread readerThread = new Thread(this::readLoop);
        readerThread.setDaemon(true);
        readerThread.start();
    }

    private void readLoop() {
        byte[] buffer = new byte[1024];

        try {
            while (true) {
                int len = in.read(buffer);

                if (len == -1) {
                    System.out.println("서버 연결종료");
                    break;
                }

                byte[] data = new byte[len];
                System.arraycopy(buffer, 0, data, 0, len);

                List<Packet> packets = decoder.decoder(data);
                for (Packet packet : packets) {
                    handleServerPacket(packet);
                }
            }
        } catch (IOException e) {
            System.out.println("읽기 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private void handleServerPacket(Packet packet) {
        ByteBuffer payload = ByteBuffer.wrap(packet.getPayload());

        switch (packet.getPacketType()) {
            case GAME_STARTED -> {
                myStone = payload.get();
                SwingUtilities.invokeLater(() -> updateStatus(1));
            }
            case STONE_PLACED -> {
                int x = payload.get();
                int y = payload.get();
                int stone = payload.get();
                SwingUtilities.invokeLater(() -> {
                        cells[x][y].placeStone(stone == 1 ? blackStone : whiteStone);
                        updateStatus((stone == 1) ? 2 : 1);
                    }
                );
            }
            case GAME_OVER -> {
                int winner = payload.get();
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(null, winner == 1 ? "흑 승리!" : "백 승리!"));
            }
            default -> {
                return;
            }
        }
    }

    private void updateStatus(int turn) {
        statusLabel.setText("나: " +  ((myStone == 1) ? "흑" : "백") + " ==== " + ((turn == myStone) ? "내 차례" : "상대 차례"));
    }

    public void close() {
        try {
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
