package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.ItemDao;
import entity.Item;
import util.DbConnection;

public class ItemDaoImpl implements ItemDao {

    @Override
    public void insert(Item item) {
        executeWrite("INSERT INTO item(game_no, item_name, item_type, description) VALUES(?, ?, ?, ?)", item, false);
    }

    @Override
    public void update(Item item) {
        executeWrite("UPDATE item SET game_no=?, item_name=?, item_type=?, description=? WHERE item_no=?", item, true);
    }

    @Override
    public void delete(int itemNo) {
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM item WHERE item_no=?")) {
            ps.setInt(1, itemNo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("刪除道具失敗；道具可能已存在玩家背包紀錄。", e);
        }
    }

    @Override
    public Item selectByItemNo(int itemNo) {
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM item WHERE item_no=?")) {
            ps.setInt(1, itemNo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapItem(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("查詢道具失敗。", e);
        }
    }

    @Override
    public List<Item> selectByGameNo(int gameNo) {
        List<Item> list = new ArrayList<>();
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM item WHERE game_no=? ORDER BY item_no")) {
            ps.setInt(1, gameNo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapItem(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("查詢遊戲道具失敗。", e);
        }
    }

    private void executeWrite(String sql, Item item, boolean update) {
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, item.getGameNo());
            ps.setString(2, item.getItemName());
            ps.setString(3, item.getItemType());
            ps.setString(4, item.getDescription());
            if (update) ps.setInt(5, item.getItemNo());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(update ? "更新道具失敗。" : "新增道具失敗。", e);
        }
    }

    private Item mapItem(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setItemNo(rs.getInt("item_no"));
        item.setGameNo(rs.getInt("game_no"));
        item.setItemName(rs.getString("item_name"));
        item.setItemType(rs.getString("item_type"));
        item.setDescription(rs.getString("description"));
        return item;
    }
}
