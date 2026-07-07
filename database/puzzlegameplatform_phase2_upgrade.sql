-- PuzzleGamePlatform Phase 2 upgrade
-- 適用：已完成 Phase 1 upgrade 的 puzzlegame Schema
-- Phase 2 的 CRUD 與匯出功能沿用現有資料表，不需新增資料表。
-- 本檔會重新建立報表 View，確保報表中心可正常讀取。

USE puzzlegame;

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

-- 驗證 Phase 2 所需欄位
SELECT player_no, player_name, account, role, status, create_time, last_login_time
FROM player
ORDER BY player_no;

SELECT game_no, game_name, difficulty, is_active, cover_image_path, create_time
FROM game
ORDER BY game_no;

SELECT * FROM vw_player_game_record
ORDER BY start_time DESC, record_no DESC;
