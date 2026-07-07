package dao;

import java.util.List;

import entity.PlayerAchievement;
import entity.PlayerAchievementRecord;

public interface PlayerAchievementDao {

	
	//新增玩家達成之成就
	void insert(PlayerAchievement playerAchievement);
	
	//判斷是否已經完成遊戲中的某成就
	boolean hasAchievement(int playerNo,int achievementNo);
	
	//查詢某成就完成的玩家有哪些
	PlayerAchievement selectByAchievement(int playerNo,int achievementNo);
	
	//查詢玩家達成的成就有哪些
	List<PlayerAchievement> selectByPlayerNo(int playerNo);

	// 查詢全部成就，並標示指定玩家是否已解鎖。
	List<PlayerAchievementRecord> selectAchievementRecordsByPlayerNo(int playerNo);
}
