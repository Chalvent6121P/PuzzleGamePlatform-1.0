package controller.games;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
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
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;

import controller.games.common.BackpackDialog;
import entity.Item;
import service.impl.engine.LibraryGameEngine;
import util.SoundPlayer;

/**
 * 《失落的圖書館》視覺型 2.5D 版本。
 *
 * <p>保留原本的 LibraryGameEngine、MySQL 遊戲紀錄、答題流程與返回大廳流程，
 * 僅升級 Swing 場景呈現：</p>
 *
 * <ul>
 *   <li>滑鼠視差攝影機</li>
 *   <li>依物件深度移動的互動熱區</li>
 *   <li>動態暖光、暗角、霧塵與地板透視線</li>
 *   <li>物件懸停光暈、呼吸動畫與點擊波紋</li>
 *   <li>可選用透明 PNG 前景層</li>
 * </ul>
 *
 * <p>Java 11 相容，不增加 Maven 相依套件。</p>
 */
public class LibraryGamePage extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final String BACKGROUND_PATH =
            "/images/library/library_scene.png";

    /**
     * 選用的透明 PNG 前景。
     * 沒有此檔案也可以正常執行。
     */
    private static final String FOREGROUND_PATH =
            "/images/library/layers/library_foreground.png";

    /**
     * 開發時可改為 true，顯示熱區紅框。
     */
    private static final boolean SHOW_HOTSPOT_BORDERS = false;

    private final LibraryGameEngine engine = new LibraryGameEngine();

    private final int playerNo;
    private final int recordNo;
    private int currentPuzzleNo;

    private ScenePanel scenePanel;

    private JTextArea messageArea;
    private JTextField inputField;
    private JButton submitButton;

    private final Runnable returnToLobbyAction;
    private boolean leavingPage;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                int testPlayerNo = 1;
                LibraryGamePage frame =
                        new LibraryGamePage(testPlayerNo);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public LibraryGamePage(int playerNo) {
        this(playerNo, null);
    }

    public LibraryGamePage(
            int playerNo,
            Runnable returnToLobbyAction) {

        if (playerNo <= 0) {
            throw new IllegalArgumentException(
                    "playerNo 必須大於 0。");
        }

        this.playerNo = playerNo;
        this.returnToLobbyAction = returnToLobbyAction;
        this.currentPuzzleNo = 0;
        this.leavingPage = false;

        this.recordNo = engine.startLibraryGame(playerNo);

        if (recordNo <= 0) {
            throw new IllegalStateException(
                    "建立遊戲紀錄失敗，無法取得 recordNo。");
        }

        initFrame();
        initComponents();
        registerHotspots();
    }

    private void initFrame() {
        setTitle("失落的圖書館 - 2.5D");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                handleWindowClosing();
            }

            @Override
            public void windowClosed(WindowEvent event) {
                if (scenePanel != null) {
                    scenePanel.stopAnimation();
                }
            }
        });
    }

    private void initComponents() {
        BufferedImage backgroundImage =
                loadRequiredImage(BACKGROUND_PATH);

        BufferedImage foregroundImage =
                loadOptionalImage(FOREGROUND_PATH);

        scenePanel = new ScenePanel(
                backgroundImage,
                foregroundImage
        );

        setContentPane(scenePanel);

        scenePanel.setHudPanel(createHudPanel());
        scenePanel.setTitlePanel(createTitlePanel());
        scenePanel.setControlPanel(createControlPanel());

        showMessage(
                "你被困在圖書館裡。\n"
              + "移動滑鼠觀察空間深度，點擊房間中的物品尋找線索。"
        );
    }

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout(4, 1)) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = createQualityGraphics(g);

                g2.setColor(new Color(8, 7, 8, 182));
                g2.fillRoundRect(
                        0, 0, getWidth(), getHeight(), 18, 18);

                g2.setColor(new Color(214, 174, 96, 165));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(
                        1, 1,
                        Math.max(0, getWidth() - 3),
                        Math.max(0, getHeight() - 3),
                        18, 18
                );

                g2.dispose();
                super.paintComponent(g);
            }
        };

        titlePanel.setOpaque(false);
        titlePanel.setBorder(new EmptyBorder(8, 14, 8, 14));

        JTextArea title = new JTextArea(
                "失落的圖書館\n"
              + "THE LOST LIBRARY");
        title.setEditable(false);
        title.setFocusable(false);
        title.setOpaque(false);
        title.setForeground(new Color(244, 220, 167));
        title.setFont(new Font(
                "Microsoft JhengHei", Font.BOLD, 15));

        titlePanel.add(title, BorderLayout.CENTER);
        return titlePanel;
    }

    private JPanel createHudPanel() {
        JPanel hudPanel = new JPanel(new BorderLayout(8, 8)) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = createQualityGraphics(g);

                GradientPaint gradient = new GradientPaint(
                        0, 0,
                        new Color(18, 14, 13, 225),
                        0, getHeight(),
                        new Color(7, 8, 12, 218)
                );

                g2.setPaint(gradient);
                g2.fillRoundRect(
                        0, 0, getWidth(), getHeight(), 24, 24);

                g2.setColor(new Color(226, 186, 103, 150));
                g2.setStroke(new BasicStroke(1.3f));
                g2.drawRoundRect(
                        1, 1,
                        Math.max(0, getWidth() - 3),
                        Math.max(0, getHeight() - 3),
                        24, 24
                );

                g2.dispose();
                super.paintComponent(g);
            }
        };

        hudPanel.setOpaque(false);
        hudPanel.setBorder(new EmptyBorder(12, 14, 12, 14));

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setFocusable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setOpaque(false);
        messageArea.setForeground(new Color(244, 226, 183));
        messageArea.setFont(new Font(
                "Microsoft JhengHei", Font.PLAIN, 16));

        JScrollPane messageScrollPane =
                new JScrollPane(messageArea);
        messageScrollPane.setBorder(null);
        messageScrollPane.setOpaque(false);
        messageScrollPane.getViewport().setOpaque(false);

        JPanel inputPanel = new JPanel(new BorderLayout(8, 0));
        inputPanel.setOpaque(false);

        inputField = new JTextField();
        inputField.setFont(new Font(
                "Microsoft JhengHei", Font.PLAIN, 16));
        inputField.setEnabled(false);
        inputField.addActionListener(e -> handleSubmit());

        submitButton = createGoldButton("輸入答案");
        submitButton.setEnabled(false);
        submitButton.addActionListener(e -> handleSubmit());

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(submitButton, BorderLayout.EAST);

        hudPanel.add(messageScrollPane, BorderLayout.CENTER);
        hudPanel.add(inputPanel, BorderLayout.SOUTH);

        return hudPanel;
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(
                FlowLayout.RIGHT, 10, 0));
        controlPanel.setOpaque(false);

        JButton backpackButton = createSceneControlButton("背包");
        backpackButton.setToolTipText("開啟背包");
        backpackButton.addActionListener(event -> openBackpack());

        JButton returnButton = createSceneControlButton("返回大廳");
        returnButton.setToolTipText("離開遊戲並返回大廳");
        returnButton.addActionListener(event -> handleWindowClosing());

        controlPanel.add(backpackButton);
        controlPanel.add(returnButton);
        return controlPanel;
    }

    private JButton createSceneControlButton(String text) {
        JButton button = new JButton(text) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g2 = createQualityGraphics(graphics);
                g2.setColor(new Color(8, 7, 8, 210));
                g2.fillRoundRect(
                        0, 0, getWidth(), getHeight(), 22, 22);
                g2.setColor(new Color(226, 186, 103, 205));
                g2.setStroke(new BasicStroke(1.4f));
                g2.drawRoundRect(
                        1, 1,
                        Math.max(0, getWidth() - 3),
                        Math.max(0, getHeight() - 3),
                        22, 22);
                g2.dispose();
                super.paintComponent(graphics);
            }
        };

        button.setUI(new BasicButtonUI());
        button.setFont(new Font(
                "Microsoft JhengHei", Font.BOLD, 14));
        button.setForeground(new Color(244, 226, 183));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(
                Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(
                "返回大廳".equals(text) ? 112 : 82, 44));
        return button;
    }

    private void openBackpack() {
        try {
            SoundPlayer.play("/sounds/bag_open.wav");
            List<Item> items = engine.getInventoryItems(recordNo);
            BackpackDialog dialog = new BackpackDialog(
                    this, items, new Color(211, 172, 91));
            dialog.setVisible(true);
        } catch (RuntimeException exception) {
            showMessage(
                    "背包讀取失敗："
                  + safeMessage(exception));
        }
    }

    private static String safeMessage(Throwable error) {
        if (error == null || error.getMessage() == null
                || error.getMessage().trim().isEmpty()) {
            return "未知錯誤";
        }
        return error.getMessage();
    }

    private JButton createGoldButton(String text) {
        JButton button = new JButton(text);
        button.setUI(new BasicButtonUI());
        button.setFont(new Font(
                "Microsoft JhengHei", Font.BOLD, 14));
        button.setForeground(new Color(25, 20, 15));
        button.setBackground(new Color(211, 172, 91));
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(247, 214, 143), 1),
                new EmptyBorder(7, 12, 7, 12)
        ));
        button.setCursor(Cursor.getPredefinedCursor(
                Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * depth：0.0 代表較遠；1.0 代表較靠近玩家。
     */
    private void registerHotspots() {
        scenePanel.addHotspot(
                "泛黃便條紙",
                0.105, 0.750, 0.245, 0.175,
                0.95,
                this::handleClueNote
        );

        scenePanel.addHotspot(
                "藍色百科全書",
                0.197, 0.205, 0.035, 0.110,
                0.30,
                this::handleEncyclopedia
        );

        scenePanel.addHotspot(
                "西方史綱",
                0.030, 0.215, 0.030, 0.100,
                0.28,
                () -> showBookName("《西方史綱》")
        );

        scenePanel.addHotspot(
                "古代王朝",
                0.075, 0.215, 0.030, 0.100,
                0.28,
                () -> showBookName("《古代王朝》")
        );

        scenePanel.addHotspot(
                "地理誌",
                0.250, 0.215, 0.030, 0.100,
                0.28,
                () -> showBookName("《世界地理誌》")
        );

        scenePanel.addHotspot(
                "星象手冊",
                0.340, 0.215, 0.030, 0.100,
                0.28,
                () -> showBookName("《星象觀測手冊》")
        );

        scenePanel.addHotspot(
                "植物圖鑑",
                0.025, 0.355, 0.032, 0.105,
                0.38,
                () -> showBookName("《古典植物圖鑑》")
        );

        scenePanel.addHotspot(
                "哲學論集",
                0.350, 0.355, 0.032, 0.105,
                0.38,
                () -> showBookName("《沉思與哲學論集》")
        );

        scenePanel.addHotspot(
                "古董時鐘",
                0.475, 0.045, 0.125, 0.545,
                0.42,
                this::handleClock
        );

        scenePanel.addHotspot(
                "地球儀",
                0.655, 0.285, 0.150, 0.240,
                0.58,
                this::handleGlobe
        );

        scenePanel.addHotspot(
                "抽屜",
                0.625, 0.500, 0.185, 0.235,
                0.78,
                this::handleDrawer
        );

        scenePanel.addHotspot(
                "密碼盒",
                0.105, 0.625, 0.140, 0.155,
                0.88,
                this::handlePasswordBox
        );

        scenePanel.addHotspot(
                "出口門",
                0.855, 0.045, 0.140, 0.710,
                0.45,
                this::handleExitDoor
        );
    }

    private void handleClueNote() {
        engine.clickObject(recordNo, "bookShelf");

        showMessage(
                "泛黃便條紙：\n"
              + "藍色是沉著冷靜的顏色，"
              + "作為書皮能讓人沉浸在知識之中。"
        );
    }

    private void handleEncyclopedia() {
        activatePuzzleInput(
                1,
                "你取出了藍色百科全書。\n"
              + "書中的內容似乎指向某個頁碼，請輸入頁碼。"
        );
    }

    private void handleClock() {
        showMessage(engine.clickObject(recordNo, "clock"));
    }

    private void handleGlobe() {
        showMessage(engine.clickObject(recordNo, "globe"));
    }

    private void handleDrawer() {
        showMessage(engine.clickObject(recordNo, "drawer"));
    }

    private void handlePasswordBox() {
        activatePuzzleInput(
                2,
                "密碼盒需要四位數密碼。\n"
              + "請輸入你找到的密碼。"
        );
    }

    private void handleExitDoor() {
        String result =
                engine.clickObject(recordNo, "exitDoor");

        if ("CLEAR".equals(result)) {
            JOptionPane.showMessageDialog(
                    this,
                    "你成功逃出失落的圖書館！",
                    "通關成功",
                    JOptionPane.INFORMATION_MESSAGE
            );

            returnToLobby();
            return;
        }

        showMessage(result);
    }

    private void handleWindowClosing() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "確定要離開失落的圖書館並返回遊戲大廳嗎？\n"
              + "本次尚未完成的遊戲紀錄會保留。",
                "返回遊戲大廳",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            returnToLobby();
        }
    }

    private void returnToLobby() {
        if (leavingPage) {
            return;
        }

        leavingPage = true;

        if (scenePanel != null) {
            scenePanel.stopAnimation();
        }

        dispose();

        if (returnToLobbyAction != null) {
            EventQueue.invokeLater(returnToLobbyAction);
        }
    }

    private void showBookName(String bookName) {
        showMessage(bookName);
    }

    private void handleSubmit() {
        if (currentPuzzleNo == 0) {
            showMessage("目前沒有需要輸入答案的謎題。");
            return;
        }

        String input = inputField.getText().trim();

        if (input.isEmpty()) {
            showMessage("請先輸入答案。");
            inputField.requestFocusInWindow();
            return;
        }

        String result = engine.submitAnswer(
                recordNo,
                currentPuzzleNo,
                input
        );

        showMessage(result);

        if (!result.contains("錯誤")
                && !result.contains("沒有這個謎題")) {

            deactivatePuzzleInput();
        } else {
            inputField.selectAll();
            inputField.requestFocusInWindow();
        }
    }

    private void activatePuzzleInput(
            int puzzleNo,
            String message) {

        currentPuzzleNo = puzzleNo;

        inputField.setEnabled(true);
        submitButton.setEnabled(true);
        inputField.setText("");
        inputField.requestFocusInWindow();

        showMessage(message);
    }

    private void deactivatePuzzleInput() {
        currentPuzzleNo = 0;

        inputField.setText("");
        inputField.setEnabled(false);
        submitButton.setEnabled(false);
    }

    private void showMessage(String message) {
        messageArea.setText(message);
        messageArea.setCaretPosition(0);

        if (scenePanel != null) {
            scenePanel.pulseHud();
        }
    }

    private BufferedImage loadRequiredImage(String path) {
        BufferedImage image = loadImage(path);

        if (image == null) {
            throw new IllegalStateException(
                    "找不到或無法讀取背景圖片：" + path
                  + "\n請確認圖片位於 "
                  + "src/main/resources/images/library/"
            );
        }

        return image;
    }

    private BufferedImage loadOptionalImage(String path) {
        return loadImage(path);
    }

    private BufferedImage loadImage(String path) {
        try (InputStream inputStream =
                     getClass().getResourceAsStream(path)) {

            if (inputStream == null) {
                return null;
            }

            return ImageIO.read(inputStream);

        } catch (IOException e) {
            return null;
        }
    }

    private static Graphics2D createQualityGraphics(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

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

    /**
     * 2.5D 場景面板。
     */
    private static final class ScenePanel extends JLayeredPane {

        private static final long serialVersionUID = 1L;

        private final BufferedImage backgroundImage;
        private final BufferedImage foregroundImage;
        private final List<HotspotSpec> hotspots =
                new ArrayList<>();
        private final List<DustParticle> dustParticles =
                new ArrayList<>();

        private final Timer animationTimer;
        private final ForegroundOverlay foregroundOverlay;

        private JPanel hudPanel;
        private JPanel titlePanel;
        private JPanel controlPanel;

        private double targetCameraX;
        private double targetCameraY;
        private double cameraX;
        private double cameraY;
        private double lightPhase;
        private double hudPulse;

        ScenePanel(
                BufferedImage backgroundImage,
                BufferedImage foregroundImage) {

            this.backgroundImage = backgroundImage;
            this.foregroundImage = foregroundImage;

            setLayout(null);
            setOpaque(true);
            setBackground(Color.BLACK);

            createDustParticles();

            foregroundOverlay = new ForegroundOverlay();
            add(foregroundOverlay, JLayeredPane.MODAL_LAYER);

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent event) {
                    updateCameraTarget(event.getPoint());
                }

                @Override
                public void mouseDragged(MouseEvent event) {
                    updateCameraTarget(event.getPoint());
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent event) {
                    targetCameraX = 0.0;
                    targetCameraY = 0.0;
                }
            });

            animationTimer = new Timer(33, event -> animateScene());
            animationTimer.start();
        }

        void setHudPanel(JPanel hudPanel) {
            this.hudPanel = hudPanel;
            add(hudPanel, JLayeredPane.POPUP_LAYER);
        }

        void setTitlePanel(JPanel titlePanel) {
            this.titlePanel = titlePanel;
            add(titlePanel, JLayeredPane.POPUP_LAYER);
        }

        void setControlPanel(JPanel controlPanel) {
            this.controlPanel = controlPanel;
            add(controlPanel, JLayeredPane.POPUP_LAYER);
        }

        void addHotspot(
                String accessibleName,
                double x,
                double y,
                double width,
                double height,
                double depth,
                Runnable action) {

            HotspotButton button =
                    new HotspotButton(accessibleName, action);

            HotspotSpec spec = new HotspotSpec(
                    button,
                    x, y, width, height,
                    clamp(depth, 0.0, 1.0)
            );

            hotspots.add(spec);
            add(button, JLayeredPane.PALETTE_LAYER);

            revalidate();
            repaint();
        }

        void pulseHud() {
            hudPulse = 1.0;
        }

        void stopAnimation() {
            animationTimer.stop();
        }

        private void updateCameraTarget(Point point) {
            if (getWidth() <= 0 || getHeight() <= 0) {
                return;
            }

            targetCameraX =
                    clamp(
                            (point.x / (double) getWidth() - 0.5) * 2.0,
                            -1.0,
                            1.0
                    );

            targetCameraY =
                    clamp(
                            (point.y / (double) getHeight() - 0.5) * 2.0,
                            -1.0,
                            1.0
                    );
        }

        private void animateScene() {
            cameraX += (targetCameraX - cameraX) * 0.075;
            cameraY += (targetCameraY - cameraY) * 0.075;
            lightPhase += 0.045;
            hudPulse = Math.max(0.0, hudPulse - 0.055);

            for (HotspotSpec hotspot : hotspots) {
                hotspot.button.advanceAnimation();
            }

            revalidate();
            repaint();
        }

        private void createDustParticles() {
            Random random = new Random(2507L);

            for (int i = 0; i < 42; i++) {
                dustParticles.add(new DustParticle(
                        random.nextDouble(),
                        random.nextDouble(),
                        0.8 + random.nextDouble() * 2.1,
                        random.nextDouble() * Math.PI * 2.0,
                        0.25 + random.nextDouble() * 0.75
                ));
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Rectangle imageBounds = getImageBounds();

            if (imageBounds.width <= 0
                    || imageBounds.height <= 0) {
                return;
            }

            Graphics2D g2 = createQualityGraphics(g);

            Rectangle cameraBounds =
                    getCameraImageBounds(imageBounds, 0.028);

            g2.drawImage(
                    backgroundImage,
                    cameraBounds.x,
                    cameraBounds.y,
                    cameraBounds.width,
                    cameraBounds.height,
                    null
            );

            paintDepthWash(g2, imageBounds);
            paintPerspectiveFloor(g2, imageBounds);
            paintLightPools(g2, imageBounds);
            paintDust(g2, imageBounds);
            paintVignette(g2, imageBounds);

            g2.dispose();
        }

        private void paintDepthWash(
                Graphics2D g2,
                Rectangle imageBounds) {

            GradientPaint verticalShade = new GradientPaint(
                    imageBounds.x,
                    imageBounds.y,
                    new Color(9, 13, 24, 92),
                    imageBounds.x,
                    imageBounds.y + imageBounds.height,
                    new Color(83, 48, 24, 18)
            );

            g2.setPaint(verticalShade);
            g2.fillRect(
                    imageBounds.x,
                    imageBounds.y,
                    imageBounds.width,
                    imageBounds.height
            );
        }

        private void paintPerspectiveFloor(
                Graphics2D g2,
                Rectangle imageBounds) {

            int vanishX = imageBounds.x
                    + (int) Math.round(imageBounds.width * 0.56);
            int vanishY = imageBounds.y
                    + (int) Math.round(imageBounds.height * 0.53);
            int bottomY = imageBounds.y + imageBounds.height;

            g2.setStroke(new BasicStroke(1.0f));
            g2.setColor(new Color(220, 176, 103, 24));

            for (int i = -7; i <= 7; i++) {
                int bottomX = vanishX
                        + (int) Math.round(
                                i * imageBounds.width * 0.105);

                g2.drawLine(
                        vanishX,
                        vanishY,
                        bottomX,
                        bottomY
                );
            }

            for (int i = 1; i <= 7; i++) {
                double t = i / 7.0;
                double curved = t * t;
                int y = vanishY
                        + (int) Math.round(
                                (bottomY - vanishY) * curved);

                int halfWidth = (int) Math.round(
                        imageBounds.width * 0.60 * curved);

                g2.drawLine(
                        vanishX - halfWidth,
                        y,
                        vanishX + halfWidth,
                        y
                );
            }
        }

        private void paintLightPools(
                Graphics2D g2,
                Rectangle imageBounds) {

            double pulse =
                    0.80 + Math.sin(lightPhase) * 0.10;

            drawSoftLight(
                    g2,
                    imageBounds,
                    0.535, 0.260,
                    0.190 * pulse,
                    new Color(255, 196, 92),
                    42
            );

            drawSoftLight(
                    g2,
                    imageBounds,
                    0.720, 0.435,
                    0.135,
                    new Color(196, 154, 82),
                    28
            );

            drawSoftLight(
                    g2,
                    imageBounds,
                    0.180, 0.760,
                    0.150,
                    new Color(240, 183, 104),
                    22
            );
        }

        private void drawSoftLight(
                Graphics2D g2,
                Rectangle imageBounds,
                double normalizedX,
                double normalizedY,
                double normalizedRadius,
                Color color,
                int maxAlpha) {

            int centerX = imageBounds.x
                    + (int) Math.round(
                            imageBounds.width * normalizedX);
            int centerY = imageBounds.y
                    + (int) Math.round(
                            imageBounds.height * normalizedY);
            int radius = (int) Math.round(
                    imageBounds.width * normalizedRadius);

            int rings = 12;

            for (int i = rings; i >= 1; i--) {
                double ratio = i / (double) rings;
                int currentRadius =
                        (int) Math.round(radius * ratio);
                int alpha =
                        (int) Math.round(
                                maxAlpha * (1.0 - ratio) + 2);

                g2.setColor(new Color(
                        color.getRed(),
                        color.getGreen(),
                        color.getBlue(),
                        Math.min(maxAlpha, alpha)
                ));

                g2.fill(new Ellipse2D.Double(
                        centerX - currentRadius,
                        centerY - currentRadius,
                        currentRadius * 2.0,
                        currentRadius * 2.0
                ));
            }
        }

        private void paintDust(
                Graphics2D g2,
                Rectangle imageBounds) {

            for (DustParticle particle : dustParticles) {
                double driftX =
                        Math.sin(
                                lightPhase * 0.35
                              + particle.phase
                        ) * 0.012 * particle.speed;

                double driftY =
                        Math.cos(
                                lightPhase * 0.22
                              + particle.phase
                        ) * 0.008 * particle.speed;

                double x = particle.x + driftX;
                double y = particle.y + driftY;

                x = x - Math.floor(x);
                y = y - Math.floor(y);

                int px = imageBounds.x
                        + (int) Math.round(
                                imageBounds.width * x);
                int py = imageBounds.y
                        + (int) Math.round(
                                imageBounds.height * y);

                int alpha = (int) Math.round(
                        18 + 35 * particle.opacity);

                g2.setColor(new Color(
                        255, 230, 177, alpha));

                double size = particle.size;

                g2.fill(new Ellipse2D.Double(
                        px, py, size, size));
            }
        }

        private void paintVignette(
                Graphics2D g2,
                Rectangle imageBounds) {

            int steps = 12;

            for (int i = 0; i < steps; i++) {
                int insetX = (int) Math.round(
                        imageBounds.width * i * 0.009);
                int insetY = (int) Math.round(
                        imageBounds.height * i * 0.009);

                int alpha = 10 + i * 5;

                g2.setColor(new Color(0, 0, 0, alpha));
                g2.setStroke(new BasicStroke(
                        Math.max(2f,
                                imageBounds.width * 0.012f)));

                g2.drawRect(
                        imageBounds.x + insetX,
                        imageBounds.y + insetY,
                        Math.max(0,
                                imageBounds.width - insetX * 2),
                        Math.max(0,
                                imageBounds.height - insetY * 2)
                );
            }
        }

        @Override
        public void doLayout() {
            Rectangle imageBounds = getImageBounds();

            for (HotspotSpec hotspot : hotspots) {
                double parallaxStrength =
                        0.004 + hotspot.depth * 0.017;

                int parallaxX = (int) Math.round(
                        cameraX
                      * imageBounds.width
                      * parallaxStrength
                );

                int parallaxY = (int) Math.round(
                        cameraY
                      * imageBounds.height
                      * parallaxStrength
                );

                double breathing =
                        1.0
                      + hotspot.button.getBreathingScale()
                      * (0.004 + hotspot.depth * 0.008);

                int width = (int) Math.round(
                        imageBounds.width
                      * hotspot.width
                      * breathing
                );

                int height = (int) Math.round(
                        imageBounds.height
                      * hotspot.height
                      * breathing
                );

                int centerX = imageBounds.x
                        + (int) Math.round(
                                imageBounds.width
                              * (hotspot.x + hotspot.width / 2.0))
                        + parallaxX;

                int centerY = imageBounds.y
                        + (int) Math.round(
                                imageBounds.height
                              * (hotspot.y + hotspot.height / 2.0))
                        + parallaxY;

                hotspot.button.setBounds(
                        centerX - width / 2,
                        centerY - height / 2,
                        width,
                        height
                );
            }

            if (hudPanel != null) {
                int hudX = imageBounds.x
                        + (int) Math.round(
                                imageBounds.width * 0.575);
                int hudY = imageBounds.y
                        + (int) Math.round(
                                imageBounds.height * 0.765);
                int hudWidth = (int) Math.round(
                        imageBounds.width * 0.405);
                int hudHeight = (int) Math.round(
                        imageBounds.height * 0.210);

                int pulseOffset =
                        (int) Math.round(hudPulse * 4.0);

                hudPanel.setBounds(
                        hudX - pulseOffset,
                        hudY - pulseOffset,
                        hudWidth + pulseOffset * 2,
                        hudHeight + pulseOffset * 2
                );
            }

            if (titlePanel != null) {
                int titleX = imageBounds.x
                        + (int) Math.round(
                                imageBounds.width * 0.025);
                int titleY = imageBounds.y
                        + (int) Math.round(
                                imageBounds.height * 0.025);
                int titleWidth = (int) Math.round(
                        imageBounds.width * 0.245);
                int titleHeight = (int) Math.round(
                        imageBounds.height * 0.090);

                titlePanel.setBounds(
                        titleX,
                        titleY,
                        titleWidth,
                        titleHeight
                );
            }

            if (controlPanel != null) {
                int controlWidth = (int) Math.round(
                        imageBounds.width * 0.205);
                int controlHeight = (int) Math.round(
                        imageBounds.height * 0.075);
                int controlX = imageBounds.x
                        + imageBounds.width
                        - controlWidth
                        - (int) Math.round(
                                imageBounds.width * 0.025);
                int controlY = imageBounds.y
                        + (int) Math.round(
                                imageBounds.height * 0.025);

                controlPanel.setBounds(
                        controlX,
                        controlY,
                        controlWidth,
                        controlHeight
                );
            }

            foregroundOverlay.setBounds(
                    0, 0, getWidth(), getHeight());
        }

        private Rectangle getCameraImageBounds(
                Rectangle imageBounds,
                double overscan) {

            int extraWidth = (int) Math.round(
                    imageBounds.width * overscan);
            int extraHeight = (int) Math.round(
                    imageBounds.height * overscan);

            int shiftX = (int) Math.round(
                    -cameraX * extraWidth * 0.60);
            int shiftY = (int) Math.round(
                    -cameraY * extraHeight * 0.60);

            return new Rectangle(
                    imageBounds.x - extraWidth + shiftX,
                    imageBounds.y - extraHeight + shiftY,
                    imageBounds.width + extraWidth * 2,
                    imageBounds.height + extraHeight * 2
            );
        }

        private Rectangle getImageBounds() {
            int panelWidth = getWidth();
            int panelHeight = getHeight();

            if (panelWidth <= 0 || panelHeight <= 0) {
                return new Rectangle();
            }

            double imageRatio =
                    backgroundImage.getWidth()
                    / (double) backgroundImage.getHeight();

            double panelRatio =
                    panelWidth / (double) panelHeight;

            int drawWidth;
            int drawHeight;
            int drawX;
            int drawY;

            if (panelRatio > imageRatio) {
                drawHeight = panelHeight;
                drawWidth = (int) Math.round(
                        drawHeight * imageRatio);
                drawX = (panelWidth - drawWidth) / 2;
                drawY = 0;
            } else {
                drawWidth = panelWidth;
                drawHeight = (int) Math.round(
                        drawWidth / imageRatio);
                drawX = 0;
                drawY = (panelHeight - drawHeight) / 2;
            }

            return new Rectangle(
                    drawX, drawY, drawWidth, drawHeight);
        }

        private static double clamp(
                double value,
                double minimum,
                double maximum) {

            return Math.max(
                    minimum,
                    Math.min(maximum, value));
        }

        /**
         * 顯示選用透明前景圖。
         * contains() 永遠回傳 false，避免擋住熱區點擊。
         */
        private final class ForegroundOverlay
                extends JComponent {

            private static final long serialVersionUID = 1L;

            @Override
            public boolean contains(int x, int y) {
                return false;
            }

            @Override
            protected void paintComponent(Graphics g) {
                if (foregroundImage == null) {
                    return;
                }

                Rectangle imageBounds = getImageBounds();
                Rectangle cameraBounds =
                        getCameraImageBounds(imageBounds, 0.012);

                Graphics2D g2 = createQualityGraphics(g);
                g2.setComposite(
                        AlphaComposite.SrcOver.derive(0.98f));

                g2.drawImage(
                        foregroundImage,
                        cameraBounds.x,
                        cameraBounds.y,
                        cameraBounds.width,
                        cameraBounds.height,
                        null
                );

                g2.dispose();
            }
        }
    }

    /**
     * 透明互動熱區，加入懸停發光與點擊波紋。
     */
    private static final class HotspotButton extends JButton {

        private static final long serialVersionUID = 1L;

        private final String objectName;
        private final Runnable action;

        private boolean hovered;
        private double hoverProgress;
        private double clickProgress;
        private double phase;

        HotspotButton(
                String objectName,
                Runnable action) {

            this.objectName = objectName;
            this.action = action;

            setUI(new BasicButtonUI());
            setName(objectName);
            getAccessibleContext()
                    .setAccessibleName(objectName);
            setToolTipText(objectName);

            setCursor(Cursor.getPredefinedCursor(
                    Cursor.HAND_CURSOR));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setOpaque(false);
            setFocusable(false);

            if (SHOW_HOTSPOT_BORDERS) {
                setBorder(BorderFactory.createLineBorder(
                        Color.RED, 2));
                setBorderPainted(true);
            } else {
                setBorderPainted(false);
            }

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
                    clickProgress = 1.0;
                }
            });

            addActionListener(event -> action.run());
        }

        void advanceAnimation() {
            double target = hovered ? 1.0 : 0.0;
            hoverProgress +=
                    (target - hoverProgress) * 0.18;
            clickProgress =
                    Math.max(0.0, clickProgress - 0.085);
            phase += 0.075;
        }

        double getBreathingScale() {
            return hoverProgress
                    * (0.5 + Math.sin(phase) * 0.5);
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (hoverProgress <= 0.015
                    && clickProgress <= 0.015) {
                return;
            }

            Graphics2D g2 = createQualityGraphics(g);

            int arc = Math.max(
                    10,
                    Math.min(getWidth(), getHeight()) / 3);

            if (hoverProgress > 0.015) {
                int outerAlpha =
                        (int) Math.round(
                                85 * hoverProgress);
                int innerAlpha =
                        (int) Math.round(
                                38 * hoverProgress);

                g2.setColor(new Color(
                        255, 210, 112, innerAlpha));
                g2.fill(new RoundRectangle2D.Double(
                        1, 1,
                        Math.max(0, getWidth() - 3),
                        Math.max(0, getHeight() - 3),
                        arc, arc
                ));

                g2.setColor(new Color(
                        255, 225, 150, outerAlpha));
                g2.setStroke(new BasicStroke(
                        1.3f + (float) hoverProgress * 1.7f));

                g2.draw(new RoundRectangle2D.Double(
                        2, 2,
                        Math.max(0, getWidth() - 5),
                        Math.max(0, getHeight() - 5),
                        arc, arc
                ));
            }

            if (clickProgress > 0.015) {
                double inverse = 1.0 - clickProgress;
                double diameter =
                        Math.max(getWidth(), getHeight())
                      * (0.25 + inverse * 1.10);

                int alpha =
                        (int) Math.round(
                                130 * clickProgress);

                g2.setColor(new Color(
                        255, 232, 170, alpha));
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

        @Override
        public String toString() {
            return objectName;
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

    private static final class DustParticle {

        private final double x;
        private final double y;
        private final double size;
        private final double phase;
        private final double speed;
        private final double opacity;

        DustParticle(
                double x,
                double y,
                double size,
                double phase,
                double speed) {

            this.x = x;
            this.y = y;
            this.size = size;
            this.phase = phase;
            this.speed = speed;
            this.opacity = 0.45 + speed * 0.35;
        }
    }
}
