package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import dao.PuzzleDao;
import entity.Puzzle;
import util.DbConnection;

public class PuzzleDaoImpl implements PuzzleDao {

    @Override
    public void insert(Puzzle puzzle) {
        String sql = "INSERT INTO puzzle(game_no, room_no, puzzle_name, correct_answer, hint, puzzle_order) VALUES(?, ?, ?, ?, ?, ?)";
        executeWrite(sql, puzzle, false);
    }

    @Override
    public void update(Puzzle puzzle) {
        String sql = "UPDATE puzzle SET game_no=?, room_no=?, puzzle_name=?, correct_answer=?, hint=?, puzzle_order=? WHERE puzzle_no=?";
        executeWrite(sql, puzzle, true);
    }

    @Override
    public void delete(int puzzleNo) {
        String sql = "DELETE FROM puzzle WHERE puzzle_no=?";
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, puzzleNo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("刪除謎題失敗；謎題可能已有作答或遊戲進度紀錄。", e);
        }
    }

    @Override
    public Puzzle selectByPuzzleNo(int puzzleNo) {
        String sql = "SELECT * FROM puzzle WHERE puzzle_no=?";
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, puzzleNo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapPuzzle(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("查詢謎題失敗。", e);
        }
    }

    @Override
    public List<Puzzle> selectByGameNo(int gameNo) {
        return selectList("SELECT * FROM puzzle WHERE game_no=? ORDER BY puzzle_order, puzzle_no", gameNo);
    }

    @Override
    public List<Puzzle> selectByRoomNo(int roomNo) {
        return selectList("SELECT * FROM puzzle WHERE room_no=? ORDER BY puzzle_order, puzzle_no", roomNo);
    }

    private List<Puzzle> selectList(String sql, int id) {
        List<Puzzle> list = new ArrayList<>();
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapPuzzle(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("查詢謎題清單失敗。", e);
        }
    }

    private void executeWrite(String sql, Puzzle puzzle, boolean update) {
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, puzzle.getGameNo());
            if (puzzle.getRoomNo() == null) ps.setNull(2, Types.INTEGER);
            else ps.setInt(2, puzzle.getRoomNo());
            ps.setString(3, puzzle.getPuzzleName());
            ps.setString(4, puzzle.getCorrectAnswer());
            ps.setString(5, puzzle.getHint());
            ps.setInt(6, puzzle.getPuzzleOrder());
            if (update) ps.setInt(7, puzzle.getPuzzleNo());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(update ? "更新謎題失敗。" : "新增謎題失敗。", e);
        }
    }

    private Puzzle mapPuzzle(ResultSet rs) throws SQLException {
        Puzzle puzzle = new Puzzle();
        puzzle.setPuzzleNo(rs.getInt("puzzle_no"));
        puzzle.setGameNo(rs.getInt("game_no"));
        int roomNo = rs.getInt("room_no");
        puzzle.setRoomNo(rs.wasNull() ? null : roomNo);
        puzzle.setPuzzleName(rs.getString("puzzle_name"));
        puzzle.setCorrectAnswer(rs.getString("correct_answer"));
        puzzle.setHint(rs.getString("hint"));
        puzzle.setPuzzleOrder(rs.getInt("puzzle_order"));
        return puzzle;
    }
}
