package service.impl.engine;

import dao.impl.InventoryDaoImpl;
import dao.impl.PlayerAchievementDaoImpl;
import dao.impl.PlayerGameRecordDaoImpl;
import dao.impl.PuzzleRecordDaoImpl;
import dao.impl.SaveGameDaoImpl;
import entity.Item;
import entity.PlayerAchievement;
import entity.PlayerGameRecord;
import entity.PuzzleRecord;
import entity.SaveGame;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import dao.InventoryDao;
import dao.PlayerAchievementDao;
import dao.PlayerGameRecordDao;
import dao.PuzzleRecordDao;
import dao.SaveGameDao;
import service.engine.GameEngine;
import util.DbConnection;

public abstract class AbstractGameEngine implements GameEngine{
	
	protected PlayerGameRecordDao recordDao=new PlayerGameRecordDaoImpl();
	protected InventoryDao inventoryDao=new InventoryDaoImpl();
	protected PuzzleRecordDao puzzleRecordDao=new PuzzleRecordDaoImpl();
	protected SaveGameDao saveGameDao=new SaveGameDaoImpl();
	protected PlayerAchievementDao playerAchievementDao=new PlayerAchievementDaoImpl();
	
	public int startGame(int playerNo, int gameNo) {

        PlayerGameRecord record = new PlayerGameRecord();

        record.setPlayerNo(playerNo);
        record.setGameNo(gameNo);
        record.setProgressStatus("進行中");
        record.setResultStatus("未完成");
        record.setCurrentStep("玩家開始遊戲");

        int recordNo = recordDao.insert(record);

        return recordNo;
    }
	protected void updateProgress(int recordNo,Integer roomNo,Integer puzzleNo,String currentStep) 
	{
		PlayerGameRecord record = recordDao.selectByRecordNo(recordNo);

		if (record != null) 
		{
			record.setCurrentRoomNo(roomNo);
			record.setCurrentPuzzleNo(puzzleNo);
			record.setProgressStatus("進行中");
			record.setResultStatus("未完成");
			record.setCurrentStep(currentStep);

			recordDao.updateProgress(record);
		}
	}
	
	 protected void addItem(int recordNo, int itemNo) 
	 {
	        boolean alreadyHasItem = inventoryDao.hasItem(recordNo, itemNo);
	        if (!alreadyHasItem) 
	        {
	            inventoryDao.insert(recordNo, itemNo);
	        }
	 }
	 protected boolean hasItem(int recordNo, int itemNo) {

	        return inventoryDao.hasItem(recordNo, itemNo);
	    }

	 public List<Item> getInventoryItems(int recordNo) {
	     return inventoryDao.selectItemsByRecordNo(recordNo);
	 }

	 protected void savePuzzleRecord(int recordNo,int puzzleNo,String inputAnswer,boolean correct) 
	 {
	        PuzzleRecord puzzleRecord = new PuzzleRecord();

	        puzzleRecord.setRecordNo(recordNo);
	        puzzleRecord.setPuzzleNo(puzzleNo);
	        puzzleRecord.setAnswer(inputAnswer);
	        puzzleRecord.setCorrect(correct);

	        puzzleRecordDao.insert(puzzleRecord);
	 }
	 
	 protected boolean hasSolved(int recordNo, int puzzleNo) 
	 {

	        return puzzleRecordDao.hasSolved(recordNo, puzzleNo);
	 }
	 
	 protected void saveGame(int recordNo,int playerNo,int gameNo,String saveName,String saveData) 
	 {
		 SaveGame saveGame = new SaveGame();

		 saveGame.setRecordNo(recordNo);
		 saveGame.setPlayerNo(playerNo);
		 saveGame.setGameNo(gameNo);
		 saveGame.setSaveName(saveName);
		 saveGame.setSaveData(saveData);

		 saveGameDao.insert(saveGame);
	 }
	 
	 protected void unlockAchievement(int playerNo, int achievementNo) 
	 {

	     boolean alreadyUnlocked = playerAchievementDao.hasAchievement(playerNo, achievementNo);

	     if (!alreadyUnlocked) 
	      {
	           PlayerAchievement playerAchievement = new PlayerAchievement();

	           playerAchievement.setPlayerNo(playerNo);
	           playerAchievement.setAchievementNo(achievementNo);

	           playerAchievementDao.insert(playerAchievement);
	      }
	  }
	 
	 public String finishGame(int recordNo,int endingNo,String resultStatus) 
	 {

		 recordDao.finishGame(recordNo,endingNo,resultStatus,"遊戲結束");

		 return "GAME_FINISHED";
	 }
	 
	 protected String finishGame(int recordNo,int endingNo,String resultStatus,String currentStep)
	 {
		 recordDao.finishGame(recordNo,endingNo,resultStatus,currentStep);

		 return "GAME_FINISHED";
	 }
	
}
