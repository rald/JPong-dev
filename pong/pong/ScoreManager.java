import java.io.*;
import java.util.Vector;

/**
 * Manages high score persistence securely using a custom legacy-compatible hash 
 * function for Java 1.0.2 tamper resistance.
 */
public class ScoreManager {
    private static final String SCORE_FILE = "scores.txt";
    private static final int MAX_SCORES = 5;

    public static Vector loadScores() {
        Vector scores = new Vector();
        File file = new File(SCORE_FILE);
        if (!file.exists()) {
            return scores;
        }

        DataInputStream dis = null;
        try {
            dis = new DataInputStream(new FileInputStream(file));
            String line;
            while ((line = readLine(dis)) != null) {
                line = line.trim();
                if (line.length() == 0) continue;

                int firstColon = line.indexOf(':');
                int lastColon = line.lastIndexOf(':');
                if (firstColon != -1 && lastColon != -1 && firstColon != lastColon) {
                    String name = line.substring(0, firstColon);
                    String scoreStr = line.substring(firstColon + 1, lastColon);
                    String checksumStr = line.substring(lastColon + 1);

                    try {
                        int score = Integer.parseInt(scoreStr);
                        long expectedChecksum = Long.parseLong(checksumStr);

                        // Verify integrity using custom hash signature
                        String payload = name + ":" + score;
                        if (calculateChecksum(payload) == expectedChecksum) {
                            scores.addElement(new ScoreEntry(name, score));
                        }
                    } catch (NumberFormatException e) {
                        // Skip tampered/corrupted lines
                    }
                }
            }
        } catch (Exception e) {
            // Fallback on read errors
        } finally {
            if (dis != null) {
                try { dis.close(); } catch (IOException ignored) {}
            }
        }
        return scores;
    }

    /**
     * Convenience method to add a single score entry, sort high scores, and save.
     */
    public static void saveScore(String name, int score) {
        Vector scores = loadScores();
        scores.addElement(new ScoreEntry(name, score));
        
        // Simple bubble sort descending by score (Java 1.0 compatible)
        for (int i = 0; i < scores.size() - 1; i++) {
            for (int j = i + 1; j < scores.size(); j++) {
                ScoreEntry e1 = (ScoreEntry) scores.elementAt(i);
                ScoreEntry e2 = (ScoreEntry) scores.elementAt(j);
                if (e2.getScore() > e1.getScore()) {
                    scores.setElementAt(e2, i);
                    scores.setElementAt(e1, j);
                }
            }
        }
        
        saveScores(scores);
    }

    public static void saveScores(Vector scores) {
        PrintStream ps = null;
        try {
            ps = new PrintStream(new FileOutputStream(SCORE_FILE));
            for (int i = 0; i < scores.size() && i < MAX_SCORES; i++) {
                ScoreEntry entry = (ScoreEntry) scores.elementAt(i);
                String payload = entry.getName() + ":" + entry.getScore();
                long checksum = calculateChecksum(payload);
                ps.println(payload + ":" + checksum);
            }
        } catch (Exception e) {
            // Handle save errors
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    /**
     * Custom lightweight djb2 hash function fully compatible with Java 1.0.2.
     */
    private static long calculateChecksum(String data) {
        long hash = 5381;
        int len = data.length();
        for (int i = 0; i < len; i++) {
            hash = ((hash << 5) + hash) + data.charAt(i);
        }
        return hash;
    }

    private static String readLine(DataInputStream dis) throws IOException {
        try {
            return dis.readLine();
        } catch (NoSuchMethodError e) {
            return null;
        }
    }
}