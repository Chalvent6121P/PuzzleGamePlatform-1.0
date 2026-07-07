package entity;

public class Room {
	
	private int roomNo;
	private int gameNo;
	private String roomName;
	private String description;
	private int roomOrder;
	public Room() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Room(int gameNo, String roomName, String description, int roomOrder) {
		super();
		this.gameNo = gameNo;
		this.roomName = roomName;
		this.description = description;
		this.roomOrder = roomOrder;
	}
	public int getRoomNo() {
		return roomNo;
	}
	public void setRoomNo(int roomNo) {
		this.roomNo = roomNo;
	}
	public int getGameNo() {
		return gameNo;
	}
	public void setGameNo(int gameNo) {
		this.gameNo = gameNo;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getRoomOrder() {
		return roomOrder;
	}
	public void setRoomOrder(int roomOrder) {
		this.roomOrder = roomOrder;
	}
	
}
