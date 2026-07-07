package dao;

import java.util.List;

import entity.Achievement;

public interface AchievementDao {
	
	//新增成就
	void insert(Achievement achievement);
	
	//修改成就名稱、描述、解鎖條件
	void update(Achievement achievement);
	
	//刪除成就
	void delete(int achievementNo);
	
	//查詢某成就
	Achievement selectByAchievementNo(int achievementNo);
	
	//查看所有成就
	List<Achievement> selectAll();
	
	//查詢某一款遊戲的專屬成就
	List<Achievement> selectByGameNo(int gameNo);
	
	//查詢全平台成就，也就是 game_no 為 NULL 的成就
	List<Achievement> selecetGlobalAchievement();
	
}
