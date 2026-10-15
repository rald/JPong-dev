/**
 * Data transfer object representing a player score entry.
 */
public class ScoreEntry {
    private String name; // Removed final for Java 1.0.2 compiler compatibility
    private int score;   // Removed final for Java 1.0.2 compiler compatibility

    public ScoreEntry(String name, int score) {
        this.name = (name != null && name.trim().length() > 0) ? name.trim() : "Player";
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }
}