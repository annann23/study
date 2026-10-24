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
import java.util.ArrayList;
import java.util.List;

public class OmokClient {
    private static final int BOARD_SIZE = 15;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel root = new JPanel(cardLayout);
    private final DefaultListModel<Integer> roomListModel = new DefaultListModel<>();
    private final JList<Integer> roomList = new JList<>(roomListModel);

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
        SwingUtilities.invokeLater(client::createAndShowWindow);
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

    private void createAndShowWindow() {
        root.add(createLobbyPanel(), "lobby");
        root.add(createGamePanel(), "game");

        JFrame frame = new JFrame("오목 클라이언트");
        frame.add(root);
        frame.setSize(600, 660);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        showLobby();
    }

    private JPanel createLobbyPanel() {
        JButton refreshButton = new JButton("새로고침");
        JButton createButton = new JButton("방 만들기");
        JButton joinButton = new JButton("입장");

        refreshButton.addActionListener(e -> send(new Packet(PacketType.REQUEST_ROOM_LIST, new byte[0])));
        createButton.addActionListener(e -> send(new Packet(PacketType.CREATE_ROOM, new byte[0])));
        joinButton.addActionListener(e -> {
            Integer roomId = roomList.getSelectedValue();
            if (roomId == null) return;
            send(new Packet(PacketType.JOIN_ROOM, new byte[]{roomId.byteValue()}));
        });

        JPanel buttons = new JPanel();
        buttons.add(refreshButton);
        buttons.add(createButton);
        buttons.add(joinButton);

        JLabel title = new JLabel("대기 중인 방", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        JPanel lobby = new JPanel(new BorderLayout());
        lobby.add(title, BorderLayout.NORTH);
        lobby.add(new JScrollPane(roomList), BorderLayout.CENTER);
        lobby.add(buttons, BorderLayout.SOUTH);
        return lobby;
    }

    private JPanel createGamePanel() {
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

        JPanel game = new JPanel(new BorderLayout());
        game.add(statusLabel, BorderLayout.NORTH);
        game.add(board, BorderLayout.CENTER);
        return game;
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

    private void showLobby() {
        cardLayout.show(root, "lobby");
        send(new Packet(PacketType.REQUEST_ROOM_LIST, new byte[0]));
    }

    private void showGame() {
        reset();
        cardLayout.show(root, "game");
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
            case ROOM_LIST -> {
                int count = payload.get();
                List<Integer> ids = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    ids.add((int) payload.get());
                }

                SwingUtilities.invokeLater(() -> {
                    roomListModel.clear();
                    roomListModel.addAll(ids);
                });
            }
            case GAME_STARTED -> {
                myStone = payload.get();
                SwingUtilities.invokeLater(() -> {
                    showGame();
                    updateStatus(1);
                });
            }
            case ROOM_CREATED_RESULT -> {
                int roomId = payload.get();
                SwingUtilities.invokeLater(() -> {
                    showGame();
                    statusLabel.setText(roomId + "번 방 | 상대를 기다리는 중입니다.");
                });
            }
            case JOIN_ROOM_RESULT -> {
                boolean success = payload.get() == 1;
                SwingUtilities.invokeLater(() -> {
                    if (success) {
                        showGame();
                    } else {
                        JOptionPane.showMessageDialog(null, "입장할 수 없는 방입니다.");
                        showLobby();
                    }
                });
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
                SwingUtilities.invokeLater(() ->{
                    String msg = (winner == 1 ? "흑 승리!" : "백 승리!") + "\n재대국 하시겠습니까?";
                    int answer = JOptionPane.showConfirmDialog(null, msg, "게임 종료", JOptionPane.YES_NO_OPTION);

                    if (answer == JOptionPane.YES_OPTION) {
                        send(new Packet(PacketType.REMATCH_OFFER, new byte[0]));
                        statusLabel.setText("상대의 응답을 기다리는 중입니다.");
                    } else {
                        send(new Packet(PacketType.LEAVE_ROOM, new byte[0]));
                        reset();
                        showLobby();
                    }
                });
            }
            case OPPONENT_LEAVED -> SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "상대가 떠났습니다.");
                reset();
                statusLabel.setText("새 상대를 기다리는 중...");
            });
            default -> {
                return;
            }
        }
    }

    private void updateStatus(int turn) {
        statusLabel.setText("나: " +  ((myStone == 1) ? "흑" : "백") + " ==== " + ((turn == myStone) ? "내 차례" : "상대 차례"));
    }

    public void reset() {
        for(BoardCell[] row: cells) {
            for(BoardCell cell: row) {
                cell.removeStone();
            }
        }
    }

    private void send(Packet packet) {
        try {
            out.write(PacketEncoder.encode(packet));
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
