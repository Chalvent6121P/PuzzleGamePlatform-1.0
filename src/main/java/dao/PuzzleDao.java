package dao;

import java.util.List;

import entity.Puzzle;

public interface PuzzleDao {
	
	//新增謎題
	void insert(Puzzle puzzle);
	
	//修改、更新謎題
    void update(Puzzle puzzle);
    
    //刪除謎題
    void delete(int puzzleNo);
    
    //查詢單一謎題、某一謎題
    Puzzle selectByPuzzleNo(int puzzleNo);
    
    //查尋某個遊戲全部謎題
    List<Puzzle> selectByGameNo(int gameNo);
    
    //查詢某個房間內的謎題
    List<Puzzle> selectByRoomNo(int roomNo);
}
