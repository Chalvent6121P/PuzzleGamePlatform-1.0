-- ================================================================
-- PuzzleGamePlatform Phase 1 升級腳本
-- 適用：既有 puzzlegame 資料庫（MySQL 8.x）
-- 內容：玩家角色/狀態、管理員帳號、五款遊戲、房間、謎題、結局、成就
-- 注意：此腳本設計為執行一次。
-- ================================================================

USE puzzlegame;

SET FOREIGN_KEY_CHECKS = 0;

ALTER TABLE player
    ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'PLAYER' AFTER password,
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' AFTER role,
    ADD COLUMN last_login_time DATETIME NULL AFTER create_time;

ALTER TABLE game
    ADD COLUMN is_active TINYINT(1) NOT NULL DEFAULT 1 AFTER description,
    ADD COLUMN cover_image_path VARCHAR(255) NULL AFTER is_active;

UPDATE player
SET role = 'PLAYER', status = 'ACTIVE'
WHERE role IS NULL OR role = '';

INSERT INTO player
    (player_name, account, password, role, status)
VALUES
    ('系統管理員', 'admin', 'Admin@1234', 'ADMIN', 'ACTIVE')
ON DUPLICATE KEY UPDATE
    player_name = VALUES(player_name),
    role = 'ADMIN',
    status = 'ACTIVE';

INSERT INTO game
    (game_no, game_name, difficulty, description, is_active, cover_image_path)
VALUES
    (1, '失落的圖書館', '簡單',
     '尋找時間、書頁與鑰匙的線索，逃出古老圖書館。', 1,
     '/images/library/library_scene.png'),
    (2, '逆行的鐘塔', '普通',
     '破解逆向運轉的時間機關，逃離重複的午夜。', 1,
     '/images/games/clock_tower.png'),
    (3, '霧鎖病棟', '普通',
     '在白霧籠罩的病棟中找回病歷、藥櫃與電梯權限。', 1,
     '/images/games/hospital.png'),
    (4, '沉沒實驗室', '困難',
     '重啟海底實驗室的氧氣、能源與逃生艙。', 1,
     '/images/games/laboratory.png'),
    (5, '鏡廳旅館', '困難',
     '找回被鏡子奪走的房號、姓名與記憶。', 1,
     '/images/games/mirror_hotel.png')
ON DUPLICATE KEY UPDATE
    game_name = VALUES(game_name),
    difficulty = VALUES(difficulty),
    description = VALUES(description),
    is_active = VALUES(is_active),
    cover_image_path = VALUES(cover_image_path);

INSERT INTO room
    (room_no, game_no, room_name, description, room_order)
VALUES
    (1, 1, '古老藏書室', '放滿古籍、時鐘、地球儀與上鎖家具的圖書館。', 1),
    (2, 2, '逆行鐘塔主機室', '齒輪、鏡面鐘盤與午夜校準裝置。', 1),
    (3, 3, '封鎖病棟', '病歷櫃、藥櫃與被霧封鎖的電梯。', 1),
    (4, 4, '海底控制艙', '氧氣、能源與逃生艙控制系統。', 1),
    (5, 5, '鏡廳大堂', '無數鏡子映出不同年代的旅館住客。', 1)
ON DUPLICATE KEY UPDATE
    game_no = VALUES(game_no),
    room_name = VALUES(room_name),
    description = VALUES(description),
    room_order = VALUES(room_order);

UPDATE puzzle SET room_no = 1 WHERE puzzle_no IN (1, 2);

INSERT INTO puzzle
    (puzzle_no, game_no, room_no, puzzle_name,
     correct_answer, hint, puzzle_order)
