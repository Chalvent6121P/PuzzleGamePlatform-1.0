package entity;

import java.time.LocalDateTime;

public class GameRecordReportRow {

    private int recordNo;
    private int playerNo;
    private String playerName;
    private String account;
    private int gameNo;
    private String gameName;
    private String roomName;
    private String puzzleName;
    private String endingName;
    private String progressStatus;
    private String resultStatus;
    private String currentStep;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public int getRecordNo() { return recordNo; }
    public void setRecordNo(int recordNo) { this.recordNo = recordNo; }
    public int getPlayerNo() { return playerNo; }
    public void setPlayerNo(int playerNo) { this.playerNo = playerNo; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
    public int getGameNo() { return gameNo; }
    public void setGameNo(int gameNo) { this.gameNo = gameNo; }
    public String getGameName() { return gameName; }
    public void setGameName(String gameName) { this.gameName = gameName; }
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public String getPuzzleName() { return puzzleName; }
    public void setPuzzleName(String puzzleName) { this.puzzleName = puzzleName; }
    public String getEndingName() { return endingName; }
    public void setEndingName(String endingName) { this.endingName = endingName; }
    public String getProgressStatus() { return progressStatus; }
    public void setProgressStatus(String progressStatus) { this.progressStatus = progressStatus; }
    public String getResultStatus() { return resultStatus; }
    public void setResultStatus(String resultStatus) { this.resultStatus = resultStatus; }
    public String getCurrentStep() { return currentStep; }
    public void setCurrentStep(String currentStep) { this.currentStep = currentStep; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}
