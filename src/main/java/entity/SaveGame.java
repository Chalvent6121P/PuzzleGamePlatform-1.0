package entity;

import java.time.LocalDateTime;

public class SaveGame {
	 private int saveNo;
	    private int recordNo;
	    private int playerNo;
	    private int gameNo;
	    private String saveName;
	    private String saveData;
	    private LocalDateTime saveTime;
	 public SaveGame() {
		 super();
		// TODO Auto-generated constructor stub
	 }
	 public SaveGame(int recordNo, int playerNo, int gameNo, String saveName, String saveData) {
	        this.recordNo = recordNo;
	        this.playerNo = playerNo;
	        this.gameNo = gameNo;
	        this.saveName = saveName;
	        this.saveData = saveData;
	  }
	 public int getSaveNo() {
		 return saveNo;
	 }
	 public void setSaveNo(int saveNo) {
		 this.saveNo = saveNo;
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
	 public String getSaveName() {
		 return saveName;
	 }
	 public void setSaveName(String saveName) {
		 this.saveName = saveName;
	 }
	 public String getSaveData() {
		 return saveData;
	 }
	 public void setSaveData(String saveData) {
		 this.saveData = saveData;
	 }
	 public LocalDateTime getSaveTime() {
		 return saveTime;
	 }
	 public void setSaveTime(LocalDateTime saveTime) {
		 this.saveTime = saveTime;
	 }
	 
	 
	 
}
