package entity;

public class Item {
	
	private int itemNo;
	private int gameNo;
	private String itemName;
	private String itemType;
	private String description;
	
	public Item() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Item(int gameNo, String itemName, String itemType, String description) {
		super();
		this.gameNo = gameNo;
		this.itemName = itemName;
		this.itemType = itemType;
		this.description = description;
	}

	public int getItemNo() {
		return itemNo;
	}

	public void setItemNo(int itemNo) {
		this.itemNo = itemNo;
	}

	public int getGameNo() {
		return gameNo;
	}

	public void setGameNo(int gameNo) {
		this.gameNo = gameNo;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
