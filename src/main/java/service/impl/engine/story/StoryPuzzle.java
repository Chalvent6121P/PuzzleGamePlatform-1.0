package service.impl.engine.story;

public final class StoryPuzzle {

    private final int puzzleNo;
    private final String title;
    private final String sceneText;
    private final String question;
    private final String answer;
    private final String hint;
    private final String successText;
    private final int rewardItemNo;
    private final String successSoundPath;

    public StoryPuzzle(
            int puzzleNo,
            String title,
            String sceneText,
            String question,
            String answer,
            String hint,
            String successText,
            int rewardItemNo,
            String successSoundPath) {
        this.puzzleNo = puzzleNo;
        this.title = title;
        this.sceneText = sceneText;
        this.question = question;
        this.answer = answer;
        this.hint = hint;
        this.successText = successText;
        this.rewardItemNo = rewardItemNo;
        this.successSoundPath = successSoundPath;
    }

    public int getPuzzleNo() {
        return puzzleNo;
    }

    public String getTitle() {
        return title;
    }

    public String getSceneText() {
        return sceneText;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getHint() {
        return hint;
    }

    public String getSuccessText() {
        return successText;
    }

    public int getRewardItemNo() {
        return rewardItemNo;
    }

    public String getSuccessSoundPath() {
        return successSoundPath;
    }
}
