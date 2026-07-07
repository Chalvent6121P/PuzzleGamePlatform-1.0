package controller.admin;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import entity.Game;
import entity.GameRecordReportRow;
import entity.Player;
import entity.ReportSummary;
import service.AdminReportService;
import service.GameService;
import service.PlayerService;
import service.impl.AdminReportServiceImpl;
import service.impl.GameServiceImpl;
import service.impl.PlayerServiceImpl;
import util.ReportExporter;

public class ReportCenterPage extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter FILE_TIME =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final Runnable backAction;
    private final PlayerService playerService = new PlayerServiceImpl();
    private final GameService gameService = new GameServiceImpl();
    private final AdminReportService reportService = new AdminReportServiceImpl();

    private JComboBox<String> reportTypeCombo;
    private JTextField searchField;
    private DefaultTableModel model;
    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;
    private final List<JLabel> statValues = new ArrayList<>();
    private String currentTitle = "";
    private boolean returned;

    public ReportCenterPage(Player adminPlayer, Runnable backAction) {
        if (adminPlayer == null || !adminPlayer.isAdmin()) {
            throw new IllegalArgumentException("只有管理員可以查看報表。");
        }
        this.backAction = backAction;
        initFrame();
        initComponents();
        refreshAll();
    }

    private void initFrame() {
        setTitle("PuzzleGamePlatform - 報表中心");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1280, 790);
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

        JLabel title = new JLabel("報表中心");
        title.setForeground(AdminStyle.GOLD);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        title.setBounds(35, 18, 260, 42);
        background.add(title);

        JLabel subtitle = new JLabel("REPORT CENTER · 玩家、遊戲與遊玩紀錄匯出");
        subtitle.setForeground(AdminStyle.MUTED);
        subtitle.setBounds(38, 57, 500, 24);
        background.add(subtitle);

        JButton back = AdminStyle.button(
                "返回控制中心", new Color(63, 66, 77), Color.WHITE);
        back.setBounds(1070, 28, 155, 34);
        back.addActionListener(event -> {
            dispose();
            returnToAdmin();
        });
        background.add(back);

        String[] statNames = {"玩家", "啟用玩家", "遊戲", "啟用遊戲", "紀錄", "成功通關"};
        for (int i = 0; i < statNames.length; i++) {
            createStatCard(background, statNames[i], 35 + i * 198, 91);
        }

        JLabel typeLabel = new JLabel("報表類型：");
        typeLabel.setForeground(AdminStyle.TEXT);
        typeLabel.setBounds(35, 202, 82, 30);
        background.add(typeLabel);

        reportTypeCombo = new JComboBox<>(new String[] {
                "玩家資料", "遊戲資料", "遊玩紀錄"
        });
        reportTypeCombo.setBounds(115, 202, 165, 30);
        reportTypeCombo.addActionListener(event -> loadCurrentReport());
        background.add(reportTypeCombo);

        JLabel searchLabel = new JLabel("搜尋：");
        searchLabel.setForeground(AdminStyle.TEXT);
        searchLabel.setBounds(300, 202, 55, 30);
        background.add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(352, 202, 260, 30);
        searchField.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {
                    @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { applySearch(); }
                    @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { applySearch(); }
                    @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { applySearch(); }
                });
        background.add(searchField);

        JButton refresh = AdminStyle.button(
                "重新整理", AdminStyle.GOLD, new Color(25, 25, 28));
        refresh.setBounds(625, 202, 105, 30);
        refresh.addActionListener(event -> refreshAll());
        background.add(refresh);

        JButton csv = AdminStyle.button("匯出 CSV", new Color(56, 103, 79), Color.WHITE);
        csv.setBounds(828, 202, 115, 30);
        csv.addActionListener(event -> export("csv"));
        background.add(csv);

        JButton txt = AdminStyle.button("匯出 TXT", new Color(68, 79, 108), Color.WHITE);
        txt.setBounds(955, 202, 115, 30);
        txt.addActionListener(event -> export("txt"));
        background.add(txt);

        JButton pdf = AdminStyle.button("匯出 PDF", AdminStyle.DANGER, Color.WHITE);
        pdf.setBounds(1082, 202, 115, 30);
        pdf.addActionListener(event -> export("pdf"));
        background.add(pdf);

        model = new DefaultTableModel() {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        AdminStyle.styleTable(table);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(35, 246, 1162, 450);
        scroll.setBorder(new LineBorder(AdminStyle.GOLD, 1));
        scroll.setPreferredSize(new Dimension(1162, 450));
        background.add(scroll);

        JLabel note = new JLabel(
                "匯出內容會套用目前搜尋篩選；CSV 為 UTF-8 BOM，PDF 可直接顯示中文。",
                SwingConstants.LEFT);
        note.setForeground(AdminStyle.MUTED);
        note.setBounds(38, 705, 780, 24);
        background.add(note);
    }

    private void createStatCard(JPanel parent, String name, int x, int y) {
        JPanel card = new JPanel(null);
        card.setBackground(AdminStyle.PANEL);
        card.setBorder(new LineBorder(new Color(80, 82, 92), 1));
        card.setBounds(x, y, 180, 88);
        parent.add(card);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setForeground(AdminStyle.MUTED);
        nameLabel.setBounds(14, 10, 150, 20);
        card.add(nameLabel);

        JLabel value = new JLabel("0");
        value.setForeground(AdminStyle.GOLD);
        value.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        value.setBounds(14, 34, 150, 38);
        card.add(value);
        statValues.add(value);
    }

    private void refreshAll() {
        try {
            ReportSummary summary = reportService.loadSummary();
            int[] values = {
                    summary.getPlayerCount(),
                    summary.getActivePlayerCount(),
                    summary.getGameCount(),
                    summary.getActiveGameCount(),
                    summary.getRecordCount(),
                    summary.getSuccessCount()
            };
            for (int i = 0; i < statValues.size(); i++) {
                statValues.get(i).setText(String.valueOf(values[i]));
            }
            loadCurrentReport();
        } catch (RuntimeException error) {
            showError("讀取報表失敗", error);
        }
    }

    private void loadCurrentReport() {
        if (model == null || reportTypeCombo == null) return;
        try {
            String type = String.valueOf(reportTypeCombo.getSelectedItem());
            if ("玩家資料".equals(type)) loadPlayerReport();
            else if ("遊戲資料".equals(type)) loadGameReport();
            else loadRecordReport();
            applyColumnWidths();
            applySearch();
        } catch (RuntimeException error) {
            showError("讀取報表資料失敗", error);
        }
    }

    private void loadPlayerReport() {
        currentTitle = "PuzzleGamePlatform 玩家資料報表";
        setColumns("玩家編號", "玩家名稱", "帳號", "角色", "狀態", "建立時間", "最後登入");
        for (Player player : playerService.findAll()) {
            model.addRow(new Object[] {
                    player.getPlayerNo(), player.getPlayerName(), player.getAccount(),
                    player.getRole(), player.getStatus(),
                    format(player.getCreateTime()), format(player.getLastLoginTime())
            });
        }
    }

    private void loadGameReport() {
        currentTitle = "PuzzleGamePlatform 遊戲資料報表";
        setColumns("遊戲編號", "遊戲名稱", "難度", "狀態", "封面路徑", "描述", "建立時間");
        for (Game game : gameService.findAll()) {
            model.addRow(new Object[] {
                    game.getGameNo(), game.getGameName(), game.getDifficulty(),
                    game.isActive() ? "啟用" : "停用", game.getCoverImagePath(),
                    game.getDescription(), format(game.getCreateTime())
            });
        }
    }

    private void loadRecordReport() {
        currentTitle = "PuzzleGamePlatform 遊玩紀錄報表";
        setColumns("紀錄編號", "玩家編號", "玩家名稱", "帳號", "遊戲編號", "遊戲名稱",
                "房間", "謎題", "結局", "進度", "結果", "目前步驟", "開始時間", "結束時間");
        for (GameRecordReportRow row : reportService.findAllGameRecords()) {
            model.addRow(new Object[] {
                    row.getRecordNo(), row.getPlayerNo(), row.getPlayerName(), row.getAccount(),
                    row.getGameNo(), row.getGameName(), value(row.getRoomName()),
                    value(row.getPuzzleName()), value(row.getEndingName()),
                    row.getProgressStatus(), row.getResultStatus(), value(row.getCurrentStep()),
                    format(row.getStartTime()), format(row.getEndTime())
            });
        }
    }

    private void setColumns(String... columns) {
        sorter.setRowFilter(null);
        model.setDataVector(new Object[0][0], columns);
        sorter.setModel(model);
    }

    private void applySearch() {
        if (sorter == null) return;
        String keyword = searchField == null ? "" : searchField.getText().trim();
        sorter.setRowFilter(keyword.isEmpty() ? null
                : RowFilter.regexFilter("(?i)" + Pattern.quote(keyword)));
    }

    private void applyColumnWidths() {
        TableColumnModel columns = table.getColumnModel();
        for (int i = 0; i < columns.getColumnCount(); i++) {
            String name = columns.getColumn(i).getHeaderValue().toString();
            int width = 100;
            if (name.contains("描述") || name.contains("步驟") || name.contains("路徑")) width = 260;
            else if (name.contains("時間")) width = 150;
            else if (name.contains("名稱") || name.equals("帳號")) width = 130;
            else if (name.equals("房間") || name.equals("謎題") || name.equals("結局")) width = 145;
            columns.getColumn(i).setPreferredWidth(width);
        }
    }

    private void export(String extension) {
        if (table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "目前沒有可匯出的資料。",
                    "無資料", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("匯出 " + extension.toUpperCase() + " 報表");
        chooser.setFileFilter(new FileNameExtensionFilter(
                extension.toUpperCase() + " 檔案", extension));
        chooser.setSelectedFile(new File(defaultFileName(extension)));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = ensureExtension(chooser.getSelectedFile(), extension);
        if (file.exists()) {
            int overwrite = JOptionPane.showConfirmDialog(this,
                    "檔案已存在，是否覆蓋？\n" + file.getAbsolutePath(),
                    "覆蓋確認", JOptionPane.YES_NO_OPTION);
            if (overwrite != JOptionPane.YES_OPTION) return;
        }

        try {
            String[] headers = currentHeaders();
            List<String[]> rows = currentVisibleRows();
            if ("csv".equals(extension)) ReportExporter.exportCsv(file, headers, rows);
            else if ("txt".equals(extension)) ReportExporter.exportTxt(file, currentTitle, headers, rows);
            else ReportExporter.exportPdf(file, currentTitle, headers, rows);
            JOptionPane.showMessageDialog(this,
                    "報表匯出完成：\n" + file.getAbsolutePath(),
                    "匯出完成", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | RuntimeException error) {
            error.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    AdminStyle.message(error), "匯出失敗", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String[] currentHeaders() {
        String[] headers = new String[model.getColumnCount()];
        for (int i = 0; i < headers.length; i++) headers[i] = model.getColumnName(i);
        return headers;
    }

    private List<String[]> currentVisibleRows() {
        List<String[]> rows = new ArrayList<>();
        for (int viewRow = 0; viewRow < table.getRowCount(); viewRow++) {
            int modelRow = table.convertRowIndexToModel(viewRow);
            String[] values = new String[model.getColumnCount()];
            for (int column = 0; column < values.length; column++) {
                Object value = model.getValueAt(modelRow, column);
                values[column] = value == null ? "" : String.valueOf(value);
            }
            rows.add(values);
        }
        return rows;
    }

    private String defaultFileName(String extension) {
        String type = String.valueOf(reportTypeCombo.getSelectedItem())
                .replace("資料", "").replace("紀錄", "records");
        return "PuzzleGamePlatform_" + type + "_"
                + FILE_TIME.format(LocalDateTime.now()) + "." + extension;
    }

    private File ensureExtension(File file, String extension) {
        if (file.getName().toLowerCase().endsWith("." + extension)) return file;
        return new File(file.getParentFile(), file.getName() + "." + extension);
    }

    private String format(LocalDateTime time) {
        return time == null ? "-" : DATE_TIME.format(time);
    }

    private String value(String text) {
        return text == null || text.trim().isEmpty() ? "-" : text;
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
}
