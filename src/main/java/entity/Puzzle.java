package entity;

public class Puzzle {
	
	private int puzzleNo;
	private int gameNo;
	private Integer roomNo;
	private String puzzleName;
	private String correctAnswer;
	private String hint;
	private int puzzleOrder;
	public Puzzle() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Puzzle(int gameNo, Integer roomNo, String puzzleName, String correctAnswer, String hint, int puzzleOrder) {
		super();
		this.gameNo = gameNo;
		this.roomNo = roomNo;
		this.puzzleName = puzzleName;
		this.correctAnswer = correctAnswer;
		this.hint = hint;
		this.puzzleOrder = puzzleOrder;
	}
	public int getPuzzleNo() {
		return puzzleNo;
	}
	public void setPuzzleNo(int puzzleNo) {
		this.puzzleNo = puzzleNo;
	}
	public int getGameNo() {
		return gameNo;
	}
	public void setGameNo(int gameNo) {
		this.gameNo = gameNo;
	}
	public Integer getRoomNo() {
		return roomNo;
	}
	public void setRoomNo(Integer roomNo) {
		this.roomNo = roomNo;
	}
	public String getPuzzleName() {
		return puzzleName;
	}
	public void setPuzzleName(String puzzleName) {
		this.puzzleName = puzzleName;
	}
	public String getCorrectAnswer() {
		return correctAnswer;
	}
	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
	}
	public String getHint() {
		return hint;
	}
	public void setHint(String hint) {
		this.hint = hint;
	}
	public int getPuzzleOrder() {
		return puzzleOrder;
	}
	public void setPuzzleOrder(int puzzleOrder) {
		this.puzzleOrder = puzzleOrder;
	}
	
}
