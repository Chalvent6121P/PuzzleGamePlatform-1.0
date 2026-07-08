package controller.games;

import controller.games.story.StoryPuzzleGamePage;
import service.impl.engine.story.GameDefinitions;

public class ClockTowerGamePage extends StoryPuzzleGamePage {

    private static final long serialVersionUID = 1L;

    public ClockTowerGamePage(int playerNo) {
        this(playerNo, null);
    }

    public ClockTowerGamePage(int playerNo, Runnable returnToLobbyAction) {
        super(playerNo, GameDefinitions.clockTower(), returnToLobbyAction);
    }
}
