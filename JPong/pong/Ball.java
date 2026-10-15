import java.awt.Graphics;
import java.awt.Color;

/**
 * Encapsulates frame-rate independent ball physics, collisions, and sound triggers.
 */
public class Ball {
    private float x;
    private float y;
    private int size;
    private float vx;
    private float vy;

    private static final float INITIAL_VX = 220.0f;
    private static final float INITIAL_VY = -220.0f;

    public Ball(float x, float y, int size, float vx, float vy) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.vx = vx;
        this.vy = vy;
    }

    public boolean update(float deltaTime, int clientWidth, int clientHeight, Paddle paddle, ScoreCallback callback) {
        x += vx * deltaTime;
        y += vy * deltaTime;

        // Wall collisions with sound
        if (x <= 0) {
            x = 0;
            vx = -vx;
//            SoundManager.playWallHit();
        }
        if (x >= clientWidth - size) {
            x = clientWidth - size;
            vx = -vx;
//            SoundManager.playWallHit();
        }
        if (y <= 0) {
            y = 0;
            vy = -vy;
//            SoundManager.playWallHit();
        }

        // Paddle collision with sound
        int intX = (int) x;
        int intY = (int) y;
        if (intY + size >= paddle.getY() && intY + size <= paddle.getY() + paddle.getHeight() + 5) {
            if (intX + size >= paddle.getX() && intX <= paddle.getX() + paddle.getWidth()) {
                y = paddle.getY() - size;
                vy = -vy;
                SoundManager.playPaddleHit();
                if (callback != null) {
                    callback.onScorePoint();
                }
            }
        }

        // Returns true if ball goes past bottom boundary (Game Over condition)
        if (y >= clientHeight) {
            SoundManager.playGameOver();
            return true;
        }

        return false;
    }

    public void reset(int clientWidth) {
        x = clientWidth / 2.0f;
        y = 100.0f;
        vx = INITIAL_VX;
        vy = INITIAL_VY;
    }

    public void draw(Graphics g) {
        if (g != null) {
            g.setColor(Color.white);
            g.fillRect((int) x, (int) y, size, size);
        }
    }
}