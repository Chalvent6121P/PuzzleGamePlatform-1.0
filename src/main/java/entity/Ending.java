package entity;

public class Ending {
	
	private int endingNo;
	private int gameNo;
	private String endingName;
	private String endingType;
	private String description;
	
	public Ending() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Ending(int gameNo, String endingName, String endingType, String description) {
		super();
		this.gameNo = gameNo;
		this.endingName = endingName;
		this.endingType = endingType;
		this.description = description;
	}

	public int getEndingNo() {
		return endingNo;
	}

	public void setEndingNo(int endingNo) {
		this.endingNo = endingNo;
	}

	public int getGameNo() {
		return gameNo;
	}

	public void setGameNo(int gameNo) {
		this.gameNo = gameNo;
	}

	public String getEndingName() {
		return endingName;
	}

	public void setEndingName(String endingName) {
		this.endingName = endingName;
	}

	public String getEndingType() {
		return endingType;
	}

	public void setEndingType(String endingType) {
		this.endingType = endingType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
