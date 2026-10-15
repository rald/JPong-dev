import sun.audio.*;
import java.io.*;

/**
 * High-performance sound manager for Java 1.0.2 with instant audio streaming.
 */
public class SoundManager {
    private static AudioData paddleHitData = null;
    private static AudioData wallHitData = null;
    private static AudioData gameOverData = null;

    static {
        paddleHitData = loadAudioData("hit.au");
        wallHitData = loadAudioData("hit.au");
        gameOverData = loadAudioData("gameover.au");
    }

    private static AudioData loadAudioData(String filename) {
        try {
            File file = new File(filename);
            if (!file.exists()) {
                return null;
            }
            InputStream in = new FileInputStream(file);
            AudioStream as = new AudioStream(in);
            AudioData data = as.getData();
            in.close();
            return data;
        } catch (Exception e) {
            return null;
        }
    }

    public static void playPaddleHit() {
        if (paddleHitData != null) {
            // Instantiate and start a fresh stream immediately on collision thread
            AudioPlayer.player.start(new AudioDataStream(paddleHitData));
        } else {
            fallbackBeep();
        }
    }

    public static void playWallHit() {
        if (wallHitData != null) {
            AudioPlayer.player.start(new AudioDataStream(wallHitData));
        } else {
            fallbackBeep();
        }
    }

    public static void playGameOver() {
        if (gameOverData != null) {
            AudioPlayer.player.start(new AudioDataStream(gameOverData));
        } else {
            fallbackBeep();
        }
    }

    private static void fallbackBeep() {
        System.out.print("\007");
        System.out.flush();
    }
}