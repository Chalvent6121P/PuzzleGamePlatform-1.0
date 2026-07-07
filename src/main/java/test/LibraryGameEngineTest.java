package test;

import service.impl.engine.LibraryGameEngine;

public class LibraryGameEngineTest {

	public static void main(String[] args) {
		LibraryGameEngine engine = new LibraryGameEngine();

        int playerNo = 1;

        int recordNo = engine.startLibraryGame(playerNo);

        System.out.println("遊戲開始，recordNo = " + recordNo);

        System.out.println(engine.clickObject(recordNo, "bookShelf"));

        System.out.println(engine.clickObject(recordNo, "clock"));

        System.out.println(engine.submitAnswer(recordNo, 1, "815"));

        System.out.println(engine.clickObject(recordNo, "globe"));

        System.out.println(engine.clickObject(recordNo, "drawer"));

        System.out.println(engine.submitAnswer(recordNo, 2, "2580"));

        System.out.println(engine.clickObject(recordNo, "exitDoor"));

	}

}
