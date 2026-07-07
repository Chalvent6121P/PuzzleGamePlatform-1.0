package dao;

import java.util.List;

import entity.Ending;

public interface EndingDao {
	
	
	//新增結局
	void insert(Ending ending);
	
	
	//更新、修改結局內容(名稱、描述)
	void update(Ending ending);
	
	//刪除結局(後台)
	void delete(int endingNo);
	
	//搜尋某結局
	Ending selectByEndingNo(int endingNo);
	
	//搜尋某個遊戲所有結果
	List<Ending> selectByGameNo(int gameNo);
}
