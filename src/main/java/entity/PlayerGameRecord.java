package entity;

import java.time.LocalDateTime;

public class PlayerGameRecord {
	
	private int recordNo;
	private int playerNo;
	private int gameNo;
	private Integer currentRoomNo;
	private Integer currentPuzzleNo;
	private Integer endingNo;
	private String progressStatus;
	private String resultStatus;
	private String currentStep;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	public PlayerGameRecord() {
		super();
		// TODO Auto-generated constructor stub
	}
	public PlayerGameRecord(int playerNo, int gameNo, String progressStatus, String resultStatus, String currentStep) {
		super();
		this.playerNo = playerNo;
		this.gameNo = gameNo;
		this.progressStatus = progressStatus;
		this.resultStatus = resultStatus;
		this.currentStep = currentStep;
	}
	public int getRecordNo() {
		return recordNo;
	}
	public void setRecordNo(int recordNo) {
		this.recordNo = recordNo;
	}
	public int getPlayerNo() {
		return playerNo;
	}
	public void setPlayerNo(int playerNo) {
		this.playerNo = playerNo;
	}
	public int getGameNo() {
		return gameNo;
	}
	public void setGameNo(int gameNo) {
		this.gameNo = gameNo;
	}
	public Integer getCurrentRoomNo() {
		return currentRoomNo;
	}
	public void setCurrentRoomNo(Integer currentRoomNo) {
		this.currentRoomNo = currentRoomNo;
	}
	public Integer getCurrentPuzzleNo() {
		return currentPuzzleNo;
	}
	public void setCurrentPuzzleNo(Integer currentPuzzleNo) {
		this.currentPuzzleNo = currentPuzzleNo;
	}
	public Integer getEndingNo() {
		return endingNo;
	}
	public void setEndingNo(Integer endingNo) {
		this.endingNo = endingNo;
	}
	public String getProgressStatus() {
		return progressStatus;
	}
	public void setProgressStatus(String progressStatus) {
		this.progressStatus = progressStatus;
	}
	public String getResultStatus() {
		return resultStatus;
	}
	public void setResultStatus(String resultStatus) {
		this.resultStatus = resultStatus;
	}
	public String getCurrentStep() {
		return currentStep;
	}
	public void setCurrentStep(String currentStep) {
		this.currentStep = currentStep;
	}
	public LocalDateTime getStartTime() {
		return startTime;
	}
	public void setStartTime(LocalDateTime starTime) {
		this.startTime = starTime;
	}
	public LocalDateTime getEndTime() {
		return endTime;
	}
	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}
	
	
}
