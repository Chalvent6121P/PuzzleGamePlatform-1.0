package controller.games;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import controller.games.common.BackpackDialog;
import service.impl.engine.LibraryGameEngine;
import util.SoundPlayer;

public class LibraryGamePage extends JFrame {

    private static final long serialVersionUID = 1L;

    /*
     * 圖片放置位置：
     *
     * src/main/resources/images/library/library_scene.png
     */
    private static final String BACKGROUND_PATH =
            "/images/library/library_scene.png";

    /*
     * 開發時可以改成 true。
     *
     * true：
     * 顯示所有可點擊區域的紅色邊框，方便調整位置。
     *
     * false：
     * 隱藏邊框，玩家只會看到遊戲背景圖。
     */
    private static final boolean SHOW_HOTSPOT_BORDERS = false;

    private final LibraryGameEngine engine = new LibraryGameEngine();

    private int playerNo;
    private int recordNo;
    private int currentPuzzleNo;

    private ScenePanel scenePanel;

    private JTextArea messageArea;
    private JTextField inputField;
    private JButton submitButton;
    private JButton soundButton;

    /*
     * 返回遊戲大廳時執行的動作。
     * 使用 Runnable 可避免遊戲頁直接依賴 GameMainPage 類別。
     */
    private final Runnable returnToLobbyAction;

    /*
     * 防止通關或關閉視窗時重複執行返回動作。
     */
    private boolean leavingPage;

    /**
     * 測試啟動。
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                /*
                 * 這個 playerNo 必須真的存在於 MySQL 的 player 表。
                 */
                int testPlayerNo = 1;

                LibraryGamePage frame =
                        new LibraryGamePage(testPlayerNo);

                frame.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 供單獨測試 LibraryGamePage 使用。
     */
    public LibraryGamePage(int playerNo) {
        this(playerNo, null);
    }

    /**
     * 正式從遊戲大廳進入時使用。
     *
     * @param playerNo           目前登入玩家編號
     * @param returnToLobbyAction 遊戲結束或離開時返回大廳的動作
     */
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

        /*
         * 開始新遊戲，並取得本次遊戲的 recordNo。
         */
        this.recordNo = engine.startLibraryGame(playerNo);

        if (recordNo <= 0) {
            throw new IllegalStateException(
                    "建立遊戲紀錄失敗，無法取得 recordNo。");
        }

