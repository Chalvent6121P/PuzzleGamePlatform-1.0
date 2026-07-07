package dao;

import java.util.List;

import entity.Room;

public interface RoomDao {
	
	//新增遊戲房間
	void insert(Room room);
	
	//更新遊戲房間
	void update(Room room);
	
	//刪除遊戲房間
	void delete(int roomNo);
	
	//查詢遊戲房間
	Room selectByRoomNo(int roomNo);
	
	//查詢某遊戲所有房間
	List<Room> selectByGameNo(int gameNo);
}
