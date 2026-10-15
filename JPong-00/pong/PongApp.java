import java.awt.*;
import java.util.Vector;

/**
 * Main application frame coordinating game states, rendering, and input events.
 */
public class PongApp extends Frame implements Runnable, ScoreCallback {
    private Thread gameThread;
    private boolean isRunning = true;

    private Paddle paddle;
    private Ball ball;

    private Image offScreenImage;
    private Graphics offScreenGraphics;

    private static final int DEFAULT_WIDTH = 400;
    private static final int DEFAULT_HEIGHT = 300;

    private static final int STATE_MENU = 0;
    private static final int STATE_PLAYING = 1;
    private int gameState = STATE_MENU;

    private String playerName = "Player";
    private int currentScore = 0;
    private Vector topScores;

    public PongApp() {
        super("Pong");
        resize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setResizable(false);
        
        paddle = new Paddle(170, 240, 60, 10);
        ball = new Ball(200.0f, 150.0f, 10, 220.0f, -220.0f);
        
        topScores = ScoreManager.loadScores();
        
        // Show frame first to establish proper focus context for modal dialog
        show();
        
        NameDialog dialog = new NameDialog(this);
        dialog.show();
        
        gameThread = new Thread(this);
        gameThread.start();
    }

    public synchronized void setPlayerName(String name) {
        if (name != null && name.trim().length() > 0) {
            this.playerName = name.trim();
        }
    }

    public boolean handleEvent(Event e) {
        if (e.id == Event.WINDOW_DESTROY) {
            shutdown();
            return true;
        }
        if (e.id == Event.MOUSE_DOWN && gameState == STATE_MENU) {
            currentScore = 0;
            Dimension d = size();
            Insets ins = insets();
            int clientWidth = d.width - ins.left - ins.right;
            ball.reset(clientWidth);
            gameState = STATE_PLAYING;
            return true;
        }
        return super.handleEvent(e);
    }

    public boolean mouseMove(Event e, int x, int y) {
        if (gameState == STATE_PLAYING) {
            updatePaddlePosition(x);
        }
        return true;
    }

    public boolean mouseDrag(Event e, int x, int y) {
        if (gameState == STATE_PLAYING) {
            updatePaddlePosition(x);
        }
        return true;
    }

    private void updatePaddlePosition(int mouseX) {
        Dimension d = size();
        Insets ins = insets();
        int clientWidth = d.width - ins.left - ins.right;
        paddle.moveTo(mouseX, clientWidth);
        repaint();
    }

    public synchronized void onScorePoint() {
        currentScore++;
    }

    public void run() {
        long lastTime = System.currentTimeMillis();

        while (isRunning) {
            long currentTime = System.currentTimeMillis();
            float deltaTime = (currentTime - lastTime) / 1000.0f;
            lastTime = currentTime;

            // Clamp delta time to avoid physics jumps during thread stutters
            if (deltaTime > 0.1f) {
                deltaTime = 0.1f;
            }

            if (gameState == STATE_PLAYING) {
                Dimension d = size();
                Insets ins = insets();
                int clientWidth = d.width - ins.left - ins.right;
                int clientHeight = d.height - ins.top - ins.bottom;

                boolean gameOver = ball.update(deltaTime, clientWidth, clientHeight, paddle, this);
                if (gameOver) {
                    ScoreManager.saveScore(playerName, currentScore);
                    topScores = ScoreManager.loadScores();
                    gameState = STATE_MENU;
                }
            }

            repaint();

            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                break;
            }
        }
    }

    private synchronized void shutdown() {
        isRunning = false;
        if (gameThread != null) {
            gameThread.interrupt();
        }
        dispose();
        System.exit(0);
    }

    public void update(Graphics g) {
        paint(g);
    }

    public void paint(Graphics g) {
        if (g == null) return;

        Dimension d = size();
        Insets ins = insets();
        int clientWidth = d.width - ins.left - ins.right;
        int clientHeight = d.height - ins.top - ins.bottom;

        if (clientWidth <= 0 || clientHeight <= 0) return;

        if (offScreenImage == null) {
            offScreenImage = createImage(clientWidth, clientHeight);
            if (offScreenImage != null) {
                offScreenGraphics = offScreenImage.getGraphics();
            }
        }

        if (offScreenGraphics != null) {
            offScreenGraphics.setColor(Color.black);
            offScreenGraphics.fillRect(0, 0, clientWidth, clientHeight);

            if (gameState == STATE_MENU) {
                offScreenGraphics.setColor(Color.white);
                offScreenGraphics.drawString("PONG GAME", clientWidth / 2 - 40, 35);
                offScreenGraphics.drawString("Player: " + playerName, clientWidth / 2 - 50, 55);

                offScreenGraphics.drawString("--- TOP RANKINGS ---", clientWidth / 2 - 60, 85);

                int yOffset = 105;
                if (topScores.size() == 0) {
                    offScreenGraphics.drawString("No scores recorded yet", clientWidth / 2 - 65, yOffset);
                } else {
                    for (int i = 0; i < topScores.size() && i < 5; i++) {
                        ScoreEntry entry = (ScoreEntry) topScores.elementAt(i);
                        String line = (i + 1) + ". " + entry.getName() + " - " + entry.getScore();
                        offScreenGraphics.drawString(line, clientWidth / 2 - 50, yOffset);
                        yOffset += 20;
                    }
                }

                offScreenGraphics.setColor(Color.yellow);
                offScreenGraphics.drawString("Click Mouse to Start / Continue", clientWidth / 2 - 85, clientHeight - 20);

            } else {
                offScreenGraphics.setColor(Color.darkGray);
                for (int i = 0; i < clientWidth; i += 20) {
                    offScreenGraphics.fillRect(i, clientHeight / 2 - 1, 10, 2);
                }

                offScreenGraphics.setColor(Color.white);
                offScreenGraphics.drawString("Score: " + currentScore, 20, 30);

                paddle.draw(offScreenGraphics);
                ball.draw(offScreenGraphics);
            }

            g.drawImage(offScreenImage, 0, 0, this);
        }
    }

    public static void main(String[] args) {
        new PongApp();
    }
}