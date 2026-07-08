# PuzzleGamePlatform 2.5D V4

此版本以 `PuzzleGamePlatform_2_5D_Complete_Maven_V3` 為基礎，完成以下修正：

- 《逆行鐘塔》、《霧鎖病棟》、《鏡廳旅館》移除左下角重複的書本「線索」按鈕與點擊熱區。
- 《沉沒實驗室》背包公式文字改用一般數字顯示，避免 Windows 字型缺字而出現方框。
- 《失落的圖書館》新增右上角「背包」與「返回大廳」按鈕。
- SQL 完整版與升級腳本中的氣體公式統一為 `P1 × V1 = P2 × V2`。
- Maven 打包名稱為 `PuzzleGamePlatform-2.5D-V4.jar`。

## 匯入 Eclipse

1. `File` → `Import` → `Existing Maven Projects`。
2. 選擇本專案資料夾。
3. 確認 `pom.xml` 被勾選後完成匯入。
4. 如需重新整理相依套件：右鍵專案 → `Maven` → `Update Project`。

## 建置

在專案根目錄執行：

```bash
mvn clean package
```

成功後 JAR 會位於：

```text
target/PuzzleGamePlatform-2.5D-V4.jar
```

## 資料庫

- 已有 Phase 2 資料庫：執行 `database/puzzlegameplatform_phase2_gameplay_upgrade.sql`。
- 全新安裝：執行 `database/puzzlegameplatform_phase2_full.sql`。
- 請依本機 MySQL 設定確認 `src/main/java/util/DbConnection.java`。
