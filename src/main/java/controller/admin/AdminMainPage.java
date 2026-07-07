package controller.admin;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import controller.LoginPage;
import entity.Player;
import entity.ReportSummary;
import service.AdminReportService;
import service.impl.AdminReportServiceImpl;

public class AdminMainPage extends JFrame {

    private static final long serialVersionUID = 1L;

    private final Player adminPlayer;
    private final AdminReportService reportService = new AdminReportServiceImpl();

    private JLabel playerCountLabel;
    private JLabel gameCountLabel;
    private JLabel recordCountLabel;
    private JLabel statusLabel;

    public AdminMainPage(Player adminPlayer) {
        if (adminPlayer == null || !adminPlayer.isAdmin()) {
            throw new IllegalArgumentException("只有管理員可以進入管理介面。");
        }
        this.adminPlayer = adminPlayer;
        initFrame();
        initComponents();
        refreshStats();
    }

    private void initFrame() {
        setTitle("PuzzleGamePlatform - 管理員控制中心");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1040, 680);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel background = new JPanel() {
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
                g2.setColor(new Color(197, 160, 89, 34));
                g2.fillOval(745, -80, 340, 340);
                g2.fillOval(-110, 390, 300, 300);
                g2.dispose();
            }
        };
        background.setLayout(null);
        setContentPane(background);

        JLabel title = new JLabel("管理員控制中心");
        title.setForeground(AdminStyle.GOLD);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
        title.setBounds(60, 40, 500, 45);
        background.add(title);

        JLabel subtitle = new JLabel(
                "ADMINISTRATOR · " + adminPlayer.getPlayerName());
        subtitle.setForeground(AdminStyle.MUTED);
        subtitle.setFont(new Font(Font.SERIF, Font.ITALIC, 14));
        subtitle.setBounds(63, 86, 500, 25);
        background.add(subtitle);

        playerCountLabel = createStatCard(background, "玩家總數", "-", 60);
        gameCountLabel = createStatCard(background, "遊戲總數", "-", 270);
        recordCountLabel = createStatCard(background, "遊玩紀錄", "-", 480);
        statusLabel = createStatCard(background, "目前版本", "PHASE 2", 690);

        createFunctionCard(background, "玩家管理",
                "新增、修改、停用或永久刪除玩家與關聯紀錄。",
                60, () -> openPlayerManagement());

        createFunctionCard(background, "遊戲內容管理",
                "管理遊戲、房間、謎題、道具與結局。",
                370, () -> openGameManagement());

        createFunctionCard(background, "報表中心",
                "查詢平台統計並匯出 PDF、TXT、CSV。",
                680, () -> openReportCenter());

        JButton refresh = AdminStyle.button(
                "更新統計", new Color(63, 66, 77), Color.WHITE);
        refresh.setBounds(725, 45, 115, 36);
        refresh.addActionListener(event -> refreshStats());
        background.add(refresh);

        JButton logoutButton = AdminStyle.button(
                "登出", new Color(82, 42, 48), AdminStyle.TEXT);
        logoutButton.setBounds(855, 45, 115, 36);
        logoutButton.addActionListener(event -> logout());
        background.add(logoutButton);
    }

    private JLabel createStatCard(
            JPanel parent, String title, String value, int x) {
        JPanel card = new JPanel();
        card.setBackground(new Color(24, 28, 37));
        card.setBorder(new LineBorder(new Color(70, 74, 85), 1));
        card.setBounds(x, 135, 180, 95);
        card.setLayout(null);
        parent.add(card);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(AdminStyle.MUTED);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        titleLabel.setBounds(15, 12, 150, 20);
        card.add(titleLabel);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(AdminStyle.GOLD);
        valueLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        valueLabel.setBounds(15, 38, 155, 38);
        card.add(valueLabel);
        return valueLabel;
    }

    private void createFunctionCard(
            JPanel parent, String title, String description,
            int x, Runnable action) {
        JPanel card = new JPanel();
        card.setBackground(AdminStyle.PANEL);
        card.setBorder(new LineBorder(AdminStyle.GOLD, 1));
        card.setBounds(x, 280, 280, 270);
        card.setLayout(null);
        parent.add(card);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(AdminStyle.TEXT);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(15, 32, 250, 30);
        card.add(titleLabel);

        JLabel descriptionLabel = new JLabel(
                "<html><center>" + description + "</center></html>");
        descriptionLabel.setForeground(AdminStyle.MUTED);
        descriptionLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        descriptionLabel.setBounds(30, 85, 220, 75);
        card.add(descriptionLabel);

        JButton openButton = AdminStyle.button(
                "進入管理", AdminStyle.GOLD, new Color(20, 22, 30));
        openButton.setBounds(65, 195, 150, 38);
        openButton.addActionListener(event -> action.run());
        card.add(openButton);
    }

    private void refreshStats() {
        try {
            ReportSummary summary = reportService.loadSummary();
            playerCountLabel.setText(String.valueOf(summary.getPlayerCount()));
            gameCountLabel.setText(String.valueOf(summary.getGameCount()));
            recordCountLabel.setText(String.valueOf(summary.getRecordCount()));
            statusLabel.setText("PHASE 2");
        } catch (RuntimeException error) {
            error.printStackTrace();
            statusLabel.setText("DB ERROR");
            JOptionPane.showMessageDialog(this,
                    AdminStyle.message(error), "統計讀取失敗",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openPlayerManagement() {
        setVisible(false);
        new PlayerManagementPage(adminPlayer, this::showAdminHome)
                .setVisible(true);
    }

    private void openGameManagement() {
        setVisible(false);
        new GameManagementPage(adminPlayer, this::showAdminHome)
                .setVisible(true);
    }

    private void openReportCenter() {
        setVisible(false);
        new ReportCenterPage(adminPlayer, this::showAdminHome)
                .setVisible(true);
    }

    private void showAdminHome() {
        refreshStats();
        setLocationRelativeTo(null);
        setVisible(true);
        toFront();
        requestFocus();
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "確定要登出管理員介面嗎？", "登出",
                JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            new LoginPage().setVisible(true);
            dispose();
        }
    }
}
