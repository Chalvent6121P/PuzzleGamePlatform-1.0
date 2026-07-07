package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.AchievementDao;
import entity.Achievement;
import util.DbConnection;

public class AchievementDaoImpl implements AchievementDao{

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void insert(Achievement achievement) {
		String sql="insert into achievement(game_no, achievement_name, description, condition_text)"
				+ " values(?,?,?,?)";
		try(Connection conn=DbConnection.getDb();
			PreparedStatement ps=conn.prepareStatement(sql))
		{
			if(achievement.getGameNo()==null) 
			{
				ps.setNull(1, java.sql.Types.INTEGER);
			}
			else 
			{
                ps.setInt(1, achievement.getGameNo());
            }
			ps.setString(2, achievement.getAchievementName());
            ps.setString(3, achievement.getDescription());
            ps.setString(4, achievement.getConditionText());

            ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void update(Achievement achievement) {
		String sql="update achievement set game_no=?,achievement_name=?,description=?,condition_text=?"
				+ " where achievement_no=?";
		try (Connection conn = DbConnection.getDb();
	             PreparedStatement ps = conn.prepareStatement(sql))
		{
			if (achievement.getGameNo() == null) {
                ps.setNull(1, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, achievement.getGameNo());
            }

            ps.setString(2, achievement.getAchievementName());
            ps.setString(3, achievement.getDescription());
            ps.setString(4, achievement.getConditionText());
            ps.setInt(5, achievement.getAchievementNo());

            ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void delete(int achievementNo) {
		String sql="delete from achievement where achievement_no=?";
		try (Connection conn = DbConnection.getDb();
	         PreparedStatement ps = conn.prepareStatement(sql))
		{
			ps.setInt(1, achievementNo);

            ps.executeUpdate();
		} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
	}

	@Override
	public Achievement selectByAchievementNo(int achievementNo) {
		String sql="select * from achievement where achievement_no=?";
		Achievement achievement=null;
		try(Connection conn=DbConnection.getDb();
			PreparedStatement ps=conn.prepareStatement(sql))
		{
			ps.setInt(1, achievementNo);
			try(ResultSet rs=ps.executeQuery())
			{
				if(rs.next()) 
				{
					achievement=mapAchievement(rs);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return achievement;
	}

	@Override
	public List<Achievement> selectAll() {
		String sql="select * from achievement order by achievement_no";
		List<Achievement> list=new ArrayList<>();
		try(Connection conn=DbConnection.getDb();
			PreparedStatement ps=conn.prepareStatement(sql);
			ResultSet rs=ps.executeQuery())
		{
			while(rs.next()) 
			{
				list.add(mapAchievement(rs));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<Achievement> selectByGameNo(int gameNo) {
		String sql="select * from achievement where game_no=? order by achievement_no";
		List<Achievement> list=new ArrayList<>();
		try(Connection conn=DbConnection.getDb();
			PreparedStatement ps=conn.prepareStatement(sql);)
		{
			ps.setInt(1, gameNo);
			try(ResultSet rs=ps.executeQuery())
			{
				while(rs.next()) 
				{
					list.add(mapAchievement(rs));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<Achievement> selecetGlobalAchievement() {
		String sql="select * from achievement where game_no is null order by achievement_no";
		List<Achievement> list=new ArrayList<>();
		try(Connection conn=DbConnection.getDb();
			PreparedStatement ps=conn.prepareStatement(sql);
			ResultSet rs=ps.executeQuery())
		{
			while(rs.next()) 
			{
				list.add(mapAchievement(rs));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	private Achievement mapAchievement(ResultSet rs) throws SQLException {

        Achievement achievement = new Achievement();

        achievement.setAchievementNo(rs.getInt("achievement_no"));

        int gameNo = rs.getInt("game_no");
        if (rs.wasNull()) {
            achievement.setGameNo(null);
        } else {
            achievement.setGameNo(gameNo);
        }

        achievement.setAchievementName(rs.getString("achievement_name"));
        achievement.setDescription(rs.getString("description"));
        achievement.setConditionText(rs.getString("condition_text"));

        return achievement;
    }

}
