package dao;

import java.util.List;

import entity.Inventory;
import entity.Item;

public interface InventoryDao {

    // 拿到新道具時新增資料
    void insert(int recordNo, int itemNo);

    // 移除某筆背包紀錄
    void delete(int inventoryNo);

    // 檢查玩家是否已經取得某個道具
    boolean hasItem(int recordNo, int itemNo);

    // 查詢某場遊戲取得的所有背包紀錄
    List<Inventory> selectByRecordNo(int recordNo);

    // 查詢某場遊戲取得的完整道具資訊
    List<Item> selectItemsByRecordNo(int recordNo);
}
