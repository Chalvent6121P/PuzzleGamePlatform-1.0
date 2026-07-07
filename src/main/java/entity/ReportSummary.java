package entity;

public class ReportSummary {

    private int playerCount;
    private int activePlayerCount;
    private int gameCount;
    private int activeGameCount;
    private int recordCount;
    private int completedCount;
    private int successCount;

    public int getPlayerCount() { return playerCount; }
    public void setPlayerCount(int playerCount) { this.playerCount = playerCount; }
    public int getActivePlayerCount() { return activePlayerCount; }
    public void setActivePlayerCount(int activePlayerCount) { this.activePlayerCount = activePlayerCount; }
    public int getGameCount() { return gameCount; }
    public void setGameCount(int gameCount) { this.gameCount = gameCount; }
    public int getActiveGameCount() { return activeGameCount; }
    public void setActiveGameCount(int activeGameCount) { this.activeGameCount = activeGameCount; }
    public int getRecordCount() { return recordCount; }
    public void setRecordCount(int recordCount) { this.recordCount = recordCount; }
    public int getCompletedCount() { return completedCount; }
    public void setCompletedCount(int completedCount) { this.completedCount = completedCount; }
    public int getSuccessCount() { return successCount; }
    public void setSuccessCount(int successCount) { this.successCount = successCount; }
}
