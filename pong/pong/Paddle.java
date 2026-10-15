import java.awt.Graphics;
import java.awt.Color;

/**
 * Encapsulates paddle geometry and bounds-checked mouse movement.
 */
public class Paddle {
    private int x;
    private int y;
    private int width;
    private int height;

    public Paddle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void moveTo(int mouseX, int clientWidth) {
        x = mouseX - (width / 2);
        if (x < 0) {
            x = 0;
        }
        if (x > clientWidth - width) {
            x = clientWidth - width;
        }
    }

    public void draw(Graphics g) {
        if (g != null) {
            g.setColor(Color.white);
            g.fillRect(x, y, width, height);
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}