package controller.games.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import entity.Item;
import util.SoundPlayer;

public final class BackpackDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final JTextArea detailArea = new JTextArea();

    public BackpackDialog(Frame owner, List<Item> items, Color accentColor) {
        super(owner, "背包", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(640, 430);
        setMinimumSize(new Dimension(520, 360));
        setLocationRelativeTo(owner);

        Color accent = accentColor == null ? new Color(190, 160, 100) : accentColor;
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(new Color(20, 22, 28));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        setContentPane(root);

        DefaultListModel<Item> model = new DefaultListModel<>();
        if (items != null) {
            for (Item item : items) {
                model.addElement(item);
            }
        }

        JList<Item> itemList = new JList<>(model);
        itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemList.setBackground(new Color(29, 32, 40));
        itemList.setForeground(Color.WHITE);
        itemList.setSelectionBackground(accent.darker());
        itemList.setSelectionForeground(Color.WHITE);
        itemList.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 15));
        itemList.setBorder(new EmptyBorder(6, 6, 6, 6));
        itemList.setCellRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public java.awt.Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                if (value instanceof Item) {
                    Item item = (Item) value;
                    String type = item.getItemType() == null
                            ? "道具" : item.getItemType();
                    setText(item.getItemName() + " 〔" + type + "〕");
                }
                setBorder(new EmptyBorder(8, 8, 8, 8));
                return this;
            }
        });

        detailArea.setEditable(false);
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);
        detailArea.setBackground(new Color(15, 17, 22));
        detailArea.setForeground(new Color(235, 232, 222));
        detailArea.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 16));
        detailArea.setBorder(new EmptyBorder(18, 18, 18, 18));

        JScrollPane listScroll = new JScrollPane(itemList);
        listScroll.setBorder(new LineBorder(new Color(70, 74, 86), 1));
        JScrollPane detailScroll = new JScrollPane(detailArea);
        detailScroll.setBorder(new LineBorder(accent.darker(), 1));

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, listScroll, detailScroll);
        split.setResizeWeight(0.38);
        split.setDividerLocation(220);
        split.setBorder(null);
        root.add(split, BorderLayout.CENTER);

        JButton closeButton = new JButton("關閉背包");
        closeButton.setFont(new Font("Microsoft JhengHei", Font.BOLD, 14));
        closeButton.setBackground(accent);
        closeButton.setForeground(new Color(25, 25, 28));
        closeButton.addActionListener(event -> dispose());
        root.add(closeButton, BorderLayout.SOUTH);

        itemList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                showItem(itemList.getSelectedValue());
            }
        });

        if (model.isEmpty()) {
            detailArea.setText(
                    "背包目前是空的。\n\n"
                  + "解開謎題、打開櫃子或取得紙條後，"
                  + "道具會自動收藏在這裡。"
            );
        } else {
            itemList.setSelectedIndex(0);
        }
    }

    /**
     * Microsoft JhengHei on some Windows installations does not contain
     * Unicode subscript numerals, which causes formulas such as P₁V₁=P₂V₂
     * to appear as square boxes. Convert those characters to universally
     * supported plain digits before displaying the item description.
     */
    private static String normalizeDisplayText(String text) {
        if (text == null) {
            return "";
        }

        return text
                .replace('₀', '0')
                .replace('₁', '1')
                .replace('₂', '2')
                .replace('₃', '3')
                .replace('₄', '4')
                .replace('₅', '5')
                .replace('₆', '6')
                .replace('₇', '7')
                .replace('₈', '8')
                .replace('₉', '9')
                .replace("P1V1=P2V2", "P1 × V1 = P2 × V2");
    }

    private void showItem(Item item) {
        if (item == null) {
            return;
        }

        String type = item.getItemType() == null ? "道具" : item.getItemType();
        String description = item.getDescription() == null
                ? "沒有更多資訊。"
                : normalizeDisplayText(item.getDescription());

        boolean paperLike = type.contains("紙")
                || type.contains("提示")
                || type.contains("病歷")
                || type.contains("紀錄");

        if (paperLike) {
            SoundPlayer.play("/sounds/paper_open.wav");
            detailArea.setText(
                    "【" + item.getItemName() + "】\n\n"
                  + "──── 紙條內容 ────\n\n"
                  + description
                  + "\n\n────────────────"
            );
        } else {
            SoundPlayer.play("/sounds/item_select.wav");
            detailArea.setText(
                    "【" + item.getItemName() + "】\n"
                  + "類型：" + type + "\n\n"
                  + description
            );
        }
        detailArea.setCaretPosition(0);
    }
}
