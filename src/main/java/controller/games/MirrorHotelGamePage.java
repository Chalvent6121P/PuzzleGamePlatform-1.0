package controller.games;

import controller.games.story.StoryPuzzleGamePage;
import service.impl.engine.story.GameDefinitions;

public class MirrorHotelGamePage extends StoryPuzzleGamePage {

    private static final long serialVersionUID = 1L;

    public MirrorHotelGamePage(int playerNo, Runnable returnToLobbyAction) {
        super(playerNo, GameDefinitions.mirrorHotel(), returnToLobbyAction);
    }
}
