package service.impl.engine;

import entity.PlayerGameRecord;

public class LibraryGameEngine extends AbstractGameEngine {

    private static final int GAME_NO = 1;
    private static final int PUZZLE_PAGE = 1;
    private static final int PUZZLE_BOX = 2;

    private static final int ITEM_NOTE = 1;
    private static final int ITEM_SMALL_KEY = 2;
    private static final int ITEM_EXIT_KEY = 3;

    private static final int ENDING_SUCCESS = 1;
    private static final int ACHIEVEMENT_CLEAR = 1;

    public int startLibraryGame(int playerNo) {
        return startGame(playerNo, GAME_NO);
    }

    @Override
    public String clickObject(int recordNo, String objectName) {
        if ("bookShelf".equals(objectName)) {
            addItem(recordNo, ITEM_NOTE);
            updateProgress(recordNo, null, null, "玩家取得泛黃便條紙");
            return "你取得泛黃便條紙：真正答案藏在時間裡。";
        }

        if ("clock".equals(objectName)) {
            if (hasItem(recordNo, ITEM_NOTE)) {
                return "時鐘停在 8:15。";
            }
            return "這是一座停止的時鐘，但你還不知道該注意什麼。";
        }

        if ("globe".equals(objectName)) {
            if (!hasSolved(recordNo, PUZZLE_PAGE)) {
                return "地球儀底座似乎有機關，但目前無法轉動。";
            }
            addItem(recordNo, ITEM_SMALL_KEY);
            updateProgress(recordNo, null, PUZZLE_PAGE, "玩家取得小鑰匙");
            return "你轉動地球儀，在亞洲的位置找到一把小鑰匙。";
        }

        if ("drawer".equals(objectName)) {
            if (hasItem(recordNo, ITEM_SMALL_KEY)) {
                return "你用小鑰匙打開抽屜，裡面寫著密碼：2580。";
            }
            return "抽屜被鎖住了，需要一把小鑰匙。";
        }

        if ("exitDoor".equals(objectName)) {
            if (hasItem(recordNo, ITEM_EXIT_KEY)) {
                finishGame(
                        recordNo,
                        ENDING_SUCCESS,
                        "成功",
                        "玩家取得出口鑰匙並成功逃出圖書館");

                PlayerGameRecord record = recordDao.selectByRecordNo(recordNo);
                if (record != null) {
                    unlockAchievement(
                            record.getPlayerNo(), ACHIEVEMENT_CLEAR);
                }
                return "CLEAR";
            }
            return "出口門被鎖住了，需要出口鑰匙。";
        }

        return "這裡沒有特別的東西。";
    }

    @Override
    public String submitAnswer(
            int recordNo, int puzzleNo, String input) {
        if (puzzleNo == PUZZLE_PAGE) {
            if (!hasItem(recordNo, ITEM_NOTE)) {
                return "你還沒有找到與藍色書皮有關的提示。";
            }

            boolean correct = "815".equals(input);
            savePuzzleRecord(recordNo, puzzleNo, input, correct);

            if (correct) {
                updateProgress(
                        recordNo,
                        null,
                        puzzleNo,
                        "玩家解開百科全書頁碼謎題");
                return "你翻到第 815 頁，上面寫著：地球不是平的。";
            }
            return "頁碼錯誤。";
        }

        if (puzzleNo == PUZZLE_BOX) {
            if (!hasItem(recordNo, ITEM_SMALL_KEY)) {
                return "你尚未打開抽屜，無法確認密碼來源。";
            }

            boolean correct = "2580".equals(input);
            savePuzzleRecord(recordNo, puzzleNo, input, correct);

            if (correct) {
                addItem(recordNo, ITEM_EXIT_KEY);
                updateProgress(
                        recordNo,
                        null,
                        puzzleNo,
                        "玩家解開密碼盒並取得出口鑰匙");
                return "密碼盒打開了，你取得出口鑰匙。";
            }
            return "密碼錯誤。";
        }

        return "沒有這個謎題。";
    }
}
