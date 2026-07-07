package controller.player;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import entity.Player;
import entity.PlayerAchievementRecord;
import service.PlayerAchievementService;
import service.impl.PlayerAchievementServiceImpl;
import util.ReportExporter;

/**
 * 玩家自己的成就總覽。可查看解鎖狀態、輸出 CSV/TXT/PDF，或交由印表機列印。
 */
public class PlayerAchievementDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private static final Color BG = new Color(23, 25, 31);
    private static final Color PANEL = new Color(34, 38, 48);
    private static final Color GOLD = new Color(197, 160, 89);
    private static final Color TEXT = new Color(236, 226, 207);
    private static final Color MUTED = new Color(165, 170, 180);
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter FILE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final Player player;
    private final PlayerAchievementService achievementService =
            new PlayerAchievementServiceImpl();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[] {
                "狀態", "遊戲", "成就名稱", "成就說明", "解鎖條件", "解鎖時間"
            }, 0) {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private JTable table;
    private JLabel summaryLabel;
    private List<PlayerAchievementRecord> records = new ArrayList<>();

    public PlayerAchievementDialog(Frame owner, Player player) {
        super(owner, "我的成就", true);
        if (player == null || player.getPlayerNo() <= 0) {
            throw new IllegalArgumentException("玩家資料不完整。");
        }
        this.player = player;
        initFrame();
        initComponents();
        loadRecords();
    }

    private void initFrame() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1040, 640);
        setMinimumSize(new Dimension(900, 540));
        setLocationRelativeTo(getOwner());
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(18, 18, 18, 18));
        setContentPane(root);

        JPanel header = new JPanel(new BorderLayout(8, 8));
        header.setOpaque(false);

        JLabel title = new JLabel("我的成就紀錄");
        title.setForeground(GOLD);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
        header.add(title, BorderLayout.NORTH);

        String displayName = safe(player.getPlayerName());
        if (displayName.isEmpty()) {
            displayName = safe(player.getAccount());
        }
        summaryLabel = new JLabel("玩家：" + displayName + "　載入中...");
        summaryLabel.setForeground(MUTED);
        summaryLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        header.add(summaryLabel, BorderLayout.SOUTH);
        root.add(header, BorderLayout.NORTH);

        table = new JTable(tableModel);
        table.setBackground(PANEL);
        table.setForeground(TEXT);
        table.setGridColor(new Color(76, 80, 92));
        table.setSelectionBackground(new Color(82, 67, 42));
        table.setSelectionForeground(Color.WHITE);
        table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        table.setRowHeight(34);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setBackground(new Color(49, 53, 65));
        table.getTableHeader().setForeground(GOLD);
        table.getTableHeader().setFont(
                new Font(Font.SANS_SERIF, Font.BOLD, 13));
        table.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(1).setCellRenderer(center);
        table.getColumnModel().getColumn(5).setCellRenderer(center);
        setColumnWidths(table.getColumnModel());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(PANEL);
        scrollPane.setBorder(new LineBorder(new Color(99, 86, 62), 1));
        root.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 6, 9, 0));
        buttonPanel.setOpaque(false);

        JButton refreshButton = createButton("重新整理", new Color(55, 62, 75));
        refreshButton.addActionListener(event -> loadRecords());
        buttonPanel.add(refreshButton);

        JButton csvButton = createButton("輸出 CSV", new Color(55, 62, 75));
        csvButton.addActionListener(event -> export("csv"));
        buttonPanel.add(csvButton);

        JButton txtButton = createButton("輸出 TXT", new Color(55, 62, 75));
        txtButton.addActionListener(event -> export("txt"));
        buttonPanel.add(txtButton);

        JButton pdfButton = createButton("輸出 PDF", new Color(55, 62, 75));
        pdfButton.addActionListener(event -> export("pdf"));
        buttonPanel.add(pdfButton);

        JButton printButton = createButton("列印", new Color(72, 63, 47));
        printButton.addActionListener(event -> printTable());
        buttonPanel.add(printButton);

        JButton closeButton = createButton("關閉", new Color(83, 45, 52));
        closeButton.addActionListener(event -> dispose());
        buttonPanel.add(closeButton);

        root.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setColumnWidths(TableColumnModel columns) {
        columns.getColumn(0).setPreferredWidth(72);
        columns.getColumn(1).setPreferredWidth(120);
        columns.getColumn(2).setPreferredWidth(145);
        columns.getColumn(3).setPreferredWidth(250);
        columns.getColumn(4).setPreferredWidth(170);
        columns.getColumn(5).setPreferredWidth(150);
    }

    private JButton createButton(String text, Color background) {
        JButton button = new JButton(text);
        button.setUI(new BasicButtonUI());
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setMargin(new Insets(0, 4, 0, 4));
        button.setBorder(new LineBorder(new Color(199, 170, 112), 1));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void loadRecords() {
        try {
            records = achievementService.findAchievementRecords(
                    player.getPlayerNo());
            tableModel.setRowCount(0);
            int unlockedCount = 0;
            for (PlayerAchievementRecord record : records) {
                if (record.isUnlocked()) {
                    unlockedCount++;
                }
                tableModel.addRow(toTableRow(record));
            }
            String name = safe(player.getPlayerName());
            if (name.isEmpty()) {
                name = safe(player.getAccount());
            }
            summaryLabel.setText("玩家：" + name
                    + "　已解鎖：" + unlockedCount + " / " + records.size()
                    + "　（解鎖狀態僅依此帳號計算）");
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "讀取成就失敗：\n" + safeMessage(ex),
                    "成就紀錄錯誤",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Object[] toTableRow(PlayerAchievementRecord record) {
        return new Object[] {
            record.isUnlocked() ? "已解鎖" : "未解鎖",
            safe(record.getGameName()),
            safe(record.getAchievementName()),
            safe(record.getDescription()),
            safe(record.getConditionText()),
            record.getUnlockTime() == null
                    ? "-" : TIME_FORMAT.format(record.getUnlockTime())
        };
    }

    private void export(String type) {
        JFileChooser chooser = new JFileChooser();
        String baseName = "玩家成就紀錄_" + safeFileName(player.getAccount())
                + "_" + FILE_TIME_FORMAT.format(LocalDateTime.now());
        chooser.setSelectedFile(new File(baseName + "." + type));
        chooser.setFileFilter(new FileNameExtensionFilter(
                type.toUpperCase() + " 檔案", type));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = ensureExtension(chooser.getSelectedFile(), type);
        if (file.exists()) {
            int overwrite = JOptionPane.showConfirmDialog(
                    this,
                    "檔案已存在，是否覆蓋？",
                    "確認覆蓋",
                    JOptionPane.YES_NO_OPTION);
            if (overwrite != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            String[] headers = exportHeaders();
            List<String[]> rows = exportRows();
            if ("csv".equals(type)) {
                ReportExporter.exportCsv(file, headers, rows);
            } else if ("txt".equals(type)) {
                ReportExporter.exportTxt(
                        file, buildReportTitle(), headers, rows);
            } else {
                ReportExporter.exportPdf(
                        file, buildReportTitle(), headers, rows);
            }
            JOptionPane.showMessageDialog(
                    this,
                    "成就紀錄已輸出：\n" + file.getAbsolutePath(),
                    "輸出完成",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "輸出失敗：\n" + ex.getMessage(),
                    "輸出錯誤",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printTable() {
        try {
            String name = safe(player.getPlayerName());
            if (name.isEmpty()) {
                name = safe(player.getAccount());
            }
            boolean completed = table.print(
                    JTable.PrintMode.FIT_WIDTH,
                    new MessageFormat("PuzzleGamePlatform - " + name + " 的成就紀錄"),
                    new MessageFormat("第 {0} 頁"));
            if (completed) {
                JOptionPane.showMessageDialog(
                        this,
                        "成就紀錄已送至印表機。",
                        "列印完成",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (PrinterException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "列印失敗：\n" + ex.getMessage(),
                    "列印錯誤",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String[] exportHeaders() {
        return new String[] {
            "狀態", "遊戲", "成就名稱", "成就說明", "解鎖條件", "解鎖時間"
        };
    }

    private List<String[]> exportRows() {
        List<String[]> rows = new ArrayList<>();
        for (PlayerAchievementRecord record : records) {
            rows.add(new String[] {
                record.isUnlocked() ? "已解鎖" : "未解鎖",
                safe(record.getGameName()),
                safe(record.getAchievementName()),
                safe(record.getDescription()),
                safe(record.getConditionText()),
                record.getUnlockTime() == null
                        ? "-" : TIME_FORMAT.format(record.getUnlockTime())
            });
        }
        return rows;
    }

    private String buildReportTitle() {
        String name = safe(player.getPlayerName());
        if (name.isEmpty()) {
            name = safe(player.getAccount());
        }
        return "PuzzleGamePlatform - " + name + " 的成就紀錄";
    }

    private File ensureExtension(File file, String extension) {
        String lower = file.getName().toLowerCase();
        return lower.endsWith("." + extension.toLowerCase())
                ? file : new File(file.getParentFile(),
                        file.getName() + "." + extension);
    }

    private String safeFileName(String value) {
        String safe = safe(value).replaceAll("[\\\\/:*?\"<>|]", "_");
        return safe.isEmpty() ? "player" : safe;
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private String safeMessage(RuntimeException ex) {
        String message = ex.getMessage();
        if ((message == null || message.trim().isEmpty())
                && ex.getCause() != null) {
            message = ex.getCause().getMessage();
        }
        return message == null || message.trim().isEmpty()
                ? ex.getClass().getSimpleName() : message;
    }
}
