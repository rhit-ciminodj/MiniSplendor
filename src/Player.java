import java.util.EnumMap;
import java.util.Map;

/**
 * Represents a player in the game.
 * Player objects are created during game initialization only.
 */
public class Player {
    private int score;
    private Map<ChipColor, Integer> chips;

    public Player() {
        this.score = 0;
        this.chips = new EnumMap<>(ChipColor.class);
        // Initialize all chip counts to 0
        for (ChipColor color : ChipColor.values()) {
            chips.put(color, 0);
        }
    }

    public int getScore() {
        return score;
    }

    /**
     * Updates the player's score by adding the specified points.
     * @param points The points to add to the score
     */
    public void updateScore(int points) {
        this.score += points;
    }

    public Map<ChipColor, Integer> getChips() {
        return new EnumMap<>(chips);
    }

    /**
     * Updates chips by adding one chip of the specified color.
     * The one that was taken.
     * @param color The color of chip to add
     */
    public void updateChips(ChipColor color) {
        chips.put(color, chips.get(color) + 1);
    }

    /**
     * Removes chips from the player (used when buying a card).
     * @param color The color of chip to remove
     * @param amount The number of chips to remove
     */
    public void removeChips(ChipColor color, int amount) {
        int current = chips.get(color);
        chips.put(color, Math.max(0, current - amount));
    }

    /**
     * Sets the score directly (used for loading game state).
     * @param score The score to set
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Sets chips directly (used for loading game state).
     * @param chips The chips map to set
     */
    public void setChips(Map<ChipColor, Integer> chips) {
        this.chips = new EnumMap<>(chips);
    }
}
