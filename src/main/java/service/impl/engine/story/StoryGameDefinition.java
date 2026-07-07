package service.impl.engine.story;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

public final class StoryGameDefinition {

    private final int gameNo;
    private final int endingNo;
    private final int achievementNo;
    private final String title;
    private final String subtitle;
    private final String introduction;
    private final String endingText;
    private final Color startColor;
    private final Color endColor;
    private final Color accentColor;
    private final String backgroundPath;
    private final List<StoryPuzzle> puzzles;

    public StoryGameDefinition(
            int gameNo,
            int endingNo,
            int achievementNo,
            String title,
            String subtitle,
            String introduction,
            String endingText,
            Color startColor,
            Color endColor,
            Color accentColor,
            String backgroundPath,
            List<StoryPuzzle> puzzles) {
        this.gameNo = gameNo;
        this.endingNo = endingNo;
        this.achievementNo = achievementNo;
        this.title = title;
        this.subtitle = subtitle;
        this.introduction = introduction;
        this.endingText = endingText;
        this.startColor = startColor;
        this.endColor = endColor;
        this.accentColor = accentColor;
        this.backgroundPath = backgroundPath;
        this.puzzles = Collections.unmodifiableList(puzzles);
    }

    public int getGameNo() {
        return gameNo;
    }

    public int getEndingNo() {
        return endingNo;
    }

    public int getAchievementNo() {
        return achievementNo;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getIntroduction() {
        return introduction;
    }

    public String getEndingText() {
        return endingText;
    }

    public Color getStartColor() {
        return startColor;
    }

    public Color getEndColor() {
        return endColor;
    }

    public Color getAccentColor() {
        return accentColor;
    }

    public String getBackgroundPath() {
        return backgroundPath;
    }

    public List<StoryPuzzle> getPuzzles() {
        return puzzles;
    }
}
