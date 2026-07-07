package controller;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;

import controller.admin.AdminMainPage;
import entity.Player;
import service.PlayerService;
import service.impl.PlayerServiceImpl;

public class LoginPage extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final String BACKGROUND_PATH =
            "/images/Login/LoginPage.png";

    private static final Color GOLD = new Color(197, 160, 89);
    private static final Color GOLD_LIGHT = new Color(224, 193, 118);
    private static final Color TEXT = new Color(235, 220, 195);
    private static final Color FIELD_BG = new Color(20, 22, 30);
    private static final Color DARK = new Color(20, 22, 30);

    private JTextField account;
    private JPasswordField password;
    private JButton btnLogin;
    private JButton btnRegister;

    private final PlayerService playerService = new PlayerServiceImpl();

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                LoginPage frame = new LoginPage();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public LoginPage() {
        initFrame();
        initComponents();
        SwingUtilities.invokeLater(() -> account.requestFocusInWindow());
    }

    private void initFrame() {
        setTitle("神祕古宅 - 登入頁面");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(840, 590);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel contentPane = createBackgroundPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        JPanel loginCard = createLoginCard();
        contentPane.add(loginCard);

        JLabel title = new JLabel("MYSTERY PUZZLE");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setForeground(GOLD);
        title.setFont(new Font(Font.SERIF, Font.BOLD, 22));
        title.setBounds(10, 20, 300, 30);
        loginCard.add(title);

        JLabel subtitle = new JLabel("PLAYER ACCESS");
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        subtitle.setForeground(new Color(145, 150, 165));
        subtitle.setFont(new Font(Font.SERIF, Font.ITALIC, 12));
        subtitle.setBounds(10, 50, 300, 20);
        loginCard.add(subtitle);

        JLabel accountLabel = createFieldLabel("帳號", 30, 88);
        loginCard.add(accountLabel);

        account = createTextField();
        account.setBounds(95, 88, 190, 30);
        loginCard.add(account);

        JLabel passwordLabel = createFieldLabel("密碼", 30, 138);
        loginCard.add(passwordLabel);

        password = new JPasswordField();
        styleInputField(password);
        password.setBounds(95, 138, 190, 30);
        loginCard.add(password);

        btnLogin = createButton("進入", GOLD, DARK);
        btnLogin.setBounds(45, 205, 105, 36);
        btnLogin.addActionListener(event -> executeLogin());
        loginCard.add(btnLogin);

        btnRegister = createButton(
                "註冊", new Color(45, 51, 65), TEXT);
        btnRegister.setBounds(170, 205, 105, 36);
        btnRegister.addActionListener(event -> openRegisterPage());
        loginCard.add(btnRegister);

        getRootPane().setDefaultButton(btnLogin);
    }

    private JPanel createLoginCard() {
        JPanel card = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(12, 16, 25, 216));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBounds(75, 125, 320, 285);
        card.setBorder(new LineBorder(GOLD, 1));
        card.setLayout(null);
        return card;
    }

    private JLabel createFieldLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        label.setForeground(TEXT);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBounds(x, y, 50, 30);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        styleInputField(field);
        return field;
    }

    private void styleInputField(JTextField field) {
        field.setBackground(FIELD_BG);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        field.setBorder(new LineBorder(new Color(60, 65, 80), 1));
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
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        button.setFocusPainted(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setBorder(new LineBorder(GOLD_LIGHT, 1));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JPanel createBackgroundPanel() {
        URL imageUrl = LoginPage.class.getResource(BACKGROUND_PATH);
        if (imageUrl == null) {
            throw new IllegalStateException(
                    "找不到登入背景圖片：" + BACKGROUND_PATH);
        }

        Image image = new ImageIcon(imageUrl).getImage();
        return new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                g2.dispose();
            }
        };
    }

    private void executeLogin() {
        String inputAccount = account.getText().trim();
        String inputPassword = new String(password.getPassword());

        if (inputAccount.isEmpty() || inputPassword.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "冒險者，請輸入完整的帳號與密碼！",
                    "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        setButtonsEnabled(false);

        try {
            Player loginPlayer = playerService.login(
                    inputAccount, inputPassword);

            if (loginPlayer == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "帳號或密碼錯誤，或帳號已停用。",
                        "存取被拒",
                        JOptionPane.ERROR_MESSAGE);
                password.setText("");
                password.requestFocusInWindow();
                return;
            }

            if (loginPlayer.isAdmin()) {
                new AdminMainPage(loginPlayer).setVisible(true);
            } else {
                new GameMainPage(loginPlayer).setVisible(true);
            }

            dispose();

        } catch (RuntimeException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "登入時發生資料庫錯誤：\n" + safeMessage(ex),
                    "系統錯誤",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            if (isDisplayable()) {
                setButtonsEnabled(true);
            }
        }
    }

    private void openRegisterPage() {
        RegisterPage page = new RegisterPage(this::showLoginPage);
        page.setVisible(true);
        setVisible(false);
    }

    private void showLoginPage() {
        setLocationRelativeTo(null);
        setVisible(true);
        account.requestFocusInWindow();
    }

    private void setButtonsEnabled(boolean enabled) {
        btnLogin.setEnabled(enabled);
        btnRegister.setEnabled(enabled);
    }

    private String safeMessage(RuntimeException ex) {
        String message = ex.getMessage();
        return message == null || message.trim().isEmpty()
                ? ex.getClass().getSimpleName() : message;
    }
}