VALUES
    (3, 2, 2, '鏡面鐘盤', '0220', '將 12:00 減去鏡面時間 21:40。', 1),
    (4, 2, 2, '齒輪序列', '48', '數列每次乘以 2。', 2),
    (5, 2, 2, '午夜校準', '268', '將 220 與 48 相加。', 3),

    (6, 3, 3, '缺頁病歷', '116', '104、108、112，每次增加 4。', 1),
    (7, 3, 3, '藥櫃標籤', '312', 'A=1、B=2、C=3，CAB。', 2),
    (8, 3, 3, '電梯權限', '28', '取 116 與 312 的末兩位相加。', 3),

    (9, 4, 4, '氧氣配比', '32', '2、4、8、16，每次加倍。', 1),
    (10, 4, 4, '能源矩陣', '56', '計算 7×8。', 2),
    (11, 4, 4, '逃生艙座標', '88', '將 32 與 56 相加。', 3),

    (12, 5, 5, '倒置房號', '816', '將鏡中的 618 反向排列。', 1),
    (13, 5, 5, '失落姓名', 'MIRROR', '13、9、18、18、15、18 對應英文字母。', 2),
    (14, 5, 5, '主鏡封印', '15', '計算 8+1+6。', 3)
ON DUPLICATE KEY UPDATE
    game_no = VALUES(game_no),
    room_no = VALUES(room_no),
    puzzle_name = VALUES(puzzle_name),
    correct_answer = VALUES(correct_answer),
    hint = VALUES(hint),
    puzzle_order = VALUES(puzzle_order);

INSERT INTO ending
    (ending_no, game_no, ending_name, ending_type, description)
VALUES
    (1, 1, '成功逃出圖書館', '成功', '取得出口鑰匙並逃出圖書館。'),
    (2, 2, '黎明鐘聲', '成功', '修復主鐘並逃離時間迴圈。'),
    (3, 3, '霧散之時', '成功', '恢復電梯權限並離開病棟。'),
    (4, 4, '海面曙光', '成功', '啟動逃生艙並離開海底基地。'),
    (5, 5, '保住姓名的人', '成功', '封印主鏡並離開旅館。')
ON DUPLICATE KEY UPDATE
    game_no = VALUES(game_no),
    ending_name = VALUES(ending_name),
    ending_type = VALUES(ending_type),
    description = VALUES(description);

INSERT INTO achievement
    (achievement_no, game_no, achievement_name, description, condition_text)
VALUES
    (1, 1, '圖書館逃脫者', '成功逃出失落的圖書館。', '完成遊戲 1'),
    (2, 2, '時間修復者', '讓逆行鐘塔恢復正向運轉。', '完成遊戲 2'),
    (3, 3, '霧中生還者', '成功離開霧鎖病棟。', '完成遊戲 3'),
    (4, 4, '深海逃生者', '從沉沒實驗室搭乘逃生艙離開。', '完成遊戲 4'),
    (5, 5, '鏡界守名者', '封印主鏡並保住自己的名字。', '完成遊戲 5')
ON DUPLICATE KEY UPDATE
    game_no = VALUES(game_no),
    achievement_name = VALUES(achievement_name),
    description = VALUES(description),
    condition_text = VALUES(condition_text);

ALTER TABLE inventory
    ADD CONSTRAINT uq_inventory_record_item
    UNIQUE (record_no, item_no);

ALTER TABLE player_achievement
    ADD CONSTRAINT uq_player_achievement
    UNIQUE (player_no, achievement_no);

DROP VIEW IF EXISTS vw_player_game_record;
CREATE VIEW vw_player_game_record AS
SELECT
    r.record_no,
    r.player_no,
    p.player_name,
    p.account,
    g.game_no,
    g.game_name,
    rm.room_name,
    pu.puzzle_name,
    e.ending_name,
    r.progress_status,
    r.result_status,
    r.current_step,
    r.start_time,
    r.end_time
FROM player_game_record r
JOIN player p ON r.player_no = p.player_no
JOIN game g ON r.game_no = g.game_no
LEFT JOIN room rm ON r.current_room_no = rm.room_no
LEFT JOIN puzzle pu ON r.current_puzzle_no = pu.puzzle_no
LEFT JOIN ending e ON r.ending_no = e.ending_no;

SET FOREIGN_KEY_CHECKS = 1;

-- 預設管理員測試帳號：
-- account: admin
-- password: Admin@1234
-- 正式交付前請立即修改密碼。
