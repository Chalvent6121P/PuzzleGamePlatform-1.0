package dao;

import java.util.List;

import entity.PuzzleRecord;

public interface PuzzleRecordDao {
	
	//新增遊戲回答紀錄
	void insert(PuzzleRecord puzzleRecord);
	
	//檢查某場遊戲中的某個謎題是否已經答對
	boolean hasSolved(int recordNo,int puzzleNo);
	
	//查詢某場遊戲的所有解謎紀錄
	List<PuzzleRecord> selectByRecordNo(int recordNo);
	
	//查詢某謎題曾被那些人類挑戰過
	List<PuzzleRecord> selectByPuzzleNo(int puzzleNo);
}
