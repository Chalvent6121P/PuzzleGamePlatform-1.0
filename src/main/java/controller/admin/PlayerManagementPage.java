package controller.admin;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import entity.Player;
import service.PlayerService;
import service.impl.PlayerServiceImpl;

public class PlayerManagementPage extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Player adminPlayer;
    private final Runnable backAction;
    private final PlayerService playerService = new PlayerServiceImpl();
    private final List<Player> loadedPlayers = new ArrayList<>();

    private DefaultTableModel model;
    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;
    private JTextField nameField;
    private JTextField accountField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;
    private JComboBox<String> statusCombo;
    private JLabel selectedIdLabel;
    private int selectedPlayerNo;
    private boolean returned;

    public PlayerManagementPage(Player adminPlayer, Runnable backAction) {
        if (adminPlayer == null || !adminPlayer.isAdmin()) {
            throw new IllegalArgumentException("只有管理員可以管理玩家。");
        }
        this.adminPlayer = adminPlayer;
        this.backAction = backAction;
        initFrame();
        initComponents();
        loadPlayers();
    }

    private void initFrame() {
        setTitle("PuzzleGamePlatform - 玩家管理");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1230, 760);
        setResizable(false);
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                returnToAdmin();
            }
        });
    }

    private void initComponents() {
        JPanel background = AdminStyle.createBackground();
        background.setLayout(null);
        setContentPane(background);

        JLabel title = new JLabel("玩家管理");
        title.setForeground(AdminStyle.GOLD);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        title.setBounds(35, 22, 250, 42);
        background.add(title);

        JLabel subtitle = new JLabel("PLAYER CRUD · 新增、修改、停用與永久刪除");
        subtitle.setForeground(AdminStyle.MUTED);
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        subtitle.setBounds(38, 61, 480, 24);
        background.add(subtitle);

        JButton backButton = AdminStyle.button(
                "返回控制中心", new Color(63, 66, 77), Color.WHITE);
        backButton.setBounds(1030, 32, 150, 36);
        backButton.addActionListener(event -> {
            dispose();
            returnToAdmin();
        });
        background.add(backButton);

        JLabel searchLabel = new JLabel("搜尋：");
        searchLabel.setForeground(AdminStyle.TEXT);
        searchLabel.setBounds(35, 103, 55, 28);
        background.add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(88, 103, 310, 30);
        searchField.getDocument().addDocumentListener(
                new SimpleDocumentListener(this::applySearch));
        background.add(searchField);

        JButton refreshButton = AdminStyle.button(
                "重新整理", AdminStyle.GOLD, new Color(24, 26, 32));
        refreshButton.setBounds(410, 103, 105, 30);
        refreshButton.addActionListener(event -> loadPlayers());
        background.add(refreshButton);

        model = new DefaultTableModel(new Object[] {
                "編號", "玩家名稱", "帳號", "角色", "狀態",
                "建立時間", "最後登入"
        }, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        AdminStyle.styleTable(table);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) selectTableRow();
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(35, 145, 785, 535);
        scrollPane.setBorder(new LineBorder(AdminStyle.GOLD, 1));
        background.add(scrollPane);

        JPanel form = new JPanel(null);
        form.setBackground(AdminStyle.PANEL);
        form.setBorder(new LineBorder(AdminStyle.GOLD, 1));
        form.setBounds(845, 103, 335, 577);
        background.add(form);

        JLabel formTitle = new JLabel("玩家資料");
        formTitle.setForeground(AdminStyle.TEXT);
        formTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        formTitle.setHorizontalAlignment(SwingConstants.CENTER);
        formTitle.setBounds(20, 18, 295, 30);
        form.add(formTitle);

        selectedIdLabel = label("玩家編號：新增模式", 28, 62, 280, 24);
        selectedIdLabel.setForeground(AdminStyle.GOLD);
        form.add(selectedIdLabel);

        form.add(label("玩家名稱", 28, 102, 100, 22));
        nameField = field(28, 126, 278, 32);
        form.add(nameField);

        form.add(label("登入帳號", 28, 171, 100, 22));
        accountField = field(28, 195, 278, 32);
        form.add(accountField);

        form.add(label("登入密碼", 28, 240, 100, 22));
        passwordField = new JPasswordField();
        passwordField.setBounds(28, 264, 278, 32);
        form.add(passwordField);

        form.add(label("角色", 28, 309, 100, 22));
        roleCombo = new JComboBox<>(new String[] {
                Player.ROLE_PLAYER, Player.ROLE_ADMIN
        });
        roleCombo.setBounds(28, 333, 130, 32);
        form.add(roleCombo);

        form.add(label("狀態", 176, 309, 100, 22));
        statusCombo = new JComboBox<>(new String[] {
                Player.STATUS_ACTIVE, Player.STATUS_INACTIVE
        });
        statusCombo.setBounds(176, 333, 130, 32);
        form.add(statusCombo);

        JButton addButton = AdminStyle.button(
                "新增玩家", AdminStyle.SUCCESS, Color.WHITE);
        addButton.setBounds(28, 395, 130, 36);
        addButton.addActionListener(event -> addPlayer());
        form.add(addButton);

        JButton updateButton = AdminStyle.button(
                "儲存修改", AdminStyle.GOLD, new Color(25, 25, 28));
        updateButton.setBounds(176, 395, 130, 36);
        updateButton.addActionListener(event -> updatePlayer());
        form.add(updateButton);

        JButton toggleButton = AdminStyle.button(
                "切換啟用狀態", new Color(66, 78, 108), Color.WHITE);
        toggleButton.setBounds(28, 446, 278, 36);
        toggleButton.addActionListener(event -> toggleStatus());
        form.add(toggleButton);

        JButton deleteButton = AdminStyle.button(
                "永久刪除玩家與紀錄", AdminStyle.DANGER, Color.WHITE);
        deleteButton.setBounds(28, 495, 278, 36);
        deleteButton.addActionListener(event -> deletePlayer());
        form.add(deleteButton);

        JButton clearButton = AdminStyle.button(
                "清除／新增模式", new Color(63, 66, 77), Color.WHITE);
        clearButton.setBounds(28, 539, 278, 28);
        clearButton.addActionListener(event -> clearForm());
        form.add(clearButton);
    }

    private JLabel label(String text, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setForeground(AdminStyle.TEXT);
        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        label.setBounds(x, y, width, height);
        return label;
    }

    private JTextField field(int x, int y, int width, int height) {
        JTextField field = new JTextField();
        field.setBounds(x, y, width, height);
        return field;
    }

    private void loadPlayers() {
        try {
            loadedPlayers.clear();
            loadedPlayers.addAll(playerService.findAll());
            model.setRowCount(0);
            for (Player player : loadedPlayers) {
                model.addRow(new Object[] {
                        player.getPlayerNo(),
                        player.getPlayerName(),
                        player.getAccount(),
                        player.getRole(),
                        player.getStatus(),
                        format(player.getCreateTime()),
                        format(player.getLastLoginTime())
                });
            }
            clearForm();
            applySearch();
        } catch (RuntimeException error) {
            showError("讀取玩家資料失敗", error);
        }
    }

    private void applySearch() {
        String keyword = searchField == null ? "" : searchField.getText().trim();
        if (keyword.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter(
                    "(?i)" + Pattern.quote(keyword)));
        }
    }

    private void selectTableRow() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return;
        int modelRow = table.convertRowIndexToModel(viewRow);
        selectedPlayerNo = ((Number) model.getValueAt(modelRow, 0)).intValue();
        Player player = findLoaded(selectedPlayerNo);
        if (player == null) return;
        selectedIdLabel.setText("玩家編號：" + selectedPlayerNo);
        nameField.setText(player.getPlayerName());
        accountField.setText(player.getAccount());
        passwordField.setText(player.getPassword());
        roleCombo.setSelectedItem(player.getRole());
        statusCombo.setSelectedItem(player.getStatus());
    }

    private void addPlayer() {
        try {
            Player player = readForm(false);
            if (!playerService.createByAdmin(player)) {
                JOptionPane.showMessageDialog(this,
                        "此帳號已存在，請更換登入帳號。",
                        "新增失敗", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this,
                    "玩家新增成功，玩家編號：" + player.getPlayerNo(),
                    "新增完成", JOptionPane.INFORMATION_MESSAGE);
            loadPlayers();
        } catch (RuntimeException error) {
            showError("新增玩家失敗", error);
        }
    }

    private void updatePlayer() {
        if (selectedPlayerNo <= 0) {
            warnSelect();
            return;
        }
        try {
            Player original = playerService.findByPlayerNo(selectedPlayerNo);
            if (original == null) {
                throw new IllegalArgumentException("找不到選取的玩家，請重新整理。");
            }
            Player player = readForm(true);
            player.setCreateTime(original.getCreateTime());
            player.setLastLoginTime(original.getLastLoginTime());
            if (selectedPlayerNo == adminPlayer.getPlayerNo()
                    && (!Player.ROLE_ADMIN.equals(player.getRole())
                    || !Player.STATUS_ACTIVE.equals(player.getStatus()))) {
                throw new IllegalArgumentException(
                        "目前登入的管理員不可取消管理員角色或停用自己。");
            }
            playerService.update(player);
            JOptionPane.showMessageDialog(this,
                    "玩家資料已更新。", "更新完成",
                    JOptionPane.INFORMATION_MESSAGE);
            loadPlayers();
        } catch (RuntimeException error) {
            showError("更新玩家失敗", error);
        }
    }

    private void toggleStatus() {
        if (selectedPlayerNo <= 0) {
            warnSelect();
            return;
        }
        if (selectedPlayerNo == adminPlayer.getPlayerNo()) {
            JOptionPane.showMessageDialog(this,
                    "目前登入的管理員不可停用自己。",
                    "無法操作", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Player player = playerService.findByPlayerNo(selectedPlayerNo);
            if (player == null) throw new IllegalArgumentException("玩家不存在。");
            player.setStatus(player.isActive()
                    ? Player.STATUS_INACTIVE : Player.STATUS_ACTIVE);
            playerService.update(player);
            loadPlayers();
        } catch (RuntimeException error) {
            showError("切換狀態失敗", error);
        }
    }

    private void deletePlayer() {
        if (selectedPlayerNo <= 0) {
            warnSelect();
            return;
        }
        if (selectedPlayerNo == adminPlayer.getPlayerNo()) {
            JOptionPane.showMessageDialog(this,
                    "目前登入的管理員不可刪除自己。",
                    "無法操作", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Player target = findLoaded(selectedPlayerNo);
        String display = target == null ? String.valueOf(selectedPlayerNo)
                : target.getPlayerName() + "（" + target.getAccount() + "）";
        int choice = JOptionPane.showConfirmDialog(this,
                "確定永久刪除 " + display + "？\n\n"
                + "此操作也會刪除該玩家的存檔、道具、謎題紀錄、成就與遊戲紀錄，且無法復原。",
                "永久刪除確認", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            boolean deleted = playerService.deleteByPlayerNo(selectedPlayerNo);
            if (!deleted) throw new IllegalArgumentException("玩家不存在或已被刪除。");
            JOptionPane.showMessageDialog(this,
                    "玩家與關聯紀錄已永久刪除。",
                    "刪除完成", JOptionPane.INFORMATION_MESSAGE);
            loadPlayers();
        } catch (RuntimeException error) {
            showError("永久刪除失敗", error);
        }
    }

    private Player readForm(boolean update) {
        Player player = new Player();
        if (update) player.setPlayerNo(selectedPlayerNo);
        player.setPlayerName(nameField.getText());
        player.setAccount(accountField.getText());
        player.setPassword(new String(passwordField.getPassword()));
        player.setRole(String.valueOf(roleCombo.getSelectedItem()));
        player.setStatus(String.valueOf(statusCombo.getSelectedItem()));
        return player;
    }

    private Player findLoaded(int playerNo) {
        for (Player player : loadedPlayers) {
            if (player.getPlayerNo() == playerNo) return player;
        }
        return null;
    }

    private void clearForm() {
        selectedPlayerNo = 0;
        if (table != null) table.clearSelection();
        if (selectedIdLabel != null) selectedIdLabel.setText("玩家編號：新增模式");
        if (nameField != null) nameField.setText("");
        if (accountField != null) accountField.setText("");
        if (passwordField != null) passwordField.setText("");
        if (roleCombo != null) roleCombo.setSelectedItem(Player.ROLE_PLAYER);
        if (statusCombo != null) statusCombo.setSelectedItem(Player.STATUS_ACTIVE);
    }

    private void warnSelect() {
        JOptionPane.showMessageDialog(this,
                "請先在左側表格選擇一位玩家。",
                "尚未選擇", JOptionPane.WARNING_MESSAGE);
    }

    private String format(LocalDateTime time) {
        return time == null ? "-" : TIME_FORMAT.format(time);
    }

    private void showError(String title, RuntimeException error) {
        error.printStackTrace();
        JOptionPane.showMessageDialog(this,
                AdminStyle.message(error), title, JOptionPane.ERROR_MESSAGE);
    }

    private void returnToAdmin() {
        if (returned) return;
        returned = true;
        if (backAction != null) backAction.run();
    }

    private interface ChangeAction { void run(); }

    private static class SimpleDocumentListener
            implements javax.swing.event.DocumentListener {
        private final ChangeAction action;
        SimpleDocumentListener(ChangeAction action) { this.action = action; }
        @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { action.run(); }
        @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { action.run(); }
        @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { action.run(); }
    }
}
