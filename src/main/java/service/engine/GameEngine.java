package service.engine;

public interface GameEngine {
	
	//開始遊戲
	int startGame(int playerNo, int gameNo);
	
	//點擊物件
    String clickObject(int recordNo, String objectName);
    
    //輸入答案
    String submitAnswer(int recordNo, int puzzleNo, String input);
    
    //完成遊戲
    String finishGame(int recordNo, int endingNo, String resultStatus);

}