        initFrame();
        initComponents();
        registerHotspots();
    }

    /**
     * JFrame 基本設定。
     */
    private void initFrame() {
        setTitle("失落的圖書館");

        /*
         * 不直接結束整個程式。
         * 玩家按下視窗 X 時，先詢問是否返回遊戲大廳。
         */
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        /*
         * 原始圖片是 1536 × 1024，比例為 3:2。
         * 1200 × 800 也是 3:2，因此不會明顯變形。
         */
        setSize(1200, 800);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                handleWindowClosing();
            }
        });
    }

    /**
     * 建立背景、訊息區及輸入區。
     */
    private void initComponents() {
        BufferedImage backgroundImage =
                loadBackgroundImage(BACKGROUND_PATH);

        scenePanel = new ScenePanel(backgroundImage);

        setContentPane(scenePanel);

        JPanel hudPanel = createHudPanel();

        /*
         * 將訊息區放到圖片的上層。
         */
        scenePanel.setHudPanel(hudPanel);

        showMessage("你被困在圖書館裡，請點擊房間中的物品尋找線索。");
    }

    /**
     * 建立右下角半透明操作介面。
     */
    private JPanel createHudPanel() {
        JPanel hudPanel = new JPanel(new BorderLayout(8, 8)) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                /*
                 * 半透明深色背景。
                 */
                g2.setColor(new Color(15, 10, 7, 160));

                g2.fillRoundRect(
                        0,
                        0,
                        getWidth(),
                        getHeight(),
                        22,
                        22
                );

                g2.dispose();

                super.paintComponent(g);
            }
        };

        hudPanel.setOpaque(false);
        hudPanel.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel toolPanel = new JPanel(new BorderLayout(8, 0));
        toolPanel.setOpaque(false);

        JButton backpackButton = new JButton("背包");
        backpackButton.setFont(new Font("Microsoft JhengHei", Font.BOLD, 13));
        backpackButton.addActionListener(e -> openBackpack());
        toolPanel.add(backpackButton, BorderLayout.WEST);

        soundButton = new JButton(soundButtonText());
        soundButton.setFont(new Font("Microsoft JhengHei", Font.BOLD, 13));
        soundButton.addActionListener(e -> toggleSound());
        toolPanel.add(soundButton, BorderLayout.EAST);

        hudPanel.add(toolPanel, BorderLayout.NORTH);

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setFocusable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setOpaque(false);

        messageArea.setForeground(new Color(244, 226, 183));
        messageArea.setFont(
                new Font("Microsoft JhengHei", Font.PLAIN, 16)
        );

        JScrollPane messageScrollPane =
                new JScrollPane(messageArea);

        messageScrollPane.setBorder(null);
        messageScrollPane.setOpaque(false);
        messageScrollPane.getViewport().setOpaque(false);

        JPanel inputPanel = new JPanel(new BorderLayout(8, 0));
        inputPanel.setOpaque(false);

        inputField = new JTextField();
        inputField.setFont(
                new Font("Microsoft JhengHei", Font.PLAIN, 16)
        );

        /*
         * 一開始沒有需要輸入答案的謎題。
         */
        inputField.setEnabled(false);

        /*
         * 在輸入框按 Enter，也可以送出答案。
         */
        inputField.addActionListener(e -> handleSubmit());

        submitButton = new JButton("輸入答案");
        submitButton.setFont(
                new Font("Microsoft JhengHei", Font.BOLD, 14)
        );
        submitButton.setEnabled(false);
        submitButton.addActionListener(e -> handleSubmit());

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(submitButton, BorderLayout.EAST);

        hudPanel.add(messageScrollPane, BorderLayout.CENTER);
        hudPanel.add(inputPanel, BorderLayout.SOUTH);

        return hudPanel;
    }

    /**
     * 註冊圖片中的所有可點擊熱區。
     *
     * x、y、width、height 都是比例：
     *
     * 0.20 = 圖片寬度的 20%
     *
     * 因此畫面縮放後，熱區也會跟著縮放。
     */
    private void registerHotspots() {

        /*
         * =========================================================
         * 第一個提示：桌上的泛黃便條紙
         * =========================================================
         */
        scenePanel.addHotspot(
                "泛黃便條紙",
                0.105,
                0.750,
                0.245,
                0.175,
                this::handleClueNote
        );

        /*
         * =========================================================
         * 正確書籍：書架上的藍色百科全書
         * =========================================================
         */
        scenePanel.addHotspot(
                "藍色百科全書",
                0.197,
                0.205,
                0.035,
                0.110,
                this::handleEncyclopedia
        );

        /*
         * =========================================================
         * 干擾書籍
         *
         * 點擊後只顯示書名，不提供提示。
         * =========================================================
         */
        scenePanel.addHotspot(
                "西方史綱",
                0.030,
                0.215,
                0.030,
                0.100,
                () -> showBookName("《西方史綱》")
        );

        scenePanel.addHotspot(
                "古代王朝",
                0.075,
                0.215,
                0.030,
                0.100,
                () -> showBookName("《古代王朝》")
        );

        scenePanel.addHotspot(
                "地理誌",
                0.250,
                0.215,
                0.030,
                0.100,
                () -> showBookName("《世界地理誌》")
        );

        scenePanel.addHotspot(
                "星象手冊",
                0.340,
                0.215,
                0.030,
                0.100,
                () -> showBookName("《星象觀測手冊》")
        );

        scenePanel.addHotspot(
                "植物圖鑑",
                0.025,
                0.355,
                0.032,
                0.105,
                () -> showBookName("《古典植物圖鑑》")
        );

        scenePanel.addHotspot(
                "哲學論集",
                0.350,
                0.355,
                0.032,
                0.105,
                () -> showBookName("《沉思與哲學論集》")
        );

        /*
         * =========================================================
         * 古董時鐘
         * =========================================================
         */
        scenePanel.addHotspot(
                "古董時鐘",
                0.475,
                0.045,
                0.125,
                0.545,
                this::handleClock
        );

        /*
         * =========================================================
         * 地球儀
         * =========================================================
         */
        scenePanel.addHotspot(
                "地球儀",
                0.655,
                0.285,
                0.150,
                0.240,
                this::handleGlobe
        );

        /*
         * =========================================================
         * 地球儀下方的抽屜櫃
         * =========================================================
         */
        scenePanel.addHotspot(
                "抽屜",
                0.625,
                0.500,
                0.185,
                0.235,
                this::handleDrawer
        );

        /*
         * =========================================================
         * 桌面左側密碼盒
         * =========================================================
         */
        scenePanel.addHotspot(
                "密碼盒",
                0.105,
                0.625,
                0.140,
                0.155,
                this::handlePasswordBox
        );

        /*
         * =========================================================
         * 出口門
         * =========================================================
         */
        scenePanel.addHotspot(
                "出口門",
                0.855,
                0.045,
                0.140,
                0.710,
                this::handleExitDoor
        );
    }

    /**
     * 點擊桌面泛黃便條紙。
     *
     * 目前使用 engine 的 bookShelf 動作來取得 ITEM_NOTE，
     * 但畫面顯示的是桌上的實際提示內容。
     */
    private void handleClueNote() {
        engine.clickObject(recordNo, "bookShelf");
        SoundPlayer.play("/sounds/paper_open.wav");

        showMessage(
                "泛黃便條紙：\n"
              + "藍色是沉著冷靜的顏色，"
              + "作為書皮能讓人沉浸在知識之中。"
        );
    }

    /**
     * 點擊藍色百科全書。
     */
    private void handleEncyclopedia() {
        SoundPlayer.play("/sounds/book_open.wav");
        activatePuzzleInput(
                1,
                "你取出了藍色百科全書。\n"
              + "書中的內容似乎指向某個頁碼，請輸入頁碼。"
        );
    }

    /**
     * 點擊時鐘。
     */
    private void handleClock() {
        SoundPlayer.play("/sounds/clock_chime.wav");
        String result =
                engine.clickObject(recordNo, "clock");

        showMessage(result);
    }

    /**
     * 點擊地球儀。
     */
    private void handleGlobe() {
        SoundPlayer.play("/sounds/gear_turn.wav");
        String result =
                engine.clickObject(recordNo, "globe");

        showMessage(result);
    }

    /**
     * 點擊抽屜。
     */
    private void handleDrawer() {
        SoundPlayer.play("/sounds/drawer_open.wav");
        String result =
                engine.clickObject(recordNo, "drawer");

        showMessage(result);
    }

    /**
     * 點擊密碼盒。
     */
    private void handlePasswordBox() {
        SoundPlayer.play("/sounds/box_click.wav");
        activatePuzzleInput(
                2,
                "密碼盒需要四位數密碼。\n"
              + "請輸入你找到的密碼。"
        );
    }

    /**
     * 點擊出口門。
     */
    private void handleExitDoor() {
        String result =
                engine.clickObject(recordNo, "exitDoor");

        if ("CLEAR".equals(result)) {
            SoundPlayer.play("/sounds/door_unlock.wav");
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

    /**
     * 玩家按下視窗右上角 X 時，詢問是否離開遊戲並返回大廳。
     */
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

    /**
     * 關閉目前遊戲頁，並重新顯示原本的遊戲大廳。
     */
    private void returnToLobby() {
        if (leavingPage) {
            return;
        }

        leavingPage = true;
        dispose();

        if (returnToLobbyAction != null) {
            EventQueue.invokeLater(returnToLobbyAction);
        }
    }

    /**
     * 點擊干擾書籍。
     *
     * 只顯示書名，不呼叫 Engine，也不提供提示。
     */
    private void showBookName(String bookName) {
        showMessage(bookName);
    }

    /**
     * 送出謎題答案。
     */
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

        /*
         * 答案正確時關閉輸入模式。
         *
         * 目前 Engine 回傳文字，因此先用文字判斷。
         * 之後也可以改成 GameActionResult 物件。
         */
        boolean solved = result.startsWith("你翻到第 815 頁")
                || result.startsWith("密碼盒打開了");

        if (solved) {
            SoundPlayer.play(currentPuzzleNo == 2
                    ? "/sounds/box_open.wav"
                    : "/sounds/correct_answer.wav");
            deactivatePuzzleInput();
        } else {
            SoundPlayer.play("/sounds/wrong_answer.wav");
            inputField.selectAll();
            inputField.requestFocusInWindow();
        }
    }

    /**
     * 開啟答案輸入模式。
     */
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

    /**
     * 關閉答案輸入模式。
     */
    private void deactivatePuzzleInput() {
        currentPuzzleNo = 0;

        inputField.setText("");
        inputField.setEnabled(false);
        submitButton.setEnabled(false);
    }

    /**
     * 更新訊息區。
     */
    private void showMessage(String message) {
        messageArea.setText(message);
        messageArea.setCaretPosition(0);
    }

    private void openBackpack() {
        try {
            SoundPlayer.play("/sounds/bag_open.wav");
            BackpackDialog dialog = new BackpackDialog(
                    this,
                    engine.getInventoryItems(recordNo),
                    new Color(190, 150, 88));
            dialog.setVisible(true);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "讀取背包失敗：\n" + ex.getMessage(),
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

    /**
     * 從 Maven resources 載入圖片。
     */
    private BufferedImage loadBackgroundImage(String path) {
        try (InputStream inputStream =
                     getClass().getResourceAsStream(path)) {

            if (inputStream == null) {
                throw new IllegalStateException(
                        "找不到背景圖片：" + path
                      + "\n請確認圖片位於 "
                      + "src/main/resources/images/library/"
                );
            }

            BufferedImage image = ImageIO.read(inputStream);

            if (image == null) {
                throw new IllegalStateException(
                        "圖片格式無法讀取：" + path
                );
            }

            return image;

        } catch (IOException e) {
            throw new IllegalStateException(
                    "讀取背景圖片失敗：" + path,
                    e
            );
        }
    }

    /**
     * 顯示背景圖片並管理點擊熱區。
     */
    private static class ScenePanel extends JLayeredPane {

        private static final long serialVersionUID = 1L;

        private final BufferedImage backgroundImage;
        private final List<HotspotSpec> hotspots =
                new ArrayList<>();

        private JPanel hudPanel;

        public ScenePanel(BufferedImage backgroundImage) {
            this.backgroundImage = backgroundImage;

            setLayout(null);
            setOpaque(true);
            setBackground(Color.BLACK);
        }

        /**
         * 設定右下角 HUD。
         */
        public void setHudPanel(JPanel hudPanel) {
            this.hudPanel = hudPanel;

            add(
                    hudPanel,
                    JLayeredPane.MODAL_LAYER
            );
        }

        /**
         * 加入圖片點擊熱區。
         */
        public void addHotspot(
                String accessibleName,
                double x,
                double y,
                double width,
                double height,
                Runnable action) {

            JButton hotspotButton = createHotspotButton(
                    accessibleName,
                    action
            );

            hotspots.add(
                    new HotspotSpec(
                            hotspotButton,
                            x,
                            y,
                            width,
                            height
                    )
            );

            add(
                    hotspotButton,
                    JLayeredPane.PALETTE_LAYER
            );

            revalidate();
            repaint();
        }

        /**
         * 建立透明按鈕。
         */
        private JButton createHotspotButton(
                String accessibleName,
                Runnable action) {

            JButton button = new JButton();

            button.setName(accessibleName);
            button.getAccessibleContext()
                    .setAccessibleName(accessibleName);

            button.setCursor(
                    Cursor.getPredefinedCursor(
                            Cursor.HAND_CURSOR
                    )
            );

            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setOpaque(false);
            button.setFocusable(false);

            if (SHOW_HOTSPOT_BORDERS) {
                button.setBorder(
                        BorderFactory.createLineBorder(
                                Color.RED,
                                2
                        )
                );
                button.setBorderPainted(true);
            } else {
                button.setBorderPainted(false);
            }

            button.addActionListener(e -> action.run());

            return button;
        }

        /**
         * 繪製背景圖片。
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Rectangle imageBounds = getImageBounds();

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR
            );

            g2.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY
            );

            g2.drawImage(
                    backgroundImage,
                    imageBounds.x,
                    imageBounds.y,
                    imageBounds.width,
                    imageBounds.height,
                    null
            );

            g2.dispose();
        }

        /**
         * 根據背景圖片位置配置所有熱區。
         */
        @Override
        public void doLayout() {
            Rectangle imageBounds = getImageBounds();

            for (HotspotSpec hotspot : hotspots) {
                int x = imageBounds.x
                        + (int) Math.round(
                                imageBounds.width * hotspot.x
                        );

                int y = imageBounds.y
                        + (int) Math.round(
                                imageBounds.height * hotspot.y
                        );

                int width = (int) Math.round(
                        imageBounds.width * hotspot.width
                );

                int height = (int) Math.round(
                        imageBounds.height * hotspot.height
                );

                hotspot.button.setBounds(
                        x,
                        y,
                        width,
                        height
                );
            }

            /*
             * HUD 放在圖片右下方的地毯區域，
             * 避免遮住便條紙、密碼盒和主要物件。
             */
            if (hudPanel != null) {
                int hudX = imageBounds.x
                        + (int) Math.round(
                                imageBounds.width * 0.585
                        );

                int hudY = imageBounds.y
                        + (int) Math.round(
                                imageBounds.height * 0.785
                        );

                int hudWidth = (int) Math.round(
                        imageBounds.width * 0.395
                );

                int hudHeight = (int) Math.round(
                        imageBounds.height * 0.190
                );

                hudPanel.setBounds(
                        hudX,
                        hudY,
                        hudWidth,
                        hudHeight
                );
            }
        }

        /**
         * 依照原圖比例計算圖片顯示範圍。
         *
         * 即使 JFrame 改變大小，圖片也不會被拉扁。
         */
        private Rectangle getImageBounds() {
            int panelWidth = getWidth();
            int panelHeight = getHeight();

            if (panelWidth <= 0 || panelHeight <= 0) {
                return new Rectangle();
            }

            double imageRatio =
                    (double) backgroundImage.getWidth()
                    / backgroundImage.getHeight();

            double panelRatio =
                    (double) panelWidth
                    / panelHeight;

            int drawWidth;
            int drawHeight;
            int drawX;
            int drawY;

            if (panelRatio > imageRatio) {
                /*
                 * 面板比較寬，左右留黑邊。
                 */
                drawHeight = panelHeight;

                drawWidth = (int) Math.round(
                        drawHeight * imageRatio
                );

                drawX = (panelWidth - drawWidth) / 2;
                drawY = 0;

            } else {
                /*
                 * 面板比較高，上下留黑邊。
                 */
                drawWidth = panelWidth;

                drawHeight = (int) Math.round(
                        drawWidth / imageRatio
                );

                drawX = 0;
                drawY = (panelHeight - drawHeight) / 2;
            }

            return new Rectangle(
                    drawX,
                    drawY,
                    drawWidth,
                    drawHeight
            );
        }
    }

    /**
     * 儲存一個熱區的相對位置。
     */
    private static class HotspotSpec {

        private final JButton button;

        private final double x;
        private final double y;
        private final double width;
        private final double height;

        public HotspotSpec(
                JButton button,
                double x,
                double y,
                double width,
                double height) {

            this.button = button;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}