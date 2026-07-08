package controller.games.story;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;

import controller.games.common.BackpackDialog;
import entity.Item;
import service.impl.engine.story.StoryGameDefinition;
import service.impl.engine.story.StoryGameEngine;
import service.impl.engine.story.StoryPuzzle;
import util.SoundPlayer;

/**
 * 四款故事遊戲的沉浸式 2.5D 畫面。
 *
 * <p>畫面直接使用使用者認可的四張完整遊戲介面圖；標示區域上方覆蓋真正可操作的
 * Swing 熱區。謎題與提示改成小型半透明浮動卡片，不再用大型固定對話框遮住場景。
 * 點擊卡片以外的任何場景位置會自動關閉卡片。</p>
 */
public class StoryPuzzleGamePage extends JFrame {

    private static final long serialVersionUID = 1L;

    private final int playerNo;
    private final Runnable returnToLobbyAction;
    private final StoryGameDefinition definition;
    private final StoryGameEngine engine;
    private final int recordNo;

    private int puzzleIndex;
    private boolean leavingPage;
    private boolean gameCleared;

    private CinematicScenePanel scenePanel;
    private JPanel popupCard;
    private Rectangle popupBounds = new Rectangle();

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
        this.recordNo = engine.startStoryGame(playerNo);
        this.puzzleIndex = 0;
        this.leavingPage = false;
        this.gameCleared = false;

        if (recordNo <= 0) {
            throw new IllegalStateException("建立遊戲紀錄失敗。");
        }

