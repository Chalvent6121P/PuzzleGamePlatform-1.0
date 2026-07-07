package controller.admin;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import dao.EndingDao;
import dao.ItemDao;
import dao.PuzzleDao;
import dao.RoomDao;
import dao.impl.EndingDaoImpl;
import dao.impl.ItemDaoImpl;
import dao.impl.PuzzleDaoImpl;
import dao.impl.RoomDaoImpl;
import entity.Ending;
import entity.Game;
import entity.Item;
import entity.Player;
import entity.Puzzle;
import entity.Room;
import service.GameService;
import service.impl.GameServiceImpl;

public class GameManagementPage extends JFrame {

    private static final long serialVersionUID = 1L;

    private final Runnable backAction;
    private final GameService gameService = new GameServiceImpl();
    private final RoomDao roomDao = new RoomDaoImpl();
    private final PuzzleDao puzzleDao = new PuzzleDaoImpl();
    private final ItemDao itemDao = new ItemDaoImpl();
    private final EndingDao endingDao = new EndingDaoImpl();
    private final List<Game> games = new ArrayList<>();

    private GamePanel gamePanel;
    private RoomPanel roomPanel;
    private PuzzlePanel puzzlePanel;
    private ItemPanel itemPanel;
    private EndingPanel endingPanel;
    private boolean returned;

    public GameManagementPage(Player adminPlayer, Runnable backAction) {
        if (adminPlayer == null || !adminPlayer.isAdmin()) {
            throw new IllegalArgumentException("只有管理員可以管理遊戲內容。");
        }
        this.backAction = backAction;
        initFrame();
        initComponents();
        refreshAll();
    }

    private void initFrame() {
        setTitle("PuzzleGamePlatform - 遊戲內容管理");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1240, 790);
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

        JLabel title = new JLabel("遊戲內容管理");
        title.setForeground(AdminStyle.GOLD);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        title.setBounds(35, 20, 320, 42);
        background.add(title);

        JLabel subtitle = new JLabel("GAME CONTENT CRUD · 遊戲、房間、謎題、道具與結局");
        subtitle.setForeground(AdminStyle.MUTED);
        subtitle.setBounds(38, 59, 520, 24);
        background.add(subtitle);

        JButton refresh = AdminStyle.button(
                "全部重新整理", AdminStyle.GOLD, new Color(24, 26, 32));
        refresh.setBounds(890, 30, 135, 34);
        refresh.addActionListener(event -> refreshAll());
        background.add(refresh);

