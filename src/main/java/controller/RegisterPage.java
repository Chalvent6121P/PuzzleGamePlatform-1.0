package controller;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;

import entity.Player;
import service.PlayerService;
import service.impl.PlayerServiceImpl;

public class RegisterPage extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final Color GOLD = new Color(197, 160, 89);
    private static final Color GOLD_LIGHT = new Color(224, 193, 118);
    private static final Color TEXT = new Color(235, 220, 195);
    private static final Color FIELD_BG = new Color(20, 22, 30);

    private final PlayerService playerService = new PlayerServiceImpl();
    private final Runnable returnToLoginAction;

    private JTextField playerNameField;
    private JTextField accountField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;

    public RegisterPage(Runnable returnToLoginAction) {
        this.returnToLoginAction = returnToLoginAction;
        initFrame();
        initComponents();
    }

    private void initFrame() {
        setTitle("解謎遊戲平台 - 玩家註冊");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(760, 590);
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
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new java.awt.GradientPaint(
                        0, 0, new Color(12, 16, 28),
                        getWidth(), getHeight(), new Color(45, 31, 38)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(197, 160, 89, 45));
                g2.fillOval(450, 40, 240, 240);
                g2.fillOval(520, 250, 160, 160);
                g2.dispose();
            }
        };
        background.setLayout(null);
        setContentPane(background);

        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(new Color(13, 17, 26, 235));
        card.setBorder(new LineBorder(GOLD, 1));
        card.setBounds(165, 55, 430, 455);
        card.setLayout(null);
        background.add(card);

        JLabel title = new JLabel("建立冒險者檔案");
        title.setForeground(GOLD);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBounds(20, 25, 390, 35);
        card.add(title);

        JLabel subtitle = new JLabel("REGISTER NEW PLAYER");
        subtitle.setForeground(new Color(130, 136, 150));
        subtitle.setFont(new Font(Font.SERIF, Font.ITALIC, 12));
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        subtitle.setBounds(20, 60, 390, 20);
        card.add(subtitle);

        playerNameField = addField(card, "玩家名稱", 105);
        accountField = addField(card, "登入帳號", 165);
        passwordField = addPasswordField(card, "登入密碼", 225);
        confirmPasswordField = addPasswordField(card, "確認密碼", 285);

        registerButton = createButton("完成註冊", GOLD, new Color(20, 22, 30));
        registerButton.setBounds(70, 370, 135, 38);
        registerButton.addActionListener(event -> register());
        card.add(registerButton);

        JButton backButton = createButton(
                "返回登入", new Color(45, 51, 65), TEXT);
        backButton.setBounds(225, 370, 135, 38);
        backButton.addActionListener(event -> returnToLogin());
        card.add(backButton);

        getRootPane().setDefaultButton(registerButton);
    }

    private JTextField addField(JPanel card, String labelText, int y) {
        JLabel label = createLabel(labelText, y);
        card.add(label);

        JTextField field = new JTextField();
        styleField(field);
        field.setBounds(150, y, 220, 32);
        card.add(field);
        return field;
    }

    private JPasswordField addPasswordField(
            JPanel card, String labelText, int y) {
        JLabel label = createLabel(labelText, y);
        card.add(label);

        JPasswordField field = new JPasswordField();
        styleField(field);
        field.setBounds(150, y, 220, 32);
        card.add(field);
        return field;
    }

    private JLabel createLabel(String text, int y) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setBounds(30, y, 105, 32);
        return label;
    }

    private void styleField(JTextField field) {
        field.setBackground(FIELD_BG);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        field.setBorder(new LineBorder(new Color(65, 70, 85), 1));
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
        button.setBorder(new LineBorder(GOLD_LIGHT, 1));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void register() {
        String playerName = playerNameField.getText().trim();
        String account = accountField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());

        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(
                    this,
                    "兩次輸入的密碼不一致。",
                    "註冊失敗",
                    JOptionPane.WARNING_MESSAGE);
            confirmPasswordField.setText("");
            confirmPasswordField.requestFocusInWindow();
            return;
        }

        registerButton.setEnabled(false);

        try {
            Player player = new Player(playerName, account, password);
            boolean success = playerService.register(player);

            if (!success) {
                JOptionPane.showMessageDialog(
                        this,
                        "此帳號已經被使用，請更換帳號。",
                        "註冊失敗",
                        JOptionPane.WARNING_MESSAGE);
                accountField.requestFocusInWindow();
                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "註冊成功，歡迎加入解謎冒險！",
                    "註冊完成",
                    JOptionPane.INFORMATION_MESSAGE);
            returnToLogin();

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "註冊資料有誤",
                    JOptionPane.WARNING_MESSAGE
            );

            /*
             * 密碼相關錯誤時，清除密碼並重新聚焦。
             */
            if (ex.getMessage() != null
                    && ex.getMessage().contains("密碼")) {

                passwordField.setText("");
                confirmPasswordField.setText("");
                passwordField.requestFocusInWindow();

            } else if (ex.getMessage() != null
                    && ex.getMessage().contains("帳號")) {

                accountField.requestFocusInWindow();

            } else {

                playerNameField.requestFocusInWindow();
            }

        /*
         * 真正的系統或資料庫錯誤。
         *
         * 這類錯誤才需要輸出 Stack Trace，
         * 方便開發時除錯。
         */
        } catch (RuntimeException ex) {
            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "註冊時發生系統錯誤：\n"
                            + ex.getMessage(),
                    "系統錯誤",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {
            if (isDisplayable()) {
                registerButton.setEnabled(true);
            }
        }
    }

    private void returnToLogin() {
        dispose();
        if (returnToLoginAction != null) {
            returnToLoginAction.run();
        } else {
            new LoginPage().setVisible(true);
        }
    }
}