        initFrame();
        initScene();
    }

    private void initFrame() {
        setTitle(definition.getTitle() + " - 2.5D");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 610));
        setPreferredSize(new Dimension(1280, 720));
        setLocationByPlatform(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                showLeaveConfirmation();
            }

            @Override
            public void windowClosed(WindowEvent event) {
                if (scenePanel != null) {
                    scenePanel.stopAnimation();
                }
            }
        });
    }

    private void initScene() {
        BufferedImage background = loadRequiredImage(
                definition.getBackgroundPath());
        BufferedImage foreground = loadOptionalImage(
                foregroundPathFor(definition.getGameNo()));

        scenePanel = new CinematicScenePanel(
                background,
                foreground,
                definition.getAccentColor()
        );
        setContentPane(scenePanel);

        installSceneHotspots();
        installUiHotspots();
        installDismissBehavior();

        pack();
        setLocationRelativeTo(null);

        EventQueue.invokeLater(() -> showIntroCard());
    }

    private void installDismissBehavior() {
        scenePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                if (popupCard != null
                        && popupCard.isVisible()
                        && !popupBounds.contains(event.getPoint())) {
                    hidePopup();
                }
            }
        });

        getRootPane().getInputMap(
                JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                        "dismiss-popup");
        getRootPane().getActionMap().put(
                "dismiss-popup",
                new AbstractAction() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(ActionEvent event) {
                        hidePopup();
                    }
                }
        );
    }

    private void installSceneHotspots() {
        for (Hotspot hotspot : hotspotsFor(definition.getGameNo())) {
            scenePanel.addHotspot(
                    hotspot.name,
                    hotspot.x,
                    hotspot.y,
                    hotspot.width,
                    hotspot.height,
                    hotspot.depth,
                    () -> handleHotspot(hotspot)
            );
        }
    }

    private void installUiHotspots() {
        int gameNo = definition.getGameNo();

        if (gameNo == 4) {
            addUi("提示", 0.018, 0.790, 0.090, 0.180,
                    this::showHintCard);
            addUi("背包物品欄", 0.250, 0.825, 0.510, 0.150,
                    this::openBackpack);
            addUi("返回", 0.925, 0.665, 0.070, 0.115,
                    this::showLeaveConfirmation);
            addUi("選單", 0.920, 0.810, 0.075, 0.155,
                    this::showSettingsCard);
        } else {
            addUi("提示", 0.765, 0.020, 0.075, 0.145,
                    this::showHintCard);
            addUi("道具", 0.845, 0.020, 0.075, 0.145,
                    this::openBackpack);
            addUi("返回", 0.925, 0.020, 0.070, 0.145,
                    this::showLeaveConfirmation);
        }
    }

    private void addUi(
            String name,
            double x,
            double y,
            double width,
            double height,
            Runnable action) {

        scenePanel.addHotspot(
                name, x, y, width, height, 0.15,
                () -> {
                    hidePopup();
                    action.run();
                }
        );
    }

    private void handleHotspot(Hotspot hotspot) {
        hidePopup();

        if (hotspot.puzzleOrder > 0) {
            openPuzzle(hotspot);
            return;
        }

        if (hotspot.soundPath != null) {
            SoundPlayer.play(hotspot.soundPath);
        }

        showInfoCard(
                hotspot.name,
                hotspot.message,
                hotspot.centerX(),
                hotspot.centerY()
        );
    }

    private void openPuzzle(Hotspot hotspot) {
        int wantedIndex = hotspot.puzzleOrder - 1;

        if (wantedIndex < puzzleIndex) {
            showInfoCard(
                    hotspot.name,
                    "這個機關已經解開。你可以繼續調查下一個發光區域。",
                    hotspot.centerX(),
                    hotspot.centerY()
            );
            return;
        }

        if (wantedIndex > puzzleIndex) {
            StoryPuzzle missing = definition.getPuzzles().get(puzzleIndex);
            showInfoCard(
                    hotspot.name,
                    "目前缺少前一個區域取得的線索或道具。\n"
                  + "請先解開：「" + missing.getTitle() + "」。",
                    hotspot.centerX(),
                    hotspot.centerY()
            );
            return;
        }

        StoryPuzzle puzzle = definition.getPuzzles().get(puzzleIndex);
        showPuzzleCard(
                puzzle,
                hotspot.centerX(),
                hotspot.centerY()
        );
    }

    private void showIntroCard() {
        showInfoCard(
                definition.getTitle(),
                definition.getIntroduction()
              + "\n\n點擊畫面中帶有光暈或機關感的區域開始探索。"
              + "\n點擊浮動訊息以外的場景即可關閉訊息。",
                0.50,
                0.50
        );
    }

    private void showHintCard() {
        SoundPlayer.play("/sounds/paper_open.wav");

        if (gameCleared || puzzleIndex >= definition.getPuzzles().size()) {
            showInfoCard(
                    "提示",
                    "所有主要機關都已完成。",
                    0.83,
                    0.10
            );
            return;
        }

        StoryPuzzle puzzle = definition.getPuzzles().get(puzzleIndex);
        showInfoCard(
                "提示｜" + puzzle.getTitle(),
                puzzle.getHint(),
                0.83,
                0.10
        );
    }

    private void showSettingsCard() {
        JPanel panel = createPopupBase(
                "遊戲選單",
                "音效狀態：" + (SoundPlayer.isEnabled() ? "開啟" : "關閉")
        );

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);

        JButton soundButton = createPopupButton(
                SoundPlayer.isEnabled() ? "關閉音效" : "開啟音效");
        soundButton.addActionListener(event -> {
            SoundPlayer.toggleEnabled();
            hidePopup();
            showSettingsCard();
        });

        JButton leaveButton = createPopupButton("返回大廳");
        leaveButton.addActionListener(event -> showLeaveConfirmation());

        buttons.add(soundButton);
        buttons.add(leaveButton);
        panel.add(buttons, BorderLayout.SOUTH);

        showPopup(panel, 0.88, 0.82, 360, 185);
    }

    private void showLeaveConfirmation() {
        JPanel panel = createPopupBase(
                "返回遊戲大廳",
                "確定要離開「" + definition.getTitle() + "」嗎？\n"
              + "本次尚未完成的遊戲紀錄會保留。"
        );

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);

        JButton cancelButton = createPopupButton("繼續探索");
        cancelButton.addActionListener(event -> hidePopup());

        JButton leaveButton = createPopupButton("確定返回");
        leaveButton.addActionListener(event -> returnToLobby());

        buttons.add(cancelButton);
        buttons.add(leaveButton);
        panel.add(buttons, BorderLayout.SOUTH);

        showPopup(panel, 0.88, 0.15, 410, 210);
    }

    private void openBackpack() {
        try {
            SoundPlayer.play("/sounds/bag_open.wav");
            List<Item> items = engine.getInventoryItems(recordNo);
            BackpackDialog dialog = new BackpackDialog(
                    this, items, definition.getAccentColor());
            dialog.setVisible(true);
        } catch (RuntimeException ex) {
            showInfoCard(
                    "背包讀取失敗",
                    safeMessage(ex),
                    0.84,
                    0.15
            );
        }
    }

    private void showPuzzleCard(
            StoryPuzzle puzzle,
            double anchorX,
            double anchorY) {

        JPanel panel = createPopupBase(
                puzzle.getTitle(),
                puzzle.getSceneText() + "\n\n" + puzzle.getQuestion()
        );

        JPanel inputRow = new JPanel(new BorderLayout(8, 0));
        inputRow.setOpaque(false);
        inputRow.setBorder(new EmptyBorder(8, 0, 0, 0));

        JTextField answerField = new JTextField();
        answerField.setFont(new Font(
                "Microsoft JhengHei", Font.PLAIN, 15));
        answerField.setBackground(new Color(20, 22, 27, 225));
        answerField.setForeground(Color.WHITE);
        answerField.setCaretColor(Color.WHITE);
        answerField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(218, 185, 112, 150), 1),
                new EmptyBorder(7, 9, 7, 9)
        ));

        JButton submitButton = createPopupButton("確認答案");
        JButton hintButton = createPopupButton("提示");

        Runnable submit = () -> submitPuzzleAnswer(
                puzzle,
                answerField,
                panel,
                anchorX,
                anchorY
        );

        submitButton.addActionListener(event -> submit.run());
        answerField.addActionListener(event -> submit.run());
        hintButton.addActionListener(event -> {
            showInfoCard(
                    "提示｜" + puzzle.getTitle(),
                    puzzle.getHint(),
                    anchorX,
                    anchorY
            );
        });

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.add(hintButton);
        right.add(submitButton);

        inputRow.add(answerField, BorderLayout.CENTER);
        inputRow.add(right, BorderLayout.EAST);
        panel.add(inputRow, BorderLayout.SOUTH);

        showPopup(panel, anchorX, anchorY, 510, 310);
        EventQueue.invokeLater(answerField::requestFocusInWindow);
    }

    private void submitPuzzleAnswer(
            StoryPuzzle puzzle,
            JTextField answerField,
            JPanel currentPanel,
            double anchorX,
            double anchorY) {

        String input = answerField.getText().trim();

        if (input.isEmpty()) {
            answerField.requestFocusInWindow();
            return;
        }

        try {
            String result = engine.submitAnswer(
                    recordNo,
                    puzzle.getPuzzleNo(),
                    input
            );

            if (result.startsWith("WRONG::")) {
                SoundPlayer.play("/sounds/wrong_answer.wav");
                showInfoCard(
                        "答案錯誤｜" + puzzle.getTitle(),
                        result.substring("WRONG::".length())
                      + "\n\n提示仍可由畫面右上角的『提示』按鈕查看。",
                        anchorX,
                        anchorY
                );
                return;
            }

            if (result.startsWith("ERROR::")) {
                throw new IllegalStateException(
                        result.substring("ERROR::".length()));
            }

            SoundPlayer.play(puzzle.getSuccessSoundPath());

            if (result.startsWith("CLEAR::")) {
                gameCleared = true;
                puzzleIndex = definition.getPuzzles().size();
                showClearCard(result.substring("CLEAR::".length()));
                return;
            }

            String message = result.startsWith("CORRECT::")
                    ? result.substring("CORRECT::".length())
                    : result;

            puzzleIndex++;
            showInfoCard(
                    "謎題解開",
                    message
                  + "\n\n下一個可調查區域已經解鎖。",
                    anchorX,
                    anchorY
            );

        } catch (RuntimeException ex) {
            showInfoCard(
                    "遊戲執行錯誤",
                    safeMessage(ex),
                    anchorX,
                    anchorY
            );
        }
    }

    private void showClearCard(String message) {
        JPanel panel = createPopupBase(
                "遊戲通關",
                message
        );

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        JButton returnButton = createPopupButton("返回遊戲大廳");
        returnButton.addActionListener(event -> returnToLobby());
        buttons.add(returnButton);
        panel.add(buttons, BorderLayout.SOUTH);

        showPopup(panel, 0.50, 0.50, 540, 330);
    }

    private void showInfoCard(
            String title,
            String message,
            double anchorX,
            double anchorY) {

        JPanel panel = createPopupBase(title, message);
        showPopup(panel, anchorX, anchorY, 450, 245);
    }

    private JPanel createPopupBase(String title, String message) {
        JPanel panel = new TranslucentCard(definition.getAccentColor());
        panel.setLayout(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(14, 16, 14, 16));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(definition.getAccentColor());
        titleLabel.setFont(new Font(
                "Microsoft JhengHei", Font.BOLD, 18));

        JButton closeButton = createCloseButton();
        closeButton.addActionListener(event -> hidePopup());

        header.add(titleLabel, BorderLayout.CENTER);
        header.add(closeButton, BorderLayout.EAST);

        JTextArea messageArea = new JTextArea(message);
        messageArea.setEditable(false);
        messageArea.setFocusable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setOpaque(false);
        messageArea.setForeground(new Color(244, 238, 221));
        messageArea.setFont(new Font(
                "Microsoft JhengHei", Font.PLAIN, 15));
        messageArea.setBorder(new EmptyBorder(4, 1, 4, 1));

        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        panel.add(header, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JButton createPopupButton(String text) {
        JButton button = new JButton(text);
        button.setUI(new BasicButtonUI());
        button.setFont(new Font(
                "Microsoft JhengHei", Font.BOLD, 13));
        button.setForeground(new Color(24, 20, 16));
        button.setBackground(definition.getAccentColor());
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(
                Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        brighten(definition.getAccentColor(), 35), 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return button;
    }

    private JButton createCloseButton() {
        JButton button = new JButton("×");
        button.setUI(new BasicButtonUI());
        button.setForeground(new Color(235, 225, 208));
        button.setBackground(new Color(0, 0, 0, 0));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        button.setCursor(Cursor.getPredefinedCursor(
                Cursor.HAND_CURSOR));
        return button;
    }

    private void showPopup(
            JPanel panel,
            double anchorX,
            double anchorY,
            int preferredWidth,
            int preferredHeight) {

        hidePopup();

        popupCard = panel;
        scenePanel.add(popupCard, JLayeredPane.POPUP_LAYER);
        layoutPopup(anchorX, anchorY, preferredWidth, preferredHeight);
        popupCard.setVisible(true);
        popupCard.revalidate();
        popupCard.repaint();
    }

    private void layoutPopup(
            double anchorX,
            double anchorY,
            int preferredWidth,
            int preferredHeight) {

        Rectangle image = scenePanel.getVisibleImageBounds();

        int width = Math.min(preferredWidth,
                Math.max(340, (int) (image.width * 0.42)));
        int height = Math.min(preferredHeight,
                Math.max(180, (int) (image.height * 0.42)));

        int anchorPixelX = image.x + (int) (image.width * anchorX);
        int margin = Math.max(18, (int) (image.width * 0.018));

        int x;
        if (anchorX >= 0.50) {
            x = image.x + margin;
        } else {
            x = image.x + image.width - width - margin;
        }

        int y = image.y + (int) (image.height * 0.22);

        if (anchorY > 0.65) {
            y = image.y + margin;
        }

        x = Math.max(image.x + margin,
                Math.min(x, image.x + image.width - width - margin));
        y = Math.max(image.y + margin,
                Math.min(y, image.y + image.height - height - margin));

        popupBounds = new Rectangle(x, y, width, height);
        popupCard.setBounds(popupBounds);
    }

    private void hidePopup() {
        if (popupCard != null) {
            scenePanel.remove(popupCard);
            popupCard = null;
            popupBounds = new Rectangle();
            scenePanel.revalidate();
            scenePanel.repaint();
        }
    }

    private void returnToLobby() {
        if (leavingPage) {
            return;
        }

        leavingPage = true;
        hidePopup();
        scenePanel.stopAnimation();
        dispose();

        if (returnToLobbyAction != null) {
            EventQueue.invokeLater(returnToLobbyAction);
        }
    }

    private BufferedImage loadRequiredImage(String path) {
        BufferedImage image = loadOptionalImage(path);
        if (image == null) {
            throw new IllegalStateException("找不到遊戲畫面：" + path);
        }
        return image;
    }

    private BufferedImage loadOptionalImage(String path) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }
        try (InputStream input = getClass().getResourceAsStream(path)) {
            return input == null ? null : ImageIO.read(input);
        } catch (IOException ex) {
            return null;
        }
    }

    private static String foregroundPathFor(int gameNo) {
        switch (gameNo) {
            case 2:
                return "/images/games/cinematic/layers/clock_tower_foreground.png";
            case 3:
                return "/images/games/cinematic/layers/hospital_foreground.png";
            case 4:
                return "/images/games/cinematic/layers/laboratory_foreground.png";
            case 5:
                return "/images/games/cinematic/layers/mirror_hotel_foreground.png";
            default:
                return null;
        }
    }

    private static List<Hotspot> hotspotsFor(int gameNo) {
        List<Hotspot> hotspots = new ArrayList<>();

        switch (gameNo) {
            case 2:
                hotspots.add(Hotspot.puzzle(
                        "鏡面鐘盤", 0.525, 0.205, 0.165, 0.285, 0.48, 1));
                hotspots.add(Hotspot.puzzle(
                        "齒輪機關箱", 0.632, 0.535, 0.145, 0.205, 0.78, 2));
                hotspots.add(Hotspot.puzzle(
                        "主鐘校準拉桿", 0.770, 0.620, 0.115, 0.205, 0.92, 3));
                hotspots.add(Hotspot.lore(
                        "時間校準圖", 0.395, 0.475, 0.145, 0.190, 0.66,
                        "圖紙畫著鏡面鐘盤、齒輪咬合方向與午夜校準公式。",
                        "/sounds/paper_open.wav"));
                hotspots.add(Hotspot.lore(
                        "鐘塔入口", 0.505, 0.585, 0.075, 0.185, 0.60,
                        "門後傳來倒轉的鐘聲。完成三段機關後，時間鎖才會解除。",
                        "/sounds/room_door.wav"));
                break;

            case 3:
                hotspots.add(Hotspot.puzzle(
                        "隔離藥櫃", 0.650, 0.285, 0.125, 0.330, 0.70, 1));
                hotspots.add(Hotspot.puzzle(
                        "護士輪值板", 0.325, 0.205, 0.090, 0.235, 0.54, 2));
                hotspots.add(Hotspot.puzzle(
                        "電力控制卡抽屜", 0.735, 0.610, 0.100, 0.215, 0.92, 3));
                hotspots.add(Hotspot.lore(
                        "病歷記錄", 0.845, 0.660, 0.140, 0.170, 0.88,
                        "紙上只剩病患編號與被塗去的診療日期，背面寫著：『規律藏在輪班日。』",
                        "/sounds/paper_open.wav"));
                hotspots.add(Hotspot.lore(
                        "霧封電梯", 0.485, 0.265, 0.130, 0.340, 0.38,
                        "電梯門後亮著微弱的綠色出口燈，但面板需要權限碼。",
                        "/sounds/elevator_open.wav"));
                break;

            case 4:
                hotspots.add(Hotspot.puzzle(
                        "能源反應堆核心", 0.195, 0.230, 0.180, 0.310, 0.62, 1));
                hotspots.add(Hotspot.puzzle(
                        "聲納控制台", 0.610, 0.345, 0.170, 0.260, 0.78, 2));
                hotspots.add(Hotspot.puzzle(
                        "逃生艙傳輸艙", 0.765, 0.300, 0.150, 0.290, 0.70, 3));
                hotspots.add(Hotspot.lore(
                        "研究站主艙", 0.355, 0.135, 0.265, 0.245, 0.32,
                        "研究站內部仍有應急照明。桌面散落著艙壓紀錄與撤離名單。",
                        "/sounds/paper_open.wav"));
                hotspots.add(Hotspot.lore(
                        "維修工具箱", 0.585, 0.640, 0.120, 0.150, 0.90,
                        "箱內缺少關鍵零件，只剩一張寫有『回波時間包含往返』的維修便條。",
                        "/sounds/box_open.wav"));
                break;

            case 5:
                hotspots.add(Hotspot.puzzle(
                        "訪客名冊", 0.120, 0.600, 0.220, 0.280, 0.92, 1));
                hotspots.add(Hotspot.puzzle(
                        "失名者肖像", 0.205, 0.205, 0.145, 0.405, 0.72, 2));
                hotspots.add(Hotspot.puzzle(
                        "主鏡封印", 0.495, 0.245, 0.125, 0.360, 0.48, 3));
                hotspots.add(Hotspot.lore(
                        "鏡廳銀鑰", 0.805, 0.650, 0.155, 0.180, 0.92,
                        "銀鑰被紫色光暈包圍。只有主鏡封印完成後，它才會真正屬於你。",
                        "/sounds/item_select.wav"));
                hotspots.add(Hotspot.lore(
                        "倒影畫廊", 0.350, 0.200, 0.125, 0.390, 0.64,
                        "鏡中的住客來自不同年代，卻同時失去了眼睛與姓名。",
                        "/sounds/mirror_whisper.wav"));
                break;

            default:
                break;
        }

        return hotspots;
    }

    private static Color brighten(Color color, int amount) {
        return new Color(
                Math.min(255, color.getRed() + amount),
                Math.min(255, color.getGreen() + amount),
                Math.min(255, color.getBlue() + amount)
        );
    }

    private static String safeMessage(Throwable error) {
        if (error == null || error.getMessage() == null
                || error.getMessage().trim().isEmpty()) {
            return "未知錯誤";
        }
        return error.getMessage();
    }

    private static Graphics2D qualityGraphics(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return g2;
    }

    private static final class TranslucentCard extends JPanel {

        private static final long serialVersionUID = 1L;
        private final Color accent;

        TranslucentCard(Color accent) {
            this.accent = accent;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = qualityGraphics(graphics);
            g2.setComposite(AlphaComposite.SrcOver);
            g2.setColor(new Color(8, 9, 12, 205));
            g2.fillRoundRect(
                    0, 0, getWidth(), getHeight(), 24, 24);
            g2.setColor(new Color(
                    accent.getRed(),
                    accent.getGreen(),
                    accent.getBlue(),
                    205
            ));
            g2.setStroke(new BasicStroke(1.4f));
            g2.drawRoundRect(
                    1, 1,
                    Math.max(0, getWidth() - 3),
                    Math.max(0, getHeight() - 3),
                    24, 24
            );
            g2.dispose();
            super.paintComponent(graphics);
        }
    }

    private static final class Hotspot {

        private final String name;
        private final double x;
        private final double y;
        private final double width;
        private final double height;
        private final double depth;
        private final int puzzleOrder;
        private final String message;
        private final String soundPath;

        private Hotspot(
                String name,
                double x,
                double y,
                double width,
                double height,
                double depth,
                int puzzleOrder,
                String message,
                String soundPath) {

            this.name = name;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.depth = depth;
            this.puzzleOrder = puzzleOrder;
            this.message = message;
            this.soundPath = soundPath;
        }

        static Hotspot puzzle(
                String name,
                double x,
                double y,
                double width,
                double height,
                double depth,
                int puzzleOrder) {

            return new Hotspot(
                    name, x, y, width, height, depth,
                    puzzleOrder, "", null);
        }

        static Hotspot lore(
                String name,
                double x,
                double y,
                double width,
                double height,
                double depth,
                String message,
                String soundPath) {

            return new Hotspot(
                    name, x, y, width, height, depth,
                    0, message, soundPath);
        }

        double centerX() {
            return x + width / 2.0;
        }

        double centerY() {
            return y + height / 2.0;
        }
    }

    private static final class CinematicScenePanel extends JLayeredPane {

        private static final long serialVersionUID = 1L;

        private final BufferedImage background;
        private final BufferedImage foreground;
        private final Color accent;
        private final List<HotspotSpec> hotspots = new ArrayList<>();
        private final List<Particle> particles = new ArrayList<>();
        private final ForegroundOverlay foregroundOverlay;
        private final Timer timer;

        private double cameraX;
        private double cameraY;
        private double targetCameraX;
        private double targetCameraY;
        private double phase;

        CinematicScenePanel(
                BufferedImage background,
                BufferedImage foreground,
                Color accent) {

            this.background = background;
            this.foreground = foreground;
            this.accent = accent;

            setLayout(null);
            setOpaque(true);
            setBackground(Color.BLACK);

            createParticles();

            foregroundOverlay = new ForegroundOverlay();
            add(foregroundOverlay, JLayeredPane.MODAL_LAYER);

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent event) {
                    updateCamera(event.getPoint());
                }

                @Override
                public void mouseDragged(MouseEvent event) {
                    updateCamera(event.getPoint());
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent event) {
                    targetCameraX = 0.0;
                    targetCameraY = 0.0;
                }
            });

            timer = new Timer(33, event -> animate());
            timer.start();
        }

        void addHotspot(
                String name,
                double x,
                double y,
                double width,
                double height,
                double depth,
                Runnable action) {

            HotspotButton button = new HotspotButton(
                    name, accent, action);
            hotspots.add(new HotspotSpec(
                    button, x, y, width, height, depth));
            add(button, JLayeredPane.PALETTE_LAYER);
            revalidate();
            repaint();
        }

        Rectangle getVisibleImageBounds() {
            return getImageBounds();
        }

        void stopAnimation() {
            timer.stop();
        }

        private void createParticles() {
            Random random = new Random(20260707L);
            for (int i = 0; i < 38; i++) {
                particles.add(new Particle(
                        random.nextDouble(),
                        random.nextDouble(),
                        0.6 + random.nextDouble() * 1.8,
                        random.nextDouble() * Math.PI * 2.0,
                        0.25 + random.nextDouble() * 0.85,
                        18 + random.nextInt(42)
                ));
            }
        }

        private void updateCamera(Point point) {
            if (getWidth() <= 0 || getHeight() <= 0) {
                return;
            }
            targetCameraX = clamp(
                    point.x / (double) getWidth() * 2.0 - 1.0,
                    -1.0, 1.0);
            targetCameraY = clamp(
                    point.y / (double) getHeight() * 2.0 - 1.0,
                    -1.0, 1.0);
        }

        private void animate() {
            cameraX += (targetCameraX - cameraX) * 0.070;
            cameraY += (targetCameraY - cameraY) * 0.070;
            phase += 0.045;
            for (HotspotSpec spec : hotspots) {
                spec.button.animate();
            }
            revalidate();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Rectangle base = getImageBounds();
            Rectangle draw = getCameraBounds(base, 0.018, 0.62);

            Graphics2D g2 = qualityGraphics(graphics);
            g2.drawImage(
                    background,
                    draw.x, draw.y, draw.width, draw.height,
                    null
            );

            paintAmbientParticles(g2, base);
            paintVignette(g2, base);
            g2.dispose();
        }

        private void paintAmbientParticles(
                Graphics2D g2,
                Rectangle bounds) {

            for (Particle particle : particles) {
                double x = particle.x
                        + Math.sin(phase * 0.35 + particle.offset)
                        * 0.010 * particle.speed;
                double y = particle.y
                        + Math.cos(phase * 0.22 + particle.offset)
                        * 0.006 * particle.speed;

                x -= Math.floor(x);
                y -= Math.floor(y);

                int px = bounds.x + (int) (bounds.width * x);
                int py = bounds.y + (int) (bounds.height * y);

                g2.setColor(new Color(
                        accent.getRed(),
                        accent.getGreen(),
                        accent.getBlue(),
                        particle.alpha
                ));
                g2.fill(new Ellipse2D.Double(
                        px, py, particle.size, particle.size));
            }
        }

        private void paintVignette(
                Graphics2D g2,
                Rectangle bounds) {

            for (int i = 0; i < 9; i++) {
                int insetX = (int) (bounds.width * i * 0.007);
                int insetY = (int) (bounds.height * i * 0.007);
                g2.setColor(new Color(0, 0, 0, 8 + i * 5));
                g2.setStroke(new BasicStroke(
                        Math.max(2f, bounds.width * 0.010f)));
                g2.drawRect(
                        bounds.x + insetX,
                        bounds.y + insetY,
                        Math.max(0, bounds.width - insetX * 2),
                        Math.max(0, bounds.height - insetY * 2)
                );
            }
        }

        @Override
        public void doLayout() {
            Rectangle base = getImageBounds();
            Rectangle camera = getCameraBounds(base, 0.018, 0.62);

            for (HotspotSpec spec : hotspots) {
                double localDepth = 0.15 + spec.depth * 0.85;
                int x = camera.x
                        + (int) Math.round(camera.width * spec.x)
                        + (int) Math.round(
                                cameraX * camera.width * 0.006 * localDepth);
                int y = camera.y
                        + (int) Math.round(camera.height * spec.y)
                        + (int) Math.round(
                                cameraY * camera.height * 0.006 * localDepth);
                int width = (int) Math.round(
                        camera.width * spec.width);
                int height = (int) Math.round(
                        camera.height * spec.height);

                spec.button.setBounds(x, y, width, height);
            }

            foregroundOverlay.setBounds(
                    0, 0, getWidth(), getHeight());
        }

        private Rectangle getImageBounds() {
            int panelWidth = getWidth();
            int panelHeight = getHeight();

            if (panelWidth <= 0 || panelHeight <= 0) {
                return new Rectangle();
            }

            double imageRatio = background.getWidth()
                    / (double) background.getHeight();
            double panelRatio = panelWidth / (double) panelHeight;

            int width;
            int height;
            int x;
            int y;

            if (panelRatio > imageRatio) {
                height = panelHeight;
                width = (int) Math.round(height * imageRatio);
                x = (panelWidth - width) / 2;
                y = 0;
            } else {
                width = panelWidth;
                height = (int) Math.round(width / imageRatio);
                x = 0;
                y = (panelHeight - height) / 2;
            }

            return new Rectangle(x, y, width, height);
        }

        private Rectangle getCameraBounds(
                Rectangle base,
                double overscan,
                double strength) {

            int extraW = (int) Math.round(base.width * overscan);
            int extraH = (int) Math.round(base.height * overscan);
            int shiftX = (int) Math.round(
                    -cameraX * extraW * strength);
            int shiftY = (int) Math.round(
                    -cameraY * extraH * strength);

            return new Rectangle(
                    base.x - extraW + shiftX,
                    base.y - extraH + shiftY,
                    base.width + extraW * 2,
                    base.height + extraH * 2
            );
        }

        private static double clamp(
                double value,
                double minimum,
                double maximum) {

            return Math.max(minimum, Math.min(maximum, value));
        }

        private final class ForegroundOverlay extends JComponent {

            private static final long serialVersionUID = 1L;

            @Override
            public boolean contains(int x, int y) {
                return false;
            }

            @Override
            protected void paintComponent(Graphics graphics) {
                if (foreground == null) {
                    return;
                }

                Rectangle base = getImageBounds();
                Rectangle draw = getCameraBounds(base, 0.010, 1.18);

                Graphics2D g2 = qualityGraphics(graphics);
                g2.setComposite(
                        AlphaComposite.SrcOver.derive(0.94f));
                g2.drawImage(
                        foreground,
                        draw.x, draw.y, draw.width, draw.height,
                        null
                );
                g2.dispose();
            }
        }
    }

    private static final class HotspotButton extends JButton {

        private static final long serialVersionUID = 1L;

        private final Color accent;
        private boolean hovered;
        private double hover;
        private double click;
        private double phase;

        HotspotButton(
                String name,
                Color accent,
                Runnable action) {

            this.accent = accent;

            setUI(new BasicButtonUI());
            setName(name);
            setToolTipText(name);
            getAccessibleContext().setAccessibleName(name);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setFocusable(false);
            setCursor(Cursor.getPredefinedCursor(
                    Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent event) {
                    hovered = true;
                }

                @Override
                public void mouseExited(MouseEvent event) {
                    hovered = false;
                }

                @Override
                public void mousePressed(MouseEvent event) {
                    click = 1.0;
                }
            });

            addActionListener(event -> action.run());
        }

        void animate() {
            hover += ((hovered ? 1.0 : 0.0) - hover) * 0.16;
            click = Math.max(0.0, click - 0.075);
            phase += 0.10;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            if (hover < 0.01 && click < 0.01) {
                return;
            }

            Graphics2D g2 = qualityGraphics(graphics);
            int arc = Math.max(12,
                    Math.min(getWidth(), getHeight()) / 3);

            if (hover > 0.01) {
                int fillAlpha = (int) (28 * hover);
                int lineAlpha = (int) (175 * hover);
                double breath = 0.5 + Math.sin(phase) * 0.5;

                g2.setColor(new Color(
                        accent.getRed(),
                        accent.getGreen(),
                        accent.getBlue(),
                        fillAlpha
                ));
                g2.fill(new RoundRectangle2D.Double(
                        1, 1,
                        Math.max(0, getWidth() - 3),
                        Math.max(0, getHeight() - 3),
                        arc, arc
                ));

                g2.setColor(new Color(
                        accent.getRed(),
                        accent.getGreen(),
                        accent.getBlue(),
                        lineAlpha
                ));
                g2.setStroke(new BasicStroke(
                        1.5f + (float) breath * 1.2f));
                g2.draw(new RoundRectangle2D.Double(
                        2, 2,
                        Math.max(0, getWidth() - 5),
                        Math.max(0, getHeight() - 5),
                        arc, arc
                ));
            }

            if (click > 0.01) {
                double diameter = Math.max(getWidth(), getHeight())
                        * (0.25 + (1.0 - click) * 1.05);
                g2.setColor(new Color(
                        accent.getRed(),
                        accent.getGreen(),
                        accent.getBlue(),
                        (int) (145 * click)
                ));
                g2.setStroke(new BasicStroke(2.0f));
                g2.draw(new Ellipse2D.Double(
                        getWidth() / 2.0 - diameter / 2.0,
                        getHeight() / 2.0 - diameter / 2.0,
                        diameter,
                        diameter
                ));
            }

            g2.dispose();
        }
    }

    private static final class HotspotSpec {

        private final HotspotButton button;
        private final double x;
        private final double y;
        private final double width;
        private final double height;
        private final double depth;

        HotspotSpec(
                HotspotButton button,
                double x,
                double y,
                double width,
                double height,
                double depth) {

            this.button = button;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.depth = depth;
        }
    }

    private static final class Particle {

        private final double x;
        private final double y;
        private final double size;
        private final double offset;
        private final double speed;
        private final int alpha;

        Particle(
                double x,
                double y,
                double size,
                double offset,
                double speed,
                int alpha) {

            this.x = x;
            this.y = y;
            this.size = size;
            this.offset = offset;
            this.speed = speed;
            this.alpha = alpha;
        }
    }
}
