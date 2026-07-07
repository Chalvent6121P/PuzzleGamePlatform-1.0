package entity;

import java.time.LocalDateTime;

public class PuzzleRecord {
	
	private int puzzleRecordNo;
	private int recordNo;
	private int puzzleNo;
	private String answer;
	private boolean correct;
	private LocalDateTime answerTime;
	
	public PuzzleRecord() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PuzzleRecord(int recordNo, int puzzleNo, String answer, boolean correct) {
		super();
		this.recordNo = recordNo;
		this.puzzleNo = puzzleNo;
		this.answer = answer;
		this.correct = correct;
	}

	public int getPuzzleRecordNo() {
		return puzzleRecordNo;
	}

	public void setPuzzleRecordNo(int puzzleRecordNo) {
		this.puzzleRecordNo = puzzleRecordNo;
	}

	public int getRecordNo() {
		return recordNo;
	}

	public void setRecordNo(int recordNo) {
		this.recordNo = recordNo;
	}

	public int getPuzzleNo() {
		return puzzleNo;
	}

	public void setPuzzleNo(int puzzleNo) {
		this.puzzleNo = puzzleNo;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public boolean isCorrect() {
		return correct;
	}

	public void setCorrect(boolean correct) {
		this.correct = correct;
	}

	public LocalDateTime getAnswerTime() {
		return answerTime;
	}

	public void setAnswerTime(LocalDateTime answerTime) {
		this.answerTime = answerTime;
	}
	
	
}
