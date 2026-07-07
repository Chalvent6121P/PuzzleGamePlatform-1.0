package entity;

import java.time.LocalDateTime;

public class PlayerAchievement {
	
	private int playerAchievementNo;
	private int playerNo;
	private int achievementNo;
	private LocalDateTime unlockTime;
	
	public PlayerAchievement() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PlayerAchievement(int playerNo, int achievementNo) {
		super();
		this.playerNo = playerNo;
		this.achievementNo = achievementNo;
	}

	public int getPlayerAchievementNo() {
		return playerAchievementNo;
	}

	public void setPlayerAchievementNo(int playerAchievementNo) {
		this.playerAchievementNo = playerAchievementNo;
	}

	public int getPlayerNo() {
		return playerNo;
	}

	public void setPlayerNo(int playerNo) {
		this.playerNo = playerNo;
	}

	public int getAchievementNo() {
		return achievementNo;
	}

	public void setAchievementNo(int achievementNo) {
		this.achievementNo = achievementNo;
	}

	public LocalDateTime getUnlockTime() {
		return unlockTime;
	}

	public void setUnlockTime(LocalDateTime unlockTime) {
		this.unlockTime = unlockTime;
	}
	
}
