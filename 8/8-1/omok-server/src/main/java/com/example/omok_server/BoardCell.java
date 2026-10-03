package com.example.omok_server;

import javax.swing.*;
import java.awt.*;

public class BoardCell extends JButton {
    private static final Color WOOD = new Color(220, 179, 92);
    private static final Color LINE = new Color(60, 40, 20);

    private final int col;
    private final int row;
    private final int boardSize;

    public BoardCell(int col, int row, int boardSize) {
        this.col = col;
        this.row = row;
        this.boardSize = boardSize;

        setMargin(new Insets(0, 0, 0, 0));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
    }

    public void placeStone(Icon icon) {
        setIcon(icon);
        setDisabledIcon(icon);
        setEnabled(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth();
        int h = getHeight();
        int cx = w / 2;
        int cy = h / 2;

        g2.setColor(WOOD);
        g2.fillRect(0, 0, w, h);

        g2.setColor(LINE);
        int left = col == 0 ? cx : 0;
        int right = col == boardSize - 1 ? cx : w;
        int top = row == 0 ? cy : 0;
        int bottom = row == boardSize - 1 ? cy : h;
        g2.drawLine(left, cy, right, cy);
        g2.drawLine(cx, top, cx, bottom);

        if (isStarPoint()) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.fillOval(cx - 3, cy - 3, 7, 7);
        }
        g2.dispose();

        super.paintComponent(g);
    }

    private boolean isStarPoint() {
        int edge = 3;
        int mid = boardSize / 2;
        boolean colMatch = col == edge || col == mid || col == boardSize - 1 - edge;
        boolean rowMatch = row == edge || row == mid || row == boardSize - 1 - edge;
        return colMatch && rowMatch;
    }
}
