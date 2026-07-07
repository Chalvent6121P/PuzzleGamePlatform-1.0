package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import dao.InventoryDao;
import entity.Inventory;
import entity.Item;
import util.DbConnection;

public class InventoryDaoImpl implements InventoryDao {

    @Override
    public void insert(int recordNo, int itemNo) {
        String sql = "INSERT IGNORE INTO inventory(record_no, item_no) VALUES(?, ?)";
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, recordNo);
            ps.setInt(2, itemNo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("新增背包道具失敗。", e);
        }
    }

    @Override
    public void delete(int inventoryNo) {
        String sql = "DELETE FROM inventory WHERE inventory_no=?";
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, inventoryNo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("移除背包道具失敗。", e);
        }
    }

    @Override
    public boolean hasItem(int recordNo, int itemNo) {
        String sql = "SELECT 1 FROM inventory WHERE record_no=? AND item_no=?";
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, recordNo);
            ps.setInt(2, itemNo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("檢查背包道具失敗。", e);
        }
    }

    @Override
    public List<Inventory> selectByRecordNo(int recordNo) {
        String sql = "SELECT * FROM inventory WHERE record_no=? ORDER BY get_time ASC, inventory_no ASC";
        List<Inventory> list = new ArrayList<>();
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, recordNo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapInventory(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("查詢背包紀錄失敗。", e);
        }
    }

    @Override
    public List<Item> selectItemsByRecordNo(int recordNo) {
        String sql = "SELECT i.* FROM inventory inv "
                + "JOIN item i ON i.item_no=inv.item_no "
                + "WHERE inv.record_no=? "
                + "ORDER BY inv.get_time ASC, inv.inventory_no ASC";
        List<Item> list = new ArrayList<>();
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, recordNo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Item item = new Item();
                    item.setItemNo(rs.getInt("item_no"));
                    item.setGameNo(rs.getInt("game_no"));
                    item.setItemName(rs.getString("item_name"));
                    item.setItemType(rs.getString("item_type"));
                    item.setDescription(rs.getString("description"));
                    list.add(item);
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("查詢背包道具資訊失敗。", e);
        }
    }

    private Inventory mapInventory(ResultSet rs) throws SQLException {
        Inventory inventory = new Inventory();
        inventory.setInventoryNo(rs.getInt("inventory_no"));
        inventory.setRecordNo(rs.getInt("record_no"));
        inventory.setItemNo(rs.getInt("item_no"));
        Timestamp getTime = rs.getTimestamp("get_time");
        if (getTime != null) {
            inventory.setGetTime(getTime.toLocalDateTime());
        }
        return inventory;
    }
}
