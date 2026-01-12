import java.util.EnumMap;
import java.util.Map;

/**
 * Represents a card in the game.
 * Card objects are created during game initialization only.
 */
public class Card {
    private int id;
    private Map<ChipColor, Integer> chipsToBuy;
    private int points;

    public Card(int id, Map<ChipColor, Integer> chipsToBuy, int points) {
        this.id = id;
        this.chipsToBuy = new EnumMap<>(chipsToBuy);
        this.points = points;
    }

    public int getId() {
        return id;
    }

    public Map<ChipColor, Integer> getChipsToBuy() {
        return new EnumMap<>(chipsToBuy);
    }

    public int getPoints() {
        return points;
    }
}
