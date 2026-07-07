package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.RoomDao;
import entity.Room;
import util.DbConnection;

public class RoomDaoImpl implements RoomDao {

    @Override
    public void insert(Room room) {
        String sql = "INSERT INTO room(game_no, room_name, description, room_order) VALUES(?, ?, ?, ?)";
        executeWrite(sql, room, false);
    }

    @Override
    public void update(Room room) {
        String sql = "UPDATE room SET game_no=?, room_name=?, description=?, room_order=? WHERE room_no=?";
        executeWrite(sql, room, true);
    }

    @Override
    public void delete(int roomNo) {
        String sql = "DELETE FROM room WHERE room_no=?";
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomNo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("刪除房間失敗；房間可能仍被謎題或遊戲紀錄使用。", e);
        }
    }

    @Override
    public Room selectByRoomNo(int roomNo) {
        String sql = "SELECT * FROM room WHERE room_no=?";
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomNo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRoom(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("查詢房間失敗。", e);
        }
    }

    @Override
    public List<Room> selectByGameNo(int gameNo) {
        String sql = "SELECT * FROM room WHERE game_no=? ORDER BY room_order, room_no";
        List<Room> list = new ArrayList<>();
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, gameNo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRoom(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("查詢遊戲房間失敗。", e);
        }
    }

    private void executeWrite(String sql, Room room, boolean update) {
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, room.getGameNo());
            ps.setString(2, room.getRoomName());
            ps.setString(3, room.getDescription());
            ps.setInt(4, room.getRoomOrder());
            if (update) ps.setInt(5, room.getRoomNo());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(update ? "更新房間失敗。" : "新增房間失敗。", e);
        }
    }

    private Room mapRoom(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setRoomNo(rs.getInt("room_no"));
        room.setGameNo(rs.getInt("game_no"));
        room.setRoomName(rs.getString("room_name"));
        room.setDescription(rs.getString("description"));
        room.setRoomOrder(rs.getInt("room_order"));
        return room;
    }
}
