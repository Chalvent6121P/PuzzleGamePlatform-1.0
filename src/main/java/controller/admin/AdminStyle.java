package controller.admin;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;

final class AdminStyle {

    static final Color BG = new Color(16, 19, 27);
    static final Color PANEL = new Color(28, 32, 42);
    static final Color PANEL_ALT = new Color(36, 40, 51);
    static final Color GOLD = new Color(197, 160, 89);
    static final Color TEXT = new Color(235, 220, 195);
    static final Color MUTED = new Color(155, 160, 172);
    static final Color DANGER = new Color(118, 52, 59);
    static final Color SUCCESS = new Color(54, 105, 77);

    private AdminStyle() {
    }

    static JPanel createBackground() {
        return new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(12, 15, 24),
                        getWidth(), getHeight(), new Color(38, 30, 35)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(197, 160, 89, 25));
                g2.fillOval(getWidth() - 250, -100, 340, 340);
                g2.fillOval(-120, getHeight() - 240, 300, 300);
                g2.dispose();
            }
        };
    }

    static JButton button(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setUI(new BasicButtonUI());
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setBorder(new LineBorder(new Color(224, 193, 118), 1));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    static void styleTable(JTable table) {
        table.setBackground(new Color(247, 247, 249));
        table.setForeground(new Color(35, 36, 40));
        table.setSelectionBackground(new Color(224, 205, 160));
        table.setSelectionForeground(new Color(25, 25, 28));
        table.setGridColor(new Color(208, 208, 214));
        table.setRowHeight(28);
        table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        table.getTableHeader().setBackground(new Color(53, 57, 69));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(
                new Font(Font.SANS_SERIF, Font.BOLD, 13));
        table.getTableHeader().setReorderingAllowed(false);
    }

    static String message(Throwable error) {
        Throwable current = error;
        while (current != null) {
            if (current.getMessage() != null
                    && !current.getMessage().trim().isEmpty()) {
                return current.getMessage().trim();
            }
            current = current.getCause();
        }
        return error.getClass().getSimpleName();
    }
}
