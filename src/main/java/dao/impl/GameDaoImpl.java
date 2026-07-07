package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import dao.GameDao;
import entity.Game;
import util.DbConnection;

public class GameDaoImpl implements GameDao {

    @Override
    public List<Game> selectAll() {
        String sql = "SELECT * FROM game ORDER BY game_no";
        List<Game> games = new ArrayList<>();

        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                games.add(mapGame(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("查詢遊戲清單失敗。", e);
        }

        return games;
    }

    @Override
    public Game selectById(int gameNo) {
        String sql = "SELECT * FROM game WHERE game_no=?";

        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, gameNo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapGame(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("查詢遊戲失敗。", e);
        }
    }

    @Override
    public void insert(Game game) {
        String sql = "INSERT INTO game "
                + "(game_name, difficulty, description, is_active, cover_image_path) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            setGameParameters(ps, game);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    game.setGameNo(keys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("新增遊戲失敗。", e);
        }
    }

    @Override
    public void update(Game game) {
        String sql = "UPDATE game SET game_name=?, difficulty=?, "
                + "description=?, is_active=?, cover_image_path=? "
                + "WHERE game_no=?";

        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setGameParameters(ps, game);
            ps.setInt(6, game.getGameNo());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("更新遊戲失敗。", e);
        }
    }

    @Override
    public boolean deleteById(int gameNo) {
        String sql = "DELETE FROM game WHERE game_no=?";

        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, gameNo);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "刪除遊戲失敗；遊戲可能仍有關聯資料。", e);
        }
    }

    private void setGameParameters(
            PreparedStatement ps, Game game) throws SQLException {
        ps.setString(1, game.getGameName());
        ps.setString(2, game.getDifficulty());
        ps.setString(3, game.getDescription());
        ps.setBoolean(4, game.isActive());
        ps.setString(5, game.getCoverImagePath());
    }

    private Game mapGame(ResultSet rs) throws SQLException {
        Game game = new Game();
        game.setGameNo(rs.getInt("game_no"));
        game.setGameName(rs.getString("game_name"));
        game.setDifficulty(rs.getString("difficulty"));
        game.setDescription(rs.getString("description"));
        game.setActive(rs.getBoolean("is_active"));
        game.setCoverImagePath(rs.getString("cover_image_path"));

        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            game.setCreateTime(createTime.toLocalDateTime());
        }
        return game;
    }
}
