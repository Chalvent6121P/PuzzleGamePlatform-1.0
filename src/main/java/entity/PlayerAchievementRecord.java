package entity;

import java.time.LocalDateTime;

/**
 * 玩家成就頁面使用的唯讀資料列。
 * 同時包含成就定義與該玩家的解鎖狀態。
 */
public class PlayerAchievementRecord {

    private int achievementNo;
    private Integer gameNo;
    private String gameName;
    private String achievementName;
    private String description;
    private String conditionText;
    private boolean unlocked;
    private LocalDateTime unlockTime;

    public int getAchievementNo() {
        return achievementNo;
    }

    public void setAchievementNo(int achievementNo) {
        this.achievementNo = achievementNo;
    }

    public Integer getGameNo() {
        return gameNo;
    }

    public void setGameNo(Integer gameNo) {
        this.gameNo = gameNo;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getAchievementName() {
        return achievementName;
    }

    public void setAchievementName(String achievementName) {
        this.achievementName = achievementName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConditionText() {
        return conditionText;
    }

    public void setConditionText(String conditionText) {
        this.conditionText = conditionText;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public LocalDateTime getUnlockTime() {
        return unlockTime;
    }

    public void setUnlockTime(LocalDateTime unlockTime) {
        this.unlockTime = unlockTime;
    }
}
