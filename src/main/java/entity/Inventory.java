package entity;

import java.time.LocalDateTime;

public class Inventory {
	
	private int inventoryNo;
	private int recordNo;
	private int itemNo;
	private LocalDateTime getTime;
	
	public Inventory() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Inventory(int recordNo, int itemNo) {
		super();
		this.recordNo = recordNo;
		this.itemNo = itemNo;
	}

	public int getInventoryNo() {
		return inventoryNo;
	}

	public void setInventoryNo(int inventoryNo) {
		this.inventoryNo = inventoryNo;
	}

	public int getRecordNo() {
		return recordNo;
	}

	public void setRecordNo(int recordNo) {
		this.recordNo = recordNo;
	}

	public int getItemNo() {
		return itemNo;
	}

	public void setItemNo(int itemNo) {
		this.itemNo = itemNo;
	}

	public LocalDateTime getGetTime() {
		return getTime;
	}

	public void setGetTime(LocalDateTime getTime) {
		this.getTime = getTime;
	}
	
	
	
}
