package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import dao.ReportDao;
import entity.GameRecordReportRow;
import entity.ReportSummary;
import util.DbConnection;

public class ReportDaoImpl implements ReportDao {

    @Override
    public List<GameRecordReportRow> selectAllGameRecords() {
        String sql = "SELECT record_no, player_no, player_name, account, "
                + "game_no, game_name, room_name, puzzle_name, ending_name, "
                + "progress_status, result_status, current_step, start_time, end_time "
                + "FROM vw_player_game_record ORDER BY start_time DESC, record_no DESC";
        List<GameRecordReportRow> rows = new ArrayList<>();

        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rows.add(mapRow(rs));
            }
            return rows;
        } catch (SQLException e) {
            throw new RuntimeException("讀取遊戲紀錄報表失敗，請確認 vw_player_game_record 已建立。", e);
        }
    }

    @Override
    public ReportSummary selectSummary() {
        String sql = "SELECT "
                + "(SELECT COUNT(*) FROM player) AS player_count, "
                + "(SELECT COUNT(*) FROM player WHERE status='ACTIVE') AS active_player_count, "
                + "(SELECT COUNT(*) FROM game) AS game_count, "
                + "(SELECT COUNT(*) FROM game WHERE is_active=1) AS active_game_count, "
                + "(SELECT COUNT(*) FROM player_game_record) AS record_count, "
                + "(SELECT COUNT(*) FROM player_game_record WHERE progress_status='完成') AS completed_count, "
                + "(SELECT COUNT(*) FROM player_game_record WHERE result_status='成功') AS success_count";

        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            ReportSummary summary = new ReportSummary();
            if (rs.next()) {
                summary.setPlayerCount(rs.getInt("player_count"));
                summary.setActivePlayerCount(rs.getInt("active_player_count"));
                summary.setGameCount(rs.getInt("game_count"));
                summary.setActiveGameCount(rs.getInt("active_game_count"));
                summary.setRecordCount(rs.getInt("record_count"));
                summary.setCompletedCount(rs.getInt("completed_count"));
                summary.setSuccessCount(rs.getInt("success_count"));
            }
            return summary;
        } catch (SQLException e) {
            throw new RuntimeException("讀取平台統計資料失敗。", e);
        }
    }

    private GameRecordReportRow mapRow(ResultSet rs) throws SQLException {
        GameRecordReportRow row = new GameRecordReportRow();
        row.setRecordNo(rs.getInt("record_no"));
        row.setPlayerNo(rs.getInt("player_no"));
        row.setPlayerName(rs.getString("player_name"));
        row.setAccount(rs.getString("account"));
        row.setGameNo(rs.getInt("game_no"));
        row.setGameName(rs.getString("game_name"));
        row.setRoomName(rs.getString("room_name"));
        row.setPuzzleName(rs.getString("puzzle_name"));
        row.setEndingName(rs.getString("ending_name"));
        row.setProgressStatus(rs.getString("progress_status"));
        row.setResultStatus(rs.getString("result_status"));
        row.setCurrentStep(rs.getString("current_step"));
        Timestamp start = rs.getTimestamp("start_time");
        Timestamp end = rs.getTimestamp("end_time");
        if (start != null) row.setStartTime(start.toLocalDateTime());
        if (end != null) row.setEndTime(end.toLocalDateTime());
        return row;
    }
}
