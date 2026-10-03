package com.example.omok_server;

import javax.swing.*;
import java.awt.*;

public class StoneIcon implements Icon {
    private final int size;
    private final boolean black;

    public StoneIcon(int size, boolean black) {
        this.size = size;
        this.black = black;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(0, 0, 0, 60));
        g2.fillOval(x + 2, y + 3, size - 2, size - 2);

        Color light = black ? new Color(100, 100, 100) : Color.WHITE;
        Color dark = black ? new Color(10, 10, 10) : new Color(190, 190, 185);
        g2.setPaint(new RadialGradientPaint(
                x + size * 0.35f, y + size * 0.3f, size * 0.75f,
                new float[]{0f, 1f}, new Color[]{light, dark}));
        g2.fillOval(x, y, size - 2, size - 2);

        g2.setColor(new Color(0, 0, 0, black ? 120 : 70));
        g2.drawOval(x, y, size - 3, size - 3);
        g2.dispose();
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }
}
