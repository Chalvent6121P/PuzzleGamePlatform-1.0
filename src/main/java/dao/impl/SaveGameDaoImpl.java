package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import dao.SaveGameDao;
import entity.SaveGame;
import util.DbConnection;

public class SaveGameDaoImpl implements SaveGameDao {

    @Override
    public void insert(SaveGame saveGame) {
        String sql = "INSERT INTO save_game "
                + "(record_no, player_no, game_no, save_name, save_data) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, saveGame.getRecordNo());
            ps.setInt(2, saveGame.getPlayerNo());
            ps.setInt(3, saveGame.getGameNo());
            ps.setString(4, saveGame.getSaveName());
            ps.setString(5, saveGame.getSaveData());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("新增存檔失敗。", e);
        }
    }

    @Override
    public void delete(int saveNo) {
        String sql = "DELETE FROM save_game WHERE save_no=?";

        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, saveNo);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("刪除存檔失敗。", e);
        }
    }

    @Override
    public SaveGame selectBySaveNo(int saveNo) {
        String sql = "SELECT * FROM save_game WHERE save_no=?";

        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, saveNo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapSaveGame(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("查詢存檔失敗。", e);
        }
    }

    @Override
    public List<SaveGame> selectByRecordNo(int recordNo) {
        String sql = "SELECT * FROM save_game "
                + "WHERE record_no=? ORDER BY save_time DESC";

        List<SaveGame> list = new ArrayList<>();

        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, recordNo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapSaveGame(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("查詢遊戲存檔失敗。", e);
        }

        return list;
    }

    private SaveGame mapSaveGame(ResultSet rs) throws SQLException {
        SaveGame saveGame = new SaveGame();
        saveGame.setSaveNo(rs.getInt("save_no"));
        saveGame.setRecordNo(rs.getInt("record_no"));
        saveGame.setPlayerNo(rs.getInt("player_no"));
        saveGame.setGameNo(rs.getInt("game_no"));
        saveGame.setSaveName(rs.getString("save_name"));
        saveGame.setSaveData(rs.getString("save_data"));

        Timestamp saveTime = rs.getTimestamp("save_time");
        if (saveTime != null) {
            saveGame.setSaveTime(saveTime.toLocalDateTime());
        }

        return saveGame;
    }
}
