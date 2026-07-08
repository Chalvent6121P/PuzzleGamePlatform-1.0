package controller.games;

import controller.games.story.StoryPuzzleGamePage;
import service.impl.engine.story.GameDefinitions;

public class HospitalGamePage extends StoryPuzzleGamePage {

    private static final long serialVersionUID = 1L;

    public HospitalGamePage(int playerNo) {
        this(playerNo, null);
    }

    public HospitalGamePage(int playerNo, Runnable returnToLobbyAction) {
        super(playerNo, GameDefinitions.hospital(), returnToLobbyAction);
    }
}