        JButton back = AdminStyle.button(
                "返回控制中心", new Color(63, 66, 77), Color.WHITE);
        back.setBounds(1038, 30, 150, 34);
        back.addActionListener(event -> {
            dispose();
            returnToAdmin();
        });
        background.add(back);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        tabs.setBounds(30, 94, 1160, 620);
        gamePanel = new GamePanel();
        roomPanel = new RoomPanel();
        puzzlePanel = new PuzzlePanel();
        itemPanel = new ItemPanel();
        endingPanel = new EndingPanel();
        tabs.addTab("遊戲", gamePanel);
        tabs.addTab("房間", roomPanel);
        tabs.addTab("謎題", puzzlePanel);
        tabs.addTab("道具", itemPanel);
        tabs.addTab("結局", endingPanel);
        background.add(tabs);
    }

    private void refreshAll() {
        try {
            games.clear();
            games.addAll(gameService.findAll());
            gamePanel.loadRows();
            roomPanel.reloadGameChoices();
            puzzlePanel.reloadGameChoices();
            itemPanel.reloadGameChoices();
            endingPanel.reloadGameChoices();
        } catch (RuntimeException error) {
            showError("重新整理遊戲內容失敗", error);
        }
    }

    private Game findGame(int gameNo) {
        for (Game game : games) {
            if (game.getGameNo() == gameNo) return game;
        }
        return null;
    }

    private int selectedGameNo(JComboBox<GameChoice> combo) {
        GameChoice choice = (GameChoice) combo.getSelectedItem();
        return choice == null ? 0 : choice.gameNo;
    }

    private void fillGameCombo(JComboBox<GameChoice> combo) {
        int old = selectedGameNo(combo);
        combo.removeAllItems();
        for (Game game : games) {
            combo.addItem(new GameChoice(game.getGameNo(), game.getGameName()));
        }
        if (old > 0) {
            for (int i = 0; i < combo.getItemCount(); i++) {
                if (combo.getItemAt(i).gameNo == old) {
                    combo.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void showError(String title, RuntimeException error) {
        error.printStackTrace();
        JOptionPane.showMessageDialog(this,
                AdminStyle.message(error), title, JOptionPane.ERROR_MESSAGE);
    }

    private boolean confirmDelete(String type, String name) {
        return JOptionPane.showConfirmDialog(this,
                "確定刪除" + type + "「" + name + "」？\n"
                        + "若已有關聯紀錄，系統會阻止刪除。",
                "刪除確認", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private String required(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + "不可空白。");
        }
        return value.trim();
    }

    private int positiveInt(String value, String label) {
        try {
            int number = Integer.parseInt(required(value, label));
            if (number < 0) throw new NumberFormatException();
            return number;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(label + "必須是 0 以上的整數。");
        }
    }

    private void returnToAdmin() {
        if (returned) return;
        returned = true;
        if (backAction != null) backAction.run();
    }

    private abstract class BasePanel extends JPanel {
        private static final long serialVersionUID = 1L;
        final DefaultTableModel model;
        final JTable table;
        int selectedId;

        BasePanel(Object[] columns, boolean gameFilter) {
            setLayout(null);
            setBackground(AdminStyle.BG);
            model = new DefaultTableModel(columns, 0) {
                private static final long serialVersionUID = 1L;
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
            table = new JTable(model);
            AdminStyle.styleTable(table);
            table.getSelectionModel().addListSelectionListener(event -> {
                if (!event.getValueIsAdjusting()) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        int modelRow = table.convertRowIndexToModel(row);
                        selectedId = ((Number) model.getValueAt(modelRow, 0)).intValue();
                        selectEntity(selectedId);
                    }
                }
            });
            JScrollPane scroll = new JScrollPane(table);
            scroll.setBorder(new LineBorder(AdminStyle.GOLD, 1));
            scroll.setBounds(18, gameFilter ? 55 : 20, 660, gameFilter ? 500 : 535);
            add(scroll);
        }

        JLabel formLabel(String text, int y) {
            JLabel label = new JLabel(text);
            label.setForeground(AdminStyle.TEXT);
            label.setBounds(20, y, 130, 22);
            return label;
        }

        JTextField formField(int y) {
            JTextField field = new JTextField();
            field.setBounds(20, y, 380, 31);
            return field;
        }

        JTextArea formArea(int y, int height) {
            JTextArea area = new JTextArea();
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
            area.setBounds(20, y, 380, height);
            area.setBorder(new LineBorder(new Color(170, 170, 175), 1));
            return area;
        }

        JPanel createFormPanel(String title) {
            JPanel form = new JPanel(null);
            form.setBackground(AdminStyle.PANEL);
            form.setBorder(new LineBorder(AdminStyle.GOLD, 1));
            form.setBounds(700, 20, 425, 535);
            JLabel label = new JLabel(title);
            label.setForeground(AdminStyle.GOLD);
            label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 19));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBounds(15, 10, 395, 28);
            form.add(label);
            add(form);
            return form;
        }

        void addCrudButtons(JPanel form, Runnable add, Runnable update,
                Runnable delete, Runnable clear) {
            JButton addButton = AdminStyle.button("新增", AdminStyle.SUCCESS, Color.WHITE);
            addButton.setBounds(20, 447, 88, 34);
            addButton.addActionListener(event -> add.run());
            form.add(addButton);
            JButton updateButton = AdminStyle.button("修改", AdminStyle.GOLD, new Color(25, 25, 28));
            updateButton.setBounds(118, 447, 88, 34);
            updateButton.addActionListener(event -> update.run());
            form.add(updateButton);
            JButton deleteButton = AdminStyle.button("刪除", AdminStyle.DANGER, Color.WHITE);
            deleteButton.setBounds(216, 447, 88, 34);
            deleteButton.addActionListener(event -> delete.run());
            form.add(deleteButton);
            JButton clearButton = AdminStyle.button("清除", new Color(63, 66, 77), Color.WHITE);
            clearButton.setBounds(314, 447, 88, 34);
            clearButton.addActionListener(event -> clear.run());
            form.add(clearButton);
        }

        void clearSelection() {
            selectedId = 0;
            table.clearSelection();
        }

        abstract void loadRows();
        abstract void selectEntity(int id);
        abstract void clearForm();
    }

    private class GamePanel extends BasePanel {
        private static final long serialVersionUID = 1L;
        private final JTextField name = formField(72);
        private final JComboBox<String> difficulty = new JComboBox<>(
                new String[] {"簡單", "普通", "困難", "自訂"});
        private final JComboBox<String> active = new JComboBox<>(
                new String[] {"啟用", "停用"});
        private final JTextField cover = formField(254);
        private final JTextArea description = formArea(337, 88);

        GamePanel() {
            super(new Object[] {"編號", "名稱", "難度", "狀態", "圖片路徑", "描述"}, false);
            JPanel form = createFormPanel("遊戲資料");
            difficulty.setEditable(true);
            form.add(formLabel("遊戲名稱", 49)); form.add(name);
            form.add(formLabel("難度", 112));
            difficulty.setBounds(20, 136, 180, 31); form.add(difficulty);
            form.add(formLabel("狀態", 177));
            active.setBounds(20, 201, 180, 31); form.add(active);
            form.add(formLabel("封面資源路徑", 231)); form.add(cover);
            form.add(formLabel("遊戲描述", 313)); form.add(description);
            addCrudButtons(form, this::addGame, this::updateGame,
                    this::deleteGame, this::clearForm);
        }

        @Override void loadRows() {
            model.setRowCount(0);
            for (Game game : games) {
                model.addRow(new Object[] {game.getGameNo(), game.getGameName(),
                        game.getDifficulty(), game.isActive() ? "啟用" : "停用",
                        game.getCoverImagePath(), game.getDescription()});
            }
            clearForm();
        }

        @Override void selectEntity(int id) {
            Game game = findGame(id);
            if (game == null) return;
            name.setText(game.getGameName());
            difficulty.setSelectedItem(game.getDifficulty());
            if (difficulty.getSelectedIndex() < 0) difficulty.setSelectedItem("自訂");
            active.setSelectedItem(game.isActive() ? "啟用" : "停用");
            cover.setText(game.getCoverImagePath());
            description.setText(game.getDescription());
        }

        private Game read() {
            Game game = new Game();
            game.setGameNo(selectedId);
            game.setGameName(required(name.getText(), "遊戲名稱"));
            String diff = required(
                    String.valueOf(difficulty.getSelectedItem()), "難度");
            game.setDifficulty(diff);
            game.setActive("啟用".equals(active.getSelectedItem()));
            game.setCoverImagePath(cover.getText());
            game.setDescription(description.getText());
            return game;
        }

        private void addGame() {
            try { gameService.create(read()); refreshAll(); }
            catch (RuntimeException e) { showError("新增遊戲失敗", e); }
        }

        private void updateGame() {
            if (selectedId <= 0) { warnSelect(); return; }
            try { gameService.update(read()); refreshAll(); }
            catch (RuntimeException e) { showError("修改遊戲失敗", e); }
        }

        private void deleteGame() {
            Game game = findGame(selectedId);
            if (game == null) { warnSelect(); return; }
            if (!confirmDelete("遊戲", game.getGameName())) return;
            try { gameService.delete(selectedId); refreshAll(); }
            catch (RuntimeException e) { showError("刪除遊戲失敗；可改用停用狀態保留歷史資料", e); }
        }

        private void warnSelect() {
            JOptionPane.showMessageDialog(GameManagementPage.this,
                    "請先選擇一筆遊戲資料。", "尚未選擇",
                    JOptionPane.WARNING_MESSAGE);
        }

        @Override void clearForm() {
            clearSelection(); name.setText(""); difficulty.setSelectedIndex(0);
            active.setSelectedIndex(0); cover.setText(""); description.setText("");
        }
    }

    private abstract class FilteredPanel extends BasePanel {
        private static final long serialVersionUID = 1L;
        final JComboBox<GameChoice> gameCombo = new JComboBox<>();

        FilteredPanel(Object[] columns, String formTitle) {
            super(columns, true);
            JLabel label = new JLabel("選擇遊戲：");
            label.setForeground(AdminStyle.TEXT);
            label.setBounds(18, 15, 85, 28);
            add(label);
            gameCombo.setBounds(102, 15, 370, 30);
            gameCombo.addActionListener(event -> onGameChanged());
            add(gameCombo);
            createFilteredForm(formTitle);
        }

        abstract void createFilteredForm(String title);

        void reloadGameChoices() {
            fillGameCombo(gameCombo);
            onGameChanged();
        }

        void onGameChanged() {
            clearForm();
            if (selectedGameNo(gameCombo) > 0) loadRows();
            else model.setRowCount(0);
        }

        int gameNo() {
            int no = selectedGameNo(gameCombo);
            if (no <= 0) throw new IllegalArgumentException("請先選擇遊戲。");
            return no;
        }

        void warnSelect(String type) {
            JOptionPane.showMessageDialog(GameManagementPage.this,
                    "請先選擇一筆" + type + "資料。", "尚未選擇",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private class RoomPanel extends FilteredPanel {
        private static final long serialVersionUID = 1L;
        private JTextField name;
        private JTextField order;
        private JTextArea description;

        RoomPanel() { super(new Object[] {"編號", "房間名稱", "順序", "描述"}, "房間資料"); }

        @Override void createFilteredForm(String title) {
            JPanel form = createFormPanel(title);
            name = formField(78); order = formField(150); description = formArea(230, 150);
            form.add(formLabel("房間名稱", 55)); form.add(name);
            form.add(formLabel("房間順序", 127)); form.add(order);
            form.add(formLabel("房間描述", 207)); form.add(description);
            addCrudButtons(form, this::addRoom, this::updateRoom, this::deleteRoom, this::clearForm);
        }

        @Override void loadRows() {
            model.setRowCount(0);
            for (Room room : roomDao.selectByGameNo(selectedGameNo(gameCombo))) {
                model.addRow(new Object[] {room.getRoomNo(), room.getRoomName(), room.getRoomOrder(), room.getDescription()});
            }
        }

        @Override void selectEntity(int id) {
            Room room = roomDao.selectByRoomNo(id);
            if (room == null) return;
            name.setText(room.getRoomName()); order.setText(String.valueOf(room.getRoomOrder()));
            description.setText(room.getDescription());
        }

        Room read() {
            Room room = new Room(); room.setRoomNo(selectedId); room.setGameNo(gameNo());
            room.setRoomName(required(name.getText(), "房間名稱"));
            room.setRoomOrder(positiveInt(order.getText(), "房間順序"));
            room.setDescription(description.getText().trim()); return room;
        }

        void addRoom() { try { roomDao.insert(read()); onGameChanged(); } catch (RuntimeException e) { showError("新增房間失敗", e); } }
        void updateRoom() { if (selectedId <= 0) { warnSelect("房間"); return; } try { roomDao.update(read()); onGameChanged(); } catch (RuntimeException e) { showError("修改房間失敗", e); } }
        void deleteRoom() { Room room = roomDao.selectByRoomNo(selectedId); if (room == null) { warnSelect("房間"); return; } if (!confirmDelete("房間", room.getRoomName())) return; try { roomDao.delete(selectedId); onGameChanged(); } catch (RuntimeException e) { showError("刪除房間失敗", e); } }
        @Override void clearForm() { clearSelection(); if (name != null) name.setText(""); if (order != null) order.setText("0"); if (description != null) description.setText(""); }
    }

    private class PuzzlePanel extends FilteredPanel {
        private static final long serialVersionUID = 1L;
        private JComboBox<RoomChoice> roomCombo;
        private JTextField name, answer, order;
        private JTextArea hint;

        PuzzlePanel() { super(new Object[] {"編號", "房間", "謎題名稱", "答案", "順序", "提示"}, "謎題資料"); }

        @Override void createFilteredForm(String title) {
            JPanel form = createFormPanel(title);
            roomCombo = new JComboBox<>(); roomCombo.setBounds(20, 73, 380, 31);
            name = formField(133); answer = formField(196); order = formField(259); hint = formArea(330, 88);
            form.add(formLabel("所屬房間（可不指定）", 50)); form.add(roomCombo);
            form.add(formLabel("謎題名稱", 110)); form.add(name);
            form.add(formLabel("正確答案", 173)); form.add(answer);
            form.add(formLabel("謎題順序", 236)); form.add(order);
            form.add(formLabel("提示內容", 307)); form.add(hint);
            addCrudButtons(form, this::addPuzzle, this::updatePuzzle, this::deletePuzzle, this::clearForm);
        }

        @Override void onGameChanged() {
            if (roomCombo != null) {
                roomCombo.removeAllItems(); roomCombo.addItem(new RoomChoice(null, "不指定房間"));
                int no = selectedGameNo(gameCombo);
                if (no > 0) for (Room room : roomDao.selectByGameNo(no)) roomCombo.addItem(new RoomChoice(room.getRoomNo(), room.getRoomName()));
            }
            super.onGameChanged();
        }

        @Override void loadRows() {
            model.setRowCount(0);
            for (Puzzle puzzle : puzzleDao.selectByGameNo(selectedGameNo(gameCombo))) {
                Room room = puzzle.getRoomNo() == null ? null : roomDao.selectByRoomNo(puzzle.getRoomNo());
                model.addRow(new Object[] {puzzle.getPuzzleNo(), room == null ? "-" : room.getRoomName(), puzzle.getPuzzleName(), puzzle.getCorrectAnswer(), puzzle.getPuzzleOrder(), puzzle.getHint()});
            }
        }

        @Override void selectEntity(int id) {
            Puzzle puzzle = puzzleDao.selectByPuzzleNo(id); if (puzzle == null) return;
            name.setText(puzzle.getPuzzleName()); answer.setText(puzzle.getCorrectAnswer()); order.setText(String.valueOf(puzzle.getPuzzleOrder())); hint.setText(puzzle.getHint());
            for (int i = 0; i < roomCombo.getItemCount(); i++) {
                RoomChoice choice = roomCombo.getItemAt(i);
                if ((choice.roomNo == null && puzzle.getRoomNo() == null) || (choice.roomNo != null && choice.roomNo.equals(puzzle.getRoomNo()))) { roomCombo.setSelectedIndex(i); break; }
            }
        }

        Puzzle read() {
            Puzzle puzzle = new Puzzle(); puzzle.setPuzzleNo(selectedId); puzzle.setGameNo(gameNo());
            RoomChoice room = (RoomChoice) roomCombo.getSelectedItem(); puzzle.setRoomNo(room == null ? null : room.roomNo);
            puzzle.setPuzzleName(required(name.getText(), "謎題名稱")); puzzle.setCorrectAnswer(required(answer.getText(), "正確答案"));
            puzzle.setPuzzleOrder(positiveInt(order.getText(), "謎題順序")); puzzle.setHint(hint.getText().trim()); return puzzle;
        }

        void addPuzzle() { try { puzzleDao.insert(read()); onGameChanged(); } catch (RuntimeException e) { showError("新增謎題失敗", e); } }
        void updatePuzzle() { if (selectedId <= 0) { warnSelect("謎題"); return; } try { puzzleDao.update(read()); onGameChanged(); } catch (RuntimeException e) { showError("修改謎題失敗", e); } }
        void deletePuzzle() { Puzzle p = puzzleDao.selectByPuzzleNo(selectedId); if (p == null) { warnSelect("謎題"); return; } if (!confirmDelete("謎題", p.getPuzzleName())) return; try { puzzleDao.delete(selectedId); onGameChanged(); } catch (RuntimeException e) { showError("刪除謎題失敗", e); } }
        @Override void clearForm() { clearSelection(); if (roomCombo != null && roomCombo.getItemCount() > 0) roomCombo.setSelectedIndex(0); if (name != null) name.setText(""); if (answer != null) answer.setText(""); if (order != null) order.setText("0"); if (hint != null) hint.setText(""); }
    }

    private class ItemPanel extends FilteredPanel {
        private static final long serialVersionUID = 1L;
        private JTextField name, type;
        private JTextArea description;

        ItemPanel() { super(new Object[] {"編號", "道具名稱", "類型", "描述"}, "道具資料"); }
        @Override void createFilteredForm(String title) {
            JPanel form = createFormPanel(title); name = formField(82); type = formField(154); description = formArea(234, 150);
            form.add(formLabel("道具名稱", 59)); form.add(name); form.add(formLabel("道具類型", 131)); form.add(type); form.add(formLabel("道具描述", 211)); form.add(description);
            addCrudButtons(form, this::addItem, this::updateItem, this::deleteItem, this::clearForm);
        }
        @Override void loadRows() { model.setRowCount(0); for (Item item : itemDao.selectByGameNo(selectedGameNo(gameCombo))) model.addRow(new Object[] {item.getItemNo(), item.getItemName(), item.getItemType(), item.getDescription()}); }
        @Override void selectEntity(int id) { Item item = itemDao.selectByItemNo(id); if (item == null) return; name.setText(item.getItemName()); type.setText(item.getItemType()); description.setText(item.getDescription()); }
        Item read() { Item item = new Item(); item.setItemNo(selectedId); item.setGameNo(gameNo()); item.setItemName(required(name.getText(), "道具名稱")); item.setItemType(required(type.getText(), "道具類型")); item.setDescription(description.getText().trim()); return item; }
        void addItem() { try { itemDao.insert(read()); onGameChanged(); } catch (RuntimeException e) { showError("新增道具失敗", e); } }
        void updateItem() { if (selectedId <= 0) { warnSelect("道具"); return; } try { itemDao.update(read()); onGameChanged(); } catch (RuntimeException e) { showError("修改道具失敗", e); } }
        void deleteItem() { Item item = itemDao.selectByItemNo(selectedId); if (item == null) { warnSelect("道具"); return; } if (!confirmDelete("道具", item.getItemName())) return; try { itemDao.delete(selectedId); onGameChanged(); } catch (RuntimeException e) { showError("刪除道具失敗", e); } }
        @Override void clearForm() { clearSelection(); if (name != null) name.setText(""); if (type != null) type.setText(""); if (description != null) description.setText(""); }
    }

    private class EndingPanel extends FilteredPanel {
        private static final long serialVersionUID = 1L;
        private JTextField name, type;
        private JTextArea description;

        EndingPanel() { super(new Object[] {"編號", "結局名稱", "類型", "描述"}, "結局資料"); }
        @Override void createFilteredForm(String title) {
            JPanel form = createFormPanel(title); name = formField(82); type = formField(154); description = formArea(234, 150);
            form.add(formLabel("結局名稱", 59)); form.add(name); form.add(formLabel("結局類型", 131)); form.add(type); form.add(formLabel("結局描述", 211)); form.add(description);
            addCrudButtons(form, this::addEnding, this::updateEnding, this::deleteEnding, this::clearForm);
        }
        @Override void loadRows() { model.setRowCount(0); for (Ending ending : endingDao.selectByGameNo(selectedGameNo(gameCombo))) model.addRow(new Object[] {ending.getEndingNo(), ending.getEndingName(), ending.getEndingType(), ending.getDescription()}); }
        @Override void selectEntity(int id) { Ending ending = endingDao.selectByEndingNo(id); if (ending == null) return; name.setText(ending.getEndingName()); type.setText(ending.getEndingType()); description.setText(ending.getDescription()); }
        Ending read() { Ending ending = new Ending(); ending.setEndingNo(selectedId); ending.setGameNo(gameNo()); ending.setEndingName(required(name.getText(), "結局名稱")); ending.setEndingType(required(type.getText(), "結局類型")); ending.setDescription(description.getText().trim()); return ending; }
        void addEnding() { try { endingDao.insert(read()); onGameChanged(); } catch (RuntimeException e) { showError("新增結局失敗", e); } }
        void updateEnding() { if (selectedId <= 0) { warnSelect("結局"); return; } try { endingDao.update(read()); onGameChanged(); } catch (RuntimeException e) { showError("修改結局失敗", e); } }
        void deleteEnding() { Ending ending = endingDao.selectByEndingNo(selectedId); if (ending == null) { warnSelect("結局"); return; } if (!confirmDelete("結局", ending.getEndingName())) return; try { endingDao.delete(selectedId); onGameChanged(); } catch (RuntimeException e) { showError("刪除結局失敗", e); } }
        @Override void clearForm() { clearSelection(); if (name != null) name.setText(""); if (type != null) type.setText(""); if (description != null) description.setText(""); }
    }

    private static class GameChoice {
        final int gameNo; final String name;
        GameChoice(int gameNo, String name) { this.gameNo = gameNo; this.name = name; }
        @Override public String toString() { return gameNo + " - " + name; }
    }

    private static class RoomChoice {
        final Integer roomNo; final String name;
        RoomChoice(Integer roomNo, String name) { this.roomNo = roomNo; this.name = name; }
        @Override public String toString() { return roomNo == null ? name : roomNo + " - " + name; }
    }
}
