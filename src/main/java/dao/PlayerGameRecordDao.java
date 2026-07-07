package dao;

import java.util.List;

import entity.PlayerGameRecord;

public interface PlayerGameRecordDao {
	
	//新增遊戲紀錄(開始遊戲時新增,並回傳recordNo)
	int insert(PlayerGameRecord record);
	
	//更新遊戲紀錄(進度:目前房間、謎題)
	void updateProgress(PlayerGameRecord record);
	
	//完成的關卡進度紀錄、遊戲完成或失敗時更新結果
	void finishGame(int recordNo,int endingNo,String resultStatus,String currentStep);
	
	//查詢遊戲紀錄
		//紀錄檔案編號查詢進度
	PlayerGameRecord selectByRecordNo(int recordNo);
		//玩家編號查詢紀錄檔,用List打包
	List<PlayerGameRecord> selectByPlayerNo(int playerNo);
}
