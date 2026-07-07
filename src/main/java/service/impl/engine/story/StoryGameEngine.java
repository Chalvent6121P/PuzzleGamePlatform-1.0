package service.impl.engine.story;

import dao.ItemDao;
import dao.impl.ItemDaoImpl;
import entity.Item;
import entity.PlayerGameRecord;
import service.impl.engine.AbstractGameEngine;

public class StoryGameEngine extends AbstractGameEngine {

    private final StoryGameDefinition definition;
    private final ItemDao itemDao = new ItemDaoImpl();

    public StoryGameEngine(StoryGameDefinition definition) {
        if (definition == null) {
            throw new IllegalArgumentException("遊戲定義不可為 null。");
        }
        this.definition = definition;
    }

    public int startStoryGame(int playerNo) {
        return startGame(playerNo, definition.getGameNo());
    }

    @Override
    public String clickObject(int recordNo, String objectName) {
        return definition.getIntroduction();
    }

    @Override
    public String submitAnswer(
            int recordNo, int puzzleNo, String input) {
        StoryPuzzle puzzle = findPuzzle(puzzleNo);

        if (puzzle == null) {
            return "ERROR::找不到指定謎題。";
        }

        boolean correct = matchesAnyAcceptedAnswer(input, puzzle.getAnswer());
        savePuzzleRecord(recordNo, puzzleNo, input, correct);

        if (!correct) {
            return "WRONG::答案不正確，房間沒有任何反應。";
        }

        if (puzzle.getRewardItemNo() > 0) {
            addItem(recordNo, puzzle.getRewardItemNo());
        }

        updateProgress(
                recordNo,
                null,
                puzzleNo,
                "玩家解開「" + puzzle.getTitle() + "」");

        String rewardText = buildRewardText(puzzle.getRewardItemNo());

        if (isLastPuzzle(puzzleNo)) {
            PlayerGameRecord record = recordDao.selectByRecordNo(recordNo);
            if (record == null) {
                return "ERROR::無法取得遊戲紀錄。";
            }

            finishGame(
                    recordNo,
                    definition.getEndingNo(),
                    "成功",
                    definition.getEndingText());

            unlockAchievement(
                    record.getPlayerNo(),
                    definition.getAchievementNo());

            return "CLEAR::" + puzzle.getSuccessText()
                    + rewardText
                    + "\n\n" + definition.getEndingText();
        }

        return "CORRECT::" + puzzle.getSuccessText() + rewardText;
    }

    private boolean matchesAnyAcceptedAnswer(String input, String acceptedAnswers) {
        String normalizedInput = normalize(input);
        if (acceptedAnswers == null) {
            return normalizedInput.isEmpty();
        }

        String[] answers = acceptedAnswers.split("\\|");
        for (String answer : answers) {
            if (normalize(answer).equalsIgnoreCase(normalizedInput)) {
                return true;
            }
        }
        return false;
    }

    private String buildRewardText(int itemNo) {
        if (itemNo <= 0) {
            return "";
        }

        Item item = itemDao.selectByItemNo(itemNo);
        return item == null
                ? ""
                : "\n\n【背包】取得：" + item.getItemName();
    }

    private StoryPuzzle findPuzzle(int puzzleNo) {
        for (StoryPuzzle puzzle : definition.getPuzzles()) {
            if (puzzle.getPuzzleNo() == puzzleNo) {
                return puzzle;
            }
        }
        return null;
    }

    private boolean isLastPuzzle(int puzzleNo) {
        int lastIndex = definition.getPuzzles().size() - 1;
        return definition.getPuzzles().get(lastIndex).getPuzzleNo() == puzzleNo;
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim()
                .replace("：", ":")
                .replace(" ", "")
                .replace("　", "")
                .toUpperCase();
    }
}
