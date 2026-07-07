package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.EndingDao;
import entity.Ending;
import util.DbConnection;

public class EndingDaoImpl implements EndingDao {

    @Override
    public void insert(Ending ending) {
        executeWrite("INSERT INTO ending(game_no, ending_name, ending_type, description) VALUES(?, ?, ?, ?)", ending, false);
    }

    @Override
    public void update(Ending ending) {
        executeWrite("UPDATE ending SET game_no=?, ending_name=?, ending_type=?, description=? WHERE ending_no=?", ending, true);
    }

    @Override
    public void delete(int endingNo) {
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM ending WHERE ending_no=?")) {
            ps.setInt(1, endingNo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("刪除結局失敗；結局可能已被遊戲紀錄引用。", e);
        }
    }

    @Override
    public Ending selectByEndingNo(int endingNo) {
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM ending WHERE ending_no=?")) {
            ps.setInt(1, endingNo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapEnding(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("查詢結局失敗。", e);
        }
    }

    @Override
    public List<Ending> selectByGameNo(int gameNo) {
        List<Ending> list = new ArrayList<>();
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM ending WHERE game_no=? ORDER BY ending_no")) {
            ps.setInt(1, gameNo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapEnding(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("查詢遊戲結局失敗。", e);
        }
    }

    private void executeWrite(String sql, Ending ending, boolean update) {
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ending.getGameNo());
            ps.setString(2, ending.getEndingName());
            ps.setString(3, ending.getEndingType());
            ps.setString(4, ending.getDescription());
            if (update) ps.setInt(5, ending.getEndingNo());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(update ? "更新結局失敗。" : "新增結局失敗。", e);
        }
    }

    private Ending mapEnding(ResultSet rs) throws SQLException {
        Ending ending = new Ending();
        ending.setEndingNo(rs.getInt("ending_no"));
        ending.setGameNo(rs.getInt("game_no"));
        ending.setEndingName(rs.getString("ending_name"));
        ending.setEndingType(rs.getString("ending_type"));
        ending.setDescription(rs.getString("description"));
        return ending;
    }
}
