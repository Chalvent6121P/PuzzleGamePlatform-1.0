package util;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;

public final class SoundPlayer {

    private static volatile boolean enabled = true;

    private SoundPlayer() {
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean value) {
        enabled = value;
    }

    public static boolean toggleEnabled() {
        enabled = !enabled;
        return enabled;
    }

    public static void play(String resourcePath) {
        if (!enabled || resourcePath == null || resourcePath.trim().isEmpty()) {
            return;
        }

        Thread thread = new Thread(() -> playInternal(resourcePath), "game-sound");
        thread.setDaemon(true);
        thread.start();
    }

    private static void playInternal(String resourcePath) {
        try {
            URL resource = SoundPlayer.class.getResource(resourcePath);
            if (resource == null) {
                System.err.println("找不到音效資源：" + resourcePath);
                return;
            }

            AudioInputStream stream = AudioSystem.getAudioInputStream(resource);
            Clip clip = AudioSystem.getClip();
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                    try {
                        stream.close();
                    } catch (Exception ignored) {
                        // 關閉失敗不影響遊戲流程。
                    }
                }
            });
            clip.open(stream);
            clip.start();
        } catch (Exception ex) {
            System.err.println("播放音效失敗：" + resourcePath);
            ex.printStackTrace();
        }
    }
}
