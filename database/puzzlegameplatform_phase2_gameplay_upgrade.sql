-- PuzzleGamePlatform Phase 2 遊戲內容、背包與音效升級
-- 適用：已完成 Phase 2 upgrade 的 puzzlegame 資料庫
-- 執行方式：MySQL Workbench -> File -> Open SQL Script -> 執行整份 Script

USE puzzlegame;

START TRANSACTION;

-- 更新圖書館既有道具說明，讓背包可顯示完整資訊。
UPDATE item SET
    item_name='泛黃便條紙',
    item_type='紙條',
    description='藍色書皮藏著頁碼線索；古董時鐘停在 8:15。'
WHERE item_no=1;

UPDATE item SET
    item_name='小鑰匙',
    item_type='鑰匙',
    description='在地球儀機關中找到，可打開抽屜。'
WHERE item_no=2;

UPDATE item SET
    item_name='出口鑰匙',
    item_type='鑰匙',
    description='由密碼盒取得，可打開圖書館出口門。'
WHERE item_no=3;

-- 新增四個故事關卡的背包道具。使用 201–212 的固定 item_no，避免與既有管理員新增道具的低編號衝突。
INSERT INTO item(item_no, game_no, item_name, item_type, description) VALUES
(201,2,'鏡面時間便條','紙條','鏡面鐘顯示 21:40；鏡面時間與真實時間相加為 12:00。'),
(202,2,'齒輪比刻片','線索','A=12 齒、B=36 齒、C=18 齒；相鄰齒輪方向相反。'),
(203,2,'校準鑰匙','鑰匙','刻有校準碼 0230，可解除主鐘鎖定。'),
(204,3,'鎮靜流程卡','紙條','遊戲分類線索：盤尼西林=抗生素；芬太尼=鴉片止痛藥；苯二氮平類=抗焦慮／鎮靜；皮質激素=抗發炎。非醫療建議。'),
(205,3,'病房輪班表殘頁','紙條','A 每 4 天巡房、B 每 6 天巡房；第 2 天共同巡房。'),
(206,3,'電梯磁卡','鑰匙','權限碼 314 驗證成功後取得，可啟動病棟電梯。'),
(207,4,'艙壓公式卡','紙條','恆溫封閉氣體使用 P₁V₁=P₂V₂；本題計算結果為 144 kPa。'),
(208,4,'聲納紀錄資料片','紀錄','水中聲速採 1500 m/s；2.4 秒為去回時間，海床距離 1800 m。'),
(209,4,'逃生艙授權晶片','鑰匙','校驗碼 162 通過後取得，可啟動逃生艙。'),
(210,5,'旅客登記殘頁','紙條','鏡中數列 13-15-15-18 反轉後為 18-15-15-13，即 ROOM。'),
(211,5,'鏡像密碼片','紙條','Atbash 字母鏡像：A↔Z、B↔Y；XLFMG 解為 COUNT。'),
(212,5,'主鏡銀鑰','鑰匙','封印碼 6173 啟動後取得，可關閉吞噬記憶的主鏡。')
ON DUPLICATE KEY UPDATE
    game_no=VALUES(game_no),
    item_name=VALUES(item_name),
    item_type=VALUES(item_type),
    description=VALUES(description);

ALTER TABLE item AUTO_INCREMENT=213;

-- 更新普通、困難關卡的題目答案與提示，保持 puzzle_no 不變，既有外鍵紀錄可繼續使用。
UPDATE puzzle SET puzzle_name='鏡面鐘盤', correct_answer='0220',
    hint='鏡面時間與真實時間相加為 12:00。', puzzle_order=1 WHERE puzzle_no=3;
UPDATE puzzle SET puzzle_name='三聯齒輪', correct_answer='6順時針',
    hint='轉數依齒數反比；兩次咬合後方向與 A 相同。', puzzle_order=2 WHERE puzzle_no=4;
UPDATE puzzle SET puzzle_name='午夜校準', correct_answer='0230',
    hint='140 + 6×15，並補成四位數。', puzzle_order=3 WHERE puzzle_no=5;

UPDATE puzzle SET puzzle_name='封存病歷：鎮靜分類', correct_answer='3',
    hint='找出標示為抗焦慮／鎮靜的藥物類別；本題為遊戲分類線索。', puzzle_order=1 WHERE puzzle_no=6;
UPDATE puzzle SET puzzle_name='夜班交會日', correct_answer='14',
    hint='4 與 6 的最小公倍數是 12，再加起始日 2。', puzzle_order=2 WHERE puzzle_no=7;
UPDATE puzzle SET puzzle_name='電梯權限', correct_answer='314',
    hint='第一題選項編號×100，再加共同巡房日。', puzzle_order=3 WHERE puzzle_no=8;

UPDATE puzzle SET puzzle_name='艙壓平衡', correct_answer='144',
    hint='使用 P₁V₁=P₂V₂。', puzzle_order=1 WHERE puzzle_no=9;
UPDATE puzzle SET puzzle_name='聲納回波', correct_answer='1800',
    hint='距離=聲速×回波時間÷2。', puzzle_order=2 WHERE puzzle_no=10;
UPDATE puzzle SET puzzle_name='逃生艙校驗碼', correct_answer='162',
    hint='(1+4+4)×(1800÷100)。', puzzle_order=3 WHERE puzzle_no=11;

UPDATE puzzle SET puzzle_name='倒映房名', correct_answer='ROOM',
    hint='先反轉數列，再用 A=1 對應字母。', puzzle_order=1 WHERE puzzle_no=12;
UPDATE puzzle SET puzzle_name='鏡像姓名', correct_answer='COUNT',
    hint='使用 Atbash：A↔Z、B↔Y。', puzzle_order=2 WHERE puzzle_no=13;
UPDATE puzzle SET puzzle_name='主鏡封印', correct_answer='6173',
    hint='ROOM 總和 61；COUNT 總和 73。', puzzle_order=3 WHERE puzzle_no=14;

UPDATE game SET description='破解鏡面時間、齒輪比與主鐘校準碼，逃離重複午夜。' WHERE game_no=2;
UPDATE game SET description='解讀藥物分類、輪班規律與電梯權限，穿越封鎖病棟。' WHERE game_no=3;
UPDATE game SET description='運用氣體定律、聲納回波與多步校驗啟動海底逃生艙。' WHERE game_no=4;
UPDATE game SET description='破解反向數列、Atbash 字母鏡像與主鏡封印碼。' WHERE game_no=5;

COMMIT;

-- 驗證結果
SELECT item_no, game_no, item_name, item_type, description
FROM item
ORDER BY game_no, item_no;

SELECT puzzle_no, game_no, puzzle_name, correct_answer, hint, puzzle_order
FROM puzzle
ORDER BY game_no, puzzle_order;
