package controller;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;

import controller.games.ClockTowerGamePage;
import controller.games.HospitalGamePage;
import controller.games.LaboratoryGamePage;
import controller.games.LibraryGamePage;
import controller.games.MirrorHotelGamePage;
import controller.player.PlayerAchievementDialog;
import entity.Game;
import entity.Player;
import service.GameService;
import service.impl.GameServiceImpl;

public class GameMainPage extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final Color BG = new Color(21, 23, 28);
    private static final Color GOLD = new Color(197, 160, 89);
    private static final Color CARD = new Color(32, 36, 46);
    private static final Color TEXT = new Color(235, 220, 195);
    private static final Color MUTED = new Color(160, 165, 175);

    private final Player loginPlayer;
    private final GameService gameService = new GameServiceImpl();
    private JPanel contentPane;
    private JButton activeGameButton;

    public GameMainPage(Player loginPlayer) {
        if (loginPlayer == null || loginPlayer.getPlayerNo() <= 0) {
            throw new IllegalArgumentException("登入玩家資料不完整。");
        }
        this.loginPlayer = loginPlayer;
        initFrame();
        initComponents();
    }

    private void initFrame() {
        setTitle("解謎遊戲平台 - 遊戲大廳");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1160, 780);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        contentPane = new JPanel();
        contentPane.setBackground(BG);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        JLabel title = new JLabel("解 謎 冒 險 大 廳");
        title.setForeground(GOLD);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 29));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBounds(10, 25, 1120, 42);
        contentPane.add(title);

        JLabel subtitle = new JLabel("SELECT YOUR DESTINY");
        subtitle.setForeground(new Color(120, 125, 135));
        subtitle.setFont(new Font(Font.SERIF, Font.ITALIC, 14));
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        subtitle.setBounds(10, 65, 1120, 22);
        contentPane.add(subtitle);

        String name = loginPlayer.getPlayerName();
        if (name == null || name.trim().isEmpty()) {
            name = loginPlayer.getAccount();
        }

        JLabel playerLabel = new JLabel("玩家：" + name);
        playerLabel.setForeground(TEXT);
        playerLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        playerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        playerLabel.setBounds(650, 94, 250, 25);
        contentPane.add(playerLabel);

        JButton achievementButton = createButton(
                "我的成就", new Color(72, 63, 47), TEXT);
        achievementButton.setBounds(910, 92, 100, 30);
        achievementButton.addActionListener(event -> openAchievements());
        contentPane.add(achievementButton);

        JButton logoutButton = createButton(
                "登出", new Color(82, 42, 48), TEXT);
        logoutButton.setBounds(1020, 92, 95, 30);
        logoutButton.addActionListener(event -> logout());
        contentPane.add(logoutButton);

        createGameCard(
                loadGame(1, "失落的圖書館", "簡單",
                        "古老藏書室中，時間、書頁與鑰匙共同封鎖出口。"),
                45, 145, () -> openLibraryGame());

        createGameCard(
                loadGame(2, "逆行的鐘塔", "普通",
                        "鐘塔開始逆向運轉，破解時間機關並逃離午夜循環。"),
                390, 145, () -> openClockTowerGame());

        createGameCard(
                loadGame(3, "霧鎖病棟", "普通",
                        "白霧吞沒病棟，病歷、藥櫃與電梯藏著唯一生路。"),
                735, 145, () -> openHospitalGame());

        createGameCard(
                loadGame(4, "沉沒實驗室", "困難",
                        "海底基地即將崩潰，重啟氧氣、能源與逃生艙。"),
                220, 435, () -> openLaboratoryGame());

        createGameCard(
                loadGame(5, "鏡廳旅館", "困難",
                        "倒影正在奪走記憶，找回房號、姓名並封印主鏡。"),
                565, 435, () -> openMirrorHotelGame());
    }

    private void createGameCard(
            Game game,
            int x,
            int y,
            Runnable launchAction) {
        String title = game.getGameName();
        String description = game.getDescription();
        String difficulty = game.getDifficulty();
        JPanel card = new JPanel();
        card.setBackground(CARD);
        card.setBorder(new LineBorder(GOLD, 1));
        card.setBounds(x, y, 300, 245);
        card.setLayout(null);
        contentPane.add(card);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(TEXT);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 19));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(15, 22, 270, 28);
        card.add(titleLabel);

        JLabel difficultyLabel = new JLabel("難度：" + difficulty);
        difficultyLabel.setForeground(GOLD);
        difficultyLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        difficultyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        difficultyLabel.setBounds(15, 55, 270, 22);
        card.add(difficultyLabel);

        JLabel descriptionLabel = new JLabel(
                "<html><center>" + description + "</center></html>");
        descriptionLabel.setForeground(MUTED);
        descriptionLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        descriptionLabel.setBounds(30, 85, 240, 70);
        card.add(descriptionLabel);

        JButton enterButton = createButton(
                game.isActive() ? "開始解謎" : "目前停用",
                game.isActive() ? GOLD : new Color(78, 80, 88), BG);
        enterButton.setBounds(75, 182, 150, 36);
        enterButton.setEnabled(game.isActive());
        enterButton.addActionListener(event -> {
            activeGameButton = enterButton;
            activeGameButton.setEnabled(false);
            try {
                launchAction.run();
            } catch (RuntimeException ex) {
                activeGameButton.setEnabled(true);
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        this,
                        "無法進入遊戲：\n" + safeMessage(ex),
                        "系統錯誤",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        card.add(enterButton);
    }


    private Game loadGame(
            int gameNo,
            String fallbackName,
            String fallbackDifficulty,
            String fallbackDescription) {
        Game game = gameService.findById(gameNo);
        if (game != null) {
            return game;
        }
        Game fallback = new Game(
                gameNo, fallbackName, fallbackDifficulty, fallbackDescription);
        fallback.setActive(false);
        return fallback;
    }

    private JButton createButton(
            String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setUI(new BasicButtonUI());
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setBorder(new LineBorder(new Color(224, 193, 118), 1));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void openLibraryGame() {
        LibraryGamePage page = new LibraryGamePage(
                loginPlayer.getPlayerNo(), this::showLobbyAfterGame);
        page.setVisible(true);
        setVisible(false);
    }

    private void openClockTowerGame() {
        ClockTowerGamePage page = new ClockTowerGamePage(
                loginPlayer.getPlayerNo(), this::showLobbyAfterGame);
        page.setVisible(true);
        setVisible(false);
    }

    private void openHospitalGame() {
        HospitalGamePage page = new HospitalGamePage(
                loginPlayer.getPlayerNo(), this::showLobbyAfterGame);
        page.setVisible(true);
        setVisible(false);
    }

    private void openLaboratoryGame() {
        LaboratoryGamePage page = new LaboratoryGamePage(
                loginPlayer.getPlayerNo(), this::showLobbyAfterGame);
        page.setVisible(true);
        setVisible(false);
    }

    private void openMirrorHotelGame() {
        MirrorHotelGamePage page = new MirrorHotelGamePage(
                loginPlayer.getPlayerNo(), this::showLobbyAfterGame);
        page.setVisible(true);
        setVisible(false);
    }

    private void showLobbyAfterGame() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::showLobbyAfterGame);
            return;
        }
        if (activeGameButton != null) {
            activeGameButton.setEnabled(true);
        }
        setLocationRelativeTo(null);
        setVisible(true);
        toFront();
        requestFocus();
    }


    private void openAchievements() {
        try {
            PlayerAchievementDialog dialog = new PlayerAchievementDialog(
                    this, loginPlayer);
            dialog.setVisible(true);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "無法開啟成就紀錄：\n" + safeMessage(ex),
                    "成就紀錄錯誤",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "確定要登出嗎？",
                "登出",
                JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            new LoginPage().setVisible(true);
            dispose();
        }
    }

    private String safeMessage(RuntimeException ex) {
        String message = ex.getMessage();
        return message == null || message.trim().isEmpty()
                ? ex.getClass().getSimpleName() : message;
    }
}
