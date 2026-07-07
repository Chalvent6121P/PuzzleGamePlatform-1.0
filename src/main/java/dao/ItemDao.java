package dao;

import java.util.List;

import entity.Item;

public interface ItemDao {
	
	//新增道具
	void insert(Item item);
	
	//更新、修改道具
	void update(Item item);
	 
	//刪除道具
	void delete(int itemNo);
	 
	//搜尋、查詢道具
		//查詢某一道具
	Item selectByItemNo(int itemNo);
		//查詢某個"遊戲的所有道具"
	List<Item> selectByGameNo(int gameNo);
}
