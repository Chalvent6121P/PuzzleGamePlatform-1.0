package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import dao.PlayerDao;
import entity.Player;
import util.DbConnection;

public class PlayerDaoImpl implements PlayerDao {

    @Override
    public void insert(Player player) {
        String sql = "INSERT INTO player "
                + "(player_name, account, password, role, status) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, player.getPlayerName());
            ps.setString(2, player.getAccount());
            ps.setString(3, player.getPassword());
            ps.setString(4, normalizeRole(player.getRole()));
            ps.setString(5, normalizeStatus(player.getStatus()));
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    player.setPlayerNo(keys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("新增玩家失敗。", e);
        }
    }

    @Override
    public Player selectByAccount(String account) {
        String sql = "SELECT * FROM player WHERE account=?";
        return selectOne(sql, account);
    }

    @Override
    public Player selectByPlayerNo(int playerNo) {
        String sql = "SELECT * FROM player WHERE player_no=?";

        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, playerNo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapPlayer(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("依編號查詢玩家失敗。", e);
        }
    }

    @Override
    public Player login(String account, String password) {
        String sql = "SELECT * FROM player "
                + "WHERE account=? AND password=? AND status='ACTIVE'";

        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, account);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapPlayer(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("登入查詢失敗。", e);
        }
    }

    @Override
    public List<Player> selectAll() {
        String sql = "SELECT * FROM player ORDER BY player_no";
        List<Player> players = new ArrayList<>();

        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                players.add(mapPlayer(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("查詢所有玩家失敗。", e);
        }

        return players;
    }

    @Override
    public void update(Player player) {
        String sql = "UPDATE player SET player_name=?, account=?, "
                + "password=?, role=?, status=? WHERE player_no=?";

        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, player.getPlayerName());
            ps.setString(2, player.getAccount());
            ps.setString(3, player.getPassword());
            ps.setString(4, normalizeRole(player.getRole()));
            ps.setString(5, normalizeStatus(player.getStatus()));
            ps.setInt(6, player.getPlayerNo());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("更新玩家失敗。", e);
        }
    }

    @Override
    public boolean deleteByPlayerNo(int playerNo) {
        String[] cleanupSql = {
                "DELETE FROM save_game WHERE player_no=?",
                "DELETE FROM inventory WHERE record_no IN "
                        + "(SELECT record_no FROM player_game_record WHERE player_no=?)",
                "DELETE FROM puzzle_record WHERE record_no IN "
                        + "(SELECT record_no FROM player_game_record WHERE player_no=?)",
                "DELETE FROM player_achievement WHERE player_no=?",
                "DELETE FROM player_game_record WHERE player_no=?",
                "DELETE FROM player WHERE player_no=?"
        };

        try (Connection conn = DbConnection.getDb()) {
            conn.setAutoCommit(false);
            try {
                int deleted = 0;
                for (int i = 0; i < cleanupSql.length; i++) {
                    try (PreparedStatement ps = conn.prepareStatement(cleanupSql[i])) {
                        ps.setInt(1, playerNo);
                        int affected = ps.executeUpdate();
                        if (i == cleanupSql.length - 1) {
                            deleted = affected;
                        }
                    }
                }
                conn.commit();
                return deleted > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("永久刪除玩家與其關聯紀錄失敗。", e);
        }
    }

    @Override
    public void updateLastLogin(int playerNo) {
        String sql = "UPDATE player SET last_login_time=NOW() "
                + "WHERE player_no=?";

        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, playerNo);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("更新最後登入時間失敗。", e);
        }
    }

    private Player selectOne(String sql, String account) {
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, account);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapPlayer(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("查詢玩家帳號失敗。", e);
        }
    }

    private Player mapPlayer(ResultSet rs) throws SQLException {
        Player player = new Player();
        player.setPlayerNo(rs.getInt("player_no"));
        player.setPlayerName(rs.getString("player_name"));
        player.setAccount(rs.getString("account"));
        player.setPassword(rs.getString("password"));
        player.setRole(rs.getString("role"));
        player.setStatus(rs.getString("status"));

        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            player.setCreateTime(createTime.toLocalDateTime());
        }

        Timestamp lastLoginTime = rs.getTimestamp("last_login_time");
        if (lastLoginTime != null) {
            player.setLastLoginTime(lastLoginTime.toLocalDateTime());
        }

        return player;
    }

    private String normalizeRole(String role) {
        return Player.ROLE_ADMIN.equalsIgnoreCase(role)
                ? Player.ROLE_ADMIN : Player.ROLE_PLAYER;
    }

    private String normalizeStatus(String status) {
        return Player.STATUS_INACTIVE.equalsIgnoreCase(status)
                ? Player.STATUS_INACTIVE : Player.STATUS_ACTIVE;
    }
}
