package entity;

import java.time.LocalDateTime;

public class Game {

    private int gameNo;
    private String gameName;
    private String difficulty;
    private String description;
    private boolean active;
    private String coverImagePath;
    private LocalDateTime createTime;

    public Game() {
        this.active = true;
    }

    public Game(
            int gameNo,
            String gameName,
            String difficulty,
            String description) {
        this();
        this.gameNo = gameNo;
        this.gameName = gameName;
        this.difficulty = difficulty;
        this.description = description;
    }

    public int getGameNo() {
        return gameNo;
    }

    public void setGameNo(int gameNo) {
        this.gameNo = gameNo;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCoverImagePath() {
        return coverImagePath;
    }

    public void setCoverImagePath(String coverImagePath) {
        this.coverImagePath = coverImagePath;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
