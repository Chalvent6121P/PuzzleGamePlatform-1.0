package controller.games;

import controller.games.story.StoryPuzzleGamePage;
import service.impl.engine.story.GameDefinitions;

public class LaboratoryGamePage extends StoryPuzzleGamePage {

    private static final long serialVersionUID = 1L;

    public LaboratoryGamePage(int playerNo) {
        this(playerNo, null);
    }

    public LaboratoryGamePage(int playerNo, Runnable returnToLobbyAction) {
        super(playerNo, GameDefinitions.laboratory(), returnToLobbyAction);
    }
}
