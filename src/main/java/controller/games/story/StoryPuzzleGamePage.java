package controller.games.story;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;

import controller.games.common.BackpackDialog;
import service.impl.engine.story.StoryGameDefinition;
import service.impl.engine.story.StoryGameEngine;
import service.impl.engine.story.StoryPuzzle;
import util.SoundPlayer;

public class StoryPuzzleGamePage extends JFrame {

    private static final long serialVersionUID = 1L;

    /*
     * 對話視窗透明度。數字越小越透明，背景圖越清楚。
     * 原版本約為 225，本版本降為 170。
     */
    private static final int CONSOLE_ALPHA = 170;

    private final int playerNo;
    private final Runnable returnToLobbyAction;
    private final StoryGameDefinition definition;
    private final StoryGameEngine engine;

    private int recordNo;
    private int puzzleIndex;
    private boolean leavingPage;

    private JLabel chapterLabel;
    private JLabel puzzleTitleLabel;
    private JTextArea storyArea;
    private JScrollPane storyScrollPane;
    private JPanel consolePanel;
    private JTextField answerField;
    private JButton submitButton;
    private JButton hintButton;
    private JButton soundButton;
    private BufferedImage backgroundImage;

    public StoryPuzzleGamePage(
            int playerNo,
            StoryGameDefinition definition,
            Runnable returnToLobbyAction) {
        if (playerNo <= 0) {
            throw new IllegalArgumentException("playerNo 必須大於 0。");
        }
        if (definition == null) {
            throw new IllegalArgumentException("遊戲定義不可為 null。");
        }

        this.playerNo = playerNo;
        this.definition = definition;
        this.returnToLobbyAction = returnToLobbyAction;
        this.engine = new StoryGameEngine(definition);
        this.backgroundImage = loadBackgroundImage(definition.getBackgroundPath());
        this.recordNo = engine.startStoryGame(playerNo);
        this.puzzleIndex = 0;

        if (recordNo <= 0) {
            throw new IllegalStateException("建立遊戲紀錄失敗。");
        }

        initFrame();
        initComponents();
        showCurrentPuzzle();
    }

