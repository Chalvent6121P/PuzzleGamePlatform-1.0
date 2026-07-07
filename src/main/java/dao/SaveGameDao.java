package dao;

import java.util.List;

import entity.SaveGame;

public interface SaveGameDao {
	
	
	//新增存檔
	void insert(SaveGame saveGame);
	
	//刪除存檔
	void delete(int saveNo);

	//查詢某一存檔
	SaveGame selectBySaveNo(int saveNo);

	//查詢某遊戲所有存檔
	List<SaveGame> selectByRecordNo(int recordNo);
}
