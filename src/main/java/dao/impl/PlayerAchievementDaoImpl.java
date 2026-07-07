package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import dao.PlayerAchievementDao;
import entity.PlayerAchievement;
import entity.PlayerAchievementRecord;
import util.DbConnection;

public class PlayerAchievementDaoImpl implements PlayerAchievementDao{

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void insert(PlayerAchievement playerAchievement) {
		String sql="insert into player_achievement(player_no,achievement_no) values(?,?)";
		
		try(Connection conn=DbConnection.getDb();
			PreparedStatement ps=conn.prepareStatement(sql))
		{
			ps.setInt(1, playerAchievement.getPlayerNo());
			ps.setInt(2, playerAchievement.getAchievementNo());
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean hasAchievement(int playerNo, int achievementNo) {
		String sql="select * from player_achievement where player_no=? and achievement_no=?";
		boolean result=false;
		
		try(Connection conn=DbConnection.getDb();
			PreparedStatement ps=conn.prepareStatement(sql))
		{
			ps.setInt(1, playerNo);
			ps.setInt(2, achievementNo);
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
	public PlayerAchievement selectByAchievement(int playerNo, int achievementNo) {
		String sql="select * from player_achievement where player_no=? and achievement_no=?";
		PlayerAchievement playerAchievement=null;
		try(Connection conn=DbConnection.getDb();
			PreparedStatement ps=conn.prepareStatement(sql))
		{
			ps.setInt(1, playerNo);
			ps.setInt(2, achievementNo);
			try(ResultSet rs=ps.executeQuery())
			{
				if(rs.next()) 
				{
					playerAchievement=mapPlayerAchievement(rs);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return playerAchievement;
	}

	@Override
	public List<PlayerAchievement> selectByPlayerNo(int playerNo) {
		String sql="select * from player_achievement where player_no=? order by unlock_time desc";
		List<PlayerAchievement> list=new ArrayList<>();
		try(Connection conn=DbConnection.getDb();
			PreparedStatement ps=conn.prepareStatement(sql) )
		{
			ps.setInt(1, playerNo);
			try(ResultSet rs=ps.executeQuery())
			{
				while(rs.next()) 
				{
					list.add(mapPlayerAchievement(rs));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	

    @Override
    public List<PlayerAchievementRecord> selectAchievementRecordsByPlayerNo(
            int playerNo) {
        String sql = "SELECT a.achievement_no, a.game_no, "
                + "COALESCE(g.game_name, '全平台') AS game_name, "
                + "a.achievement_name, a.description, a.condition_text, "
                + "pa.unlock_time, "
                + "CASE WHEN pa.player_achievement_no IS NULL THEN 0 ELSE 1 END AS unlocked "
                + "FROM achievement a "
                + "LEFT JOIN game g ON a.game_no = g.game_no "
                + "LEFT JOIN player_achievement pa "
                + "ON pa.achievement_no = a.achievement_no AND pa.player_no = ? "
                + "ORDER BY unlocked DESC, a.achievement_no";

        List<PlayerAchievementRecord> records = new ArrayList<>();
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerNo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PlayerAchievementRecord record = new PlayerAchievementRecord();
                    record.setAchievementNo(rs.getInt("achievement_no"));
                    int gameNo = rs.getInt("game_no");
                    record.setGameNo(rs.wasNull() ? null : Integer.valueOf(gameNo));
                    record.setGameName(rs.getString("game_name"));
                    record.setAchievementName(rs.getString("achievement_name"));
                    record.setDescription(rs.getString("description"));
                    record.setConditionText(rs.getString("condition_text"));
                    record.setUnlocked(rs.getInt("unlocked") == 1);
                    Timestamp unlockTime = rs.getTimestamp("unlock_time");
                    if (unlockTime != null) {
                        record.setUnlockTime(unlockTime.toLocalDateTime());
                    }
                    records.add(record);
                }
            }
            return records;
        } catch (SQLException e) {
            throw new RuntimeException("讀取玩家成就紀錄失敗。", e);
        }
    }

	private PlayerAchievement mapPlayerAchievement(ResultSet rs) throws SQLException {
        PlayerAchievement playerAchievement = new PlayerAchievement();

        playerAchievement.setPlayerAchievementNo(rs.getInt("player_achievement_no"));
        playerAchievement.setPlayerNo(rs.getInt("player_no"));
        playerAchievement.setAchievementNo(rs.getInt("achievement_no"));

        Timestamp unlockTime = rs.getTimestamp("unlock_time");

        if (unlockTime != null) {
            playerAchievement.setUnlockTime(unlockTime.toLocalDateTime());
        }

        return playerAchievement;
    }

}