    private void initFrame() {
        setTitle(definition.getTitle());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1050, 700);
        setMinimumSize(new java.awt.Dimension(900, 600));
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                requestLeaveGame();
            }
        });
    }

    private void initComponents() {
        JPanel background = createScenePanel();
        background.setLayout(null);
        setContentPane(background);

        JLabel title = new JLabel(definition.getTitle());
        title.setForeground(definition.getAccentColor());
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));
        title.setBounds(55, 35, 620, 45);
        background.add(title);

        JLabel subtitle = new JLabel(definition.getSubtitle());
        subtitle.setForeground(new Color(230, 232, 238));
        subtitle.setFont(new Font(Font.SERIF, Font.ITALIC, 14));
        subtitle.setBounds(58, 82, 620, 25);
        background.add(subtitle);

        chapterLabel = new JLabel();
        chapterLabel.setForeground(new Color(235, 235, 235));
        chapterLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        chapterLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        chapterLabel.setBounds(755, 55, 225, 25);
        background.add(chapterLabel);

        JButton backpackButton = createButton(
                "背包", new Color(51, 58, 70, 215), Color.WHITE);
        backpackButton.setBounds(680, 88, 92, 32);
        backpackButton.addActionListener(event -> openBackpack());
        background.add(backpackButton);

        soundButton = createButton(
                soundButtonText(), new Color(51, 58, 70, 215), Color.WHITE);
        soundButton.setBounds(782, 88, 116, 32);
        soundButton.addActionListener(event -> toggleSound());
        background.add(soundButton);

        consolePanel = createTransparentConsole();
        consolePanel.setLayout(new BorderLayout(12, 12));
        consolePanel.setBorder(new EmptyBorder(22, 24, 22, 24));
        consolePanel.setBounds(55, 135, 925, 440);
        background.add(consolePanel);

        puzzleTitleLabel = new JLabel();
        puzzleTitleLabel.setForeground(definition.getAccentColor());
        puzzleTitleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 21));
        consolePanel.add(puzzleTitleLabel, BorderLayout.NORTH);

        storyArea = new JTextArea();
        storyArea.setEditable(false);
        storyArea.setFocusable(false);
        storyArea.setLineWrap(true);
        storyArea.setWrapStyleWord(true);
        storyArea.setOpaque(false);
        storyArea.setDoubleBuffered(false);
        storyArea.setForeground(new Color(245, 244, 238));
        storyArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 17));
        storyArea.setBorder(new EmptyBorder(10, 2, 10, 2));

        storyScrollPane = new JScrollPane(storyArea);
        storyScrollPane.setBorder(null);
        storyScrollPane.setOpaque(false);
        storyScrollPane.getViewport().setOpaque(false);
        /*
         * 透明文字區使用 SIMPLE_SCROLL_MODE，避免切換謎題後留下上一題文字殘影。
         */
        storyScrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        consolePanel.add(storyScrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setOpaque(false);

        answerField = new JTextField();
        answerField.setBackground(new Color(21, 25, 34, 225));
        answerField.setForeground(Color.WHITE);
        answerField.setCaretColor(Color.WHITE);
        answerField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        answerField.setBorder(new LineBorder(new Color(120, 125, 140), 1));
        answerField.addActionListener(event -> submitAnswer());
        inputPanel.add(answerField, BorderLayout.CENTER);

        hintButton = createButton(
                "提示", new Color(55, 60, 72, 225), Color.WHITE);
        hintButton.addActionListener(event -> showHint());
        inputPanel.add(hintButton, BorderLayout.WEST);

        submitButton = createButton(
                "確認答案", definition.getAccentColor(), new Color(18, 20, 26));
        submitButton.addActionListener(event -> submitAnswer());
        inputPanel.add(submitButton, BorderLayout.EAST);

        consolePanel.add(inputPanel, BorderLayout.SOUTH);

        JButton leaveButton = createButton(
                "返回大廳", new Color(77, 42, 50, 225), Color.WHITE);
        leaveButton.setBounds(835, 605, 145, 38);
        leaveButton.addActionListener(event -> requestLeaveGame());
        background.add(leaveButton);
    }

    private JPanel createTransparentConsole() {
        JPanel panel = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setComposite(AlphaComposite.SrcOver);
                g2.setColor(new Color(8, 12, 20, CONSOLE_ALPHA));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(new Color(220, 225, 235, 55));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    private BufferedImage loadBackgroundImage(String path) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }

        try (InputStream input = getClass().getResourceAsStream(path)) {
            if (input == null) {
                return null;
            }
            return ImageIO.read(input);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private JPanel createScenePanel() {
        return new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(
                        0, 0, definition.getStartColor(),
                        getWidth(), getHeight(), definition.getEndColor()));
                g2.fillRect(0, 0, getWidth(), getHeight());

                if (backgroundImage != null) {
                    g2.drawImage(
                            backgroundImage,
                            0,
                            0,
                            getWidth(),
                            getHeight(),
                            this);
                    /* 原本遮罩約 105，降到 42，讓背景更清楚。 */
                    g2.setColor(new Color(5, 7, 12, 42));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }

                g2.setColor(new Color(
                        definition.getAccentColor().getRed(),
                        definition.getAccentColor().getGreen(),
                        definition.getAccentColor().getBlue(),
                        18));
                g2.fillOval(-120, 340, 420, 420);
                g2.fillOval(750, -180, 430, 430);

                drawSceneSilhouette(g2);
                g2.dispose();
            }
        };
    }

    private void drawSceneSilhouette(Graphics2D g2) {
        /* 降低裝飾剪影透明度，避免背景照片被壓暗。 */
        g2.setColor(new Color(4, 7, 12, 52));
        int width = getWidth();
        int height = getHeight();
        g2.fillRect(0, height - 140, width, 140);

        int gameNo = definition.getGameNo();
        if (gameNo == 2) {
            g2.fillRect(760, 80, 150, 390);
            g2.fillOval(775, 95, 120, 120);
            g2.drawLine(835, 155, 835, 110);
            g2.drawLine(835, 155, 875, 180);
        } else if (gameNo == 3) {
            for (int x = 90; x < width; x += 160) {
                g2.fillRect(x, 90, 85, 420);
                g2.fillRect(x + 10, 145, 65, 12);
            }
        } else if (gameNo == 4) {
            g2.fillOval(760, 90, 190, 300);
            g2.drawOval(790, 125, 130, 130);
            g2.fillRect(115, 105, 200, 315);
        } else {
            for (int x = 100; x < width; x += 205) {
                g2.drawRoundRect(x, 85, 125, 360, 20, 20);
                g2.drawLine(x + 62, 85, x + 62, 445);
            }
        }
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
        button.setMargin(new Insets(0, 14, 0, 14));
        button.setBorder(new LineBorder(new Color(225, 225, 225, 105), 1));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void showCurrentPuzzle() {
        StoryPuzzle puzzle = definition.getPuzzles().get(puzzleIndex);
        chapterLabel.setText(
                "謎題 " + (puzzleIndex + 1) + " / "
                        + definition.getPuzzles().size());
        puzzleTitleLabel.setText(toDisplaySafeText(puzzle.getTitle()));
        storyArea.setText("");
        storyArea.setText(toDisplaySafeText(
                puzzle.getSceneText() + "\n\n" + puzzle.getQuestion()));
        storyArea.setCaretPosition(0);
        storyArea.revalidate();
        storyArea.repaint();
        if (storyScrollPane != null) {
            storyScrollPane.getViewport().setViewPosition(new Point(0, 0));
            storyScrollPane.getViewport().revalidate();
            storyScrollPane.getViewport().repaint();
            storyScrollPane.repaint();
        }
        if (consolePanel != null) {
            consolePanel.revalidate();
            consolePanel.repaint();
        }
        getContentPane().repaint();
        answerField.setText("");
        answerField.requestFocusInWindow();
    }

    private void submitAnswer() {
        String input = answerField.getText().trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "請先輸入答案。",
                    "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        StoryPuzzle puzzle = definition.getPuzzles().get(puzzleIndex);
        setInputEnabled(false);

        try {
            String result = engine.submitAnswer(
                    recordNo, puzzle.getPuzzleNo(), input);

            if (result.startsWith("WRONG::")) {
                SoundPlayer.play("/sounds/wrong_answer.wav");
                JOptionPane.showMessageDialog(
                        this,
                        result.substring("WRONG::".length()),
                        "答案錯誤",
                        JOptionPane.WARNING_MESSAGE);
                answerField.selectAll();
                return;
            }

            if (result.startsWith("ERROR::")) {
                throw new IllegalStateException(
                        result.substring("ERROR::".length()));
            }

            SoundPlayer.play(puzzle.getSuccessSoundPath());

            if (result.startsWith("CLEAR::")) {
                JOptionPane.showMessageDialog(
                        this,
                        result.substring("CLEAR::".length()),
                        "遊戲通關",
                        JOptionPane.INFORMATION_MESSAGE);
                returnToLobby();
                return;
            }

            String message = result.startsWith("CORRECT::")
                    ? result.substring("CORRECT::".length()) : result;
            JOptionPane.showMessageDialog(
                    this,
                    message,
                    "謎題解開",
                    JOptionPane.INFORMATION_MESSAGE);
            puzzleIndex++;
            showCurrentPuzzle();

        } catch (RuntimeException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "遊戲執行錯誤：\n" + safeMessage(ex),
                    "系統錯誤",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            if (isDisplayable()) {
                setInputEnabled(true);
            }
        }
    }

    private void showHint() {
        SoundPlayer.play("/sounds/paper_open.wav");
        StoryPuzzle puzzle = definition.getPuzzles().get(puzzleIndex);
        JOptionPane.showMessageDialog(
                this,
                toDisplaySafeText(puzzle.getHint()),
                "謎題提示",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void openBackpack() {
        try {
            SoundPlayer.play("/sounds/bag_open.wav");
            BackpackDialog dialog = new BackpackDialog(
                    this,
                    engine.getInventoryItems(recordNo),
                    definition.getAccentColor());
            dialog.setVisible(true);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "讀取背包失敗：\n" + safeMessage(ex),
                    "背包錯誤",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void toggleSound() {
        boolean enabled = SoundPlayer.toggleEnabled();
        soundButton.setText(soundButtonText());
        if (enabled) {
            SoundPlayer.play("/sounds/item_select.wav");
        }
    }

    private String soundButtonText() {
        return SoundPlayer.isEnabled() ? "音效：開" : "音效：關";
    }

    private void requestLeaveGame() {
        if (leavingPage) {
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "目前進度尚未完成，確定返回遊戲大廳嗎？",
                "離開遊戲",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            returnToLobby();
        }
    }

    private void returnToLobby() {
        if (leavingPage) {
            return;
        }
        leavingPage = true;
        dispose();
        if (returnToLobbyAction != null) {
            returnToLobbyAction.run();
        }
    }

    private void setInputEnabled(boolean enabled) {
        answerField.setEnabled(enabled);
        submitButton.setEnabled(enabled);
        hintButton.setEnabled(enabled);
    }


    /**
     * 將部分在不同 Windows 字型上容易顯示成方框的數學／箭頭符號，
     * 轉成所有 Java 邏輯字型都能穩定顯示的文字。
     */
    private String toDisplaySafeText(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("₀", "0")
                .replace("₁", "1")
                .replace("₂", "2")
                .replace("₃", "3")
                .replace("₄", "4")
                .replace("₅", "5")
                .replace("₆", "6")
                .replace("₇", "7")
                .replace("₈", "8")
                .replace("₉", "9")
                .replace("↔", " 對應 ")
                .replace("→", " -> ")
                .replace("←", " <- ")
                .replace("×", " x ")
                .replace("÷", " / ")
                .replace("－", "-")
                .replace("……", "...")
                .replace("…", "...");
    }

    private String safeMessage(RuntimeException ex) {
        String message = ex.getMessage();
        return message == null || message.trim().isEmpty()
                ? ex.getClass().getSimpleName() : message;
    }
}
