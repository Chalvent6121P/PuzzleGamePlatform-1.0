package controller;

import java.awt.EventQueue;

/**
 * PuzzleGamePlatform 的正式啟動入口。
 */
public final class Application {

    private Application() {
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                LoginPage loginPage = new LoginPage();
                loginPage.setVisible(true);
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
        });
    }
}