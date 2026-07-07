package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import dao.PuzzleRecordDao;
import entity.PuzzleRecord;
import util.DbConnection;

public class PuzzleRecordDaoImpl implements PuzzleRecordDao{

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void insert(PuzzleRecord puzzleRecord) {
		String sql="insert into puzzle_record(record_no,puzzle_no,input_answer,is_correct) "
				+ "values(?,?,?,?)";
		try(Connection conn=DbConnection.getDb();
			PreparedStatement ps=conn.prepareStatement(sql))
		{
			ps.setInt(1, puzzleRecord.getRecordNo());
			ps.setInt(2, puzzleRecord.getPuzzleNo());
			ps.setString(3, puzzleRecord.getAnswer());
			ps.setBoolean(4, puzzleRecord.isCorrect());
			
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public boolean hasSolved(int recordNo, int puzzleNo) {
		String sql="select * from puzzle_record where record_no=? and puzzle_no=? and is_correct=true";
		boolean result=false;
		try(Connection conn=DbConnection.getDb();
			PreparedStatement ps=conn.prepareStatement(sql))
		{
			ps.setInt(1, recordNo);
			ps.setInt(2, puzzleNo);
			
			try(ResultSet rs=ps.executeQuery())
			{
				if(rs.next()) 
				{
					result=true;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public List<PuzzleRecord> selectByRecordNo(int recordNo) {
		String sql="select * from puzzle_record where record_no=? order by answer_time asc";
		List<PuzzleRecord> list=new ArrayList<>();
		try(Connection conn=DbConnection.getDb();
			PreparedStatement ps=conn.prepareStatement(sql))
		{
			ps.setInt(1, recordNo);
			
			try(ResultSet rs=ps.executeQuery())
			{
				while(rs.next()) 
				{
					list.add(mapPuzzleRecord(rs));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<PuzzleRecord> selectByPuzzleNo(int puzzleNo) {
		String sql="select * from puzzle_record where puzzle_no=? order by answer_time desc";
		List<PuzzleRecord> list=new ArrayList<>();
		try(Connection conn=DbConnection.getDb();
			PreparedStatement ps=conn.prepareStatement(sql))
		{
			ps.setInt(1, puzzleNo);
			try(ResultSet rs=ps.executeQuery())
			{
				while(rs.next()) 
				{
					list.add(mapPuzzleRecord(rs));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	 private PuzzleRecord mapPuzzleRecord(ResultSet rs) throws SQLException {
	        PuzzleRecord puzzleRecord = new PuzzleRecord();

	        puzzleRecord.setPuzzleRecordNo(rs.getInt("puzzle_record_no"));
	        puzzleRecord.setRecordNo(rs.getInt("record_no"));
	        puzzleRecord.setPuzzleNo(rs.getInt("puzzle_no"));
	        puzzleRecord.setAnswer(rs.getString("input_answer"));
	        puzzleRecord.setCorrect(rs.getBoolean("is_correct"));

	        Timestamp answerTime = rs.getTimestamp("answer_time");

	        if (answerTime != null) {
	            puzzleRecord.setAnswerTime(answerTime.toLocalDateTime());
	        }

	        return puzzleRecord;
	    }
	

}
