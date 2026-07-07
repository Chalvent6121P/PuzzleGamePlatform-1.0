package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import dao.PlayerGameRecordDao;
import entity.PlayerGameRecord;
import util.DbConnection;

public class PlayerGameRecordDaoImpl implements PlayerGameRecordDao{

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public int insert(PlayerGameRecord record) {
		String sql="insert into player_game_record(player_no,game_no,progress_status,"
				+ "result_status,current_step) values(?,?,?,?,?)";
		int recordNo=0;
		try(Connection conn=DbConnection.getDb();
		PreparedStatement ps=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);) {
			
			ps.setInt(1, record.getPlayerNo());
			ps.setInt(2, record.getGameNo());
			ps.setString(3, record.getProgressStatus());
			ps.setString(4, record.getResultStatus());
			ps.setString(5, record.getCurrentStep());
			ps.executeUpdate();
			
			try(ResultSet rs=ps.getGeneratedKeys();)
			{
				if(rs.next()) 
				{
					recordNo=rs.getInt(1);
				}
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return recordNo;
	}

	@Override
	public void updateProgress(PlayerGameRecord record) {
		String sql = "update player_game_record set current_room_no=?, "
	            + "current_puzzle_no=?, "
	            + "progress_status=?, "
	            + "result_status=?, "
	            + "current_step=? "
	            + "where record_no=?";

	    try (Connection conn = DbConnection.getDb();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        if (record.getCurrentRoomNo() == null) {
	            ps.setNull(1, java.sql.Types.INTEGER);
	        } else {
	            ps.setInt(1, record.getCurrentRoomNo());
	        }

	        if (record.getCurrentPuzzleNo() == null) {
	            ps.setNull(2, java.sql.Types.INTEGER);
	        } else {
	            ps.setInt(2, record.getCurrentPuzzleNo());
	        }

	        ps.setString(3, record.getProgressStatus());
	        ps.setString(4, record.getResultStatus());
	        ps.setString(5, record.getCurrentStep());
	        ps.setInt(6, record.getRecordNo());

	        ps.executeUpdate();

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		
	}

	@Override
	public void finishGame(int recordNo, int endingNo, String resultStatus, String currentStep) {
		String sql="update player_game_record set ending_no=?,"
				+ "progress_status='完成',"
				+ "result_status=?,"
				+ "current_step=?,"
				+ "end_time=now() "
				+ "where record_no=?";
		try(Connection conn=DbConnection.getDb();
				PreparedStatement ps=conn.prepareStatement(sql))
		{
			ps.setInt(1, endingNo);
			ps.setString(2, resultStatus);
			ps.setString(3, currentStep);
			ps.setInt(4, recordNo);
			ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public PlayerGameRecord selectByRecordNo(int recordNo) {
		 String sql = "select*from player_game_record WHERE record_no = ?";

	        PlayerGameRecord record = null;

	        try (Connection conn = DbConnection.getDb();
	             PreparedStatement ps = conn.prepareStatement(sql)) {

	            ps.setInt(1, recordNo);

	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) {
	                    record = mapRecord(rs);
	                }
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        return record;
	}

	@Override
	public List<PlayerGameRecord> selectByPlayerNo(int playerNo) {
		String sql="select*from player_game_record where player_no=? order by start_time desc";
		List<PlayerGameRecord> list=new ArrayList<>();
		  try (Connection conn = DbConnection.getDb();
		             PreparedStatement ps = conn.prepareStatement(sql))
		  {

		            ps.setInt(1, playerNo);

		            try (ResultSet rs = ps.executeQuery())
		            {
		                while (rs.next())
		                {
		                    list.add(mapRecord(rs));
		                }
		            }

		   }
		  catch (Exception e)
		  {
		      e.printStackTrace();
		  }

	return list;
	}
	
	private PlayerGameRecord mapRecord(ResultSet rs) throws SQLException{
		 PlayerGameRecord record = new PlayerGameRecord();

	        record.setRecordNo(rs.getInt("record_no"));
	        record.setPlayerNo(rs.getInt("player_no"));
	        record.setGameNo(rs.getInt("game_no"));

	        int currentRoomNo = rs.getInt("current_room_no");
	        if (rs.wasNull()) {
	            record.setCurrentRoomNo(null);
	        } else {
	            record.setCurrentRoomNo(currentRoomNo);
	        }

	        int currentPuzzleNo = rs.getInt("current_puzzle_no");
	        if (rs.wasNull()) {
	            record.setCurrentPuzzleNo(null);
	        } else {
	            record.setCurrentPuzzleNo(currentPuzzleNo);
	        }

	        int endingNo = rs.getInt("ending_no");
	        if (rs.wasNull()) {
	            record.setEndingNo(null);
	        } else {
	            record.setEndingNo(endingNo);
	        }

	        record.setProgressStatus(rs.getString("progress_status"));
	        record.setResultStatus(rs.getString("result_status"));
	        record.setCurrentStep(rs.getString("current_step"));

	        Timestamp startTime = rs.getTimestamp("start_time");
	        if (startTime != null) {
	            record.setStartTime(startTime.toLocalDateTime());
	        }

	        Timestamp endTime = rs.getTimestamp("end_time");
	        if (endTime != null) {
	            record.setEndTime(endTime.toLocalDateTime());
	        }

	        return record;
	}

}
