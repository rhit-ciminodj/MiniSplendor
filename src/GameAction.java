/**
 * Represents a transient action created by PresentationLayer.
 * GameAction objects are consumed by BusinessLogicLayer and discarded after execution.
 */
public class GameAction {
    private ActionType type;
    private int cardID;
    private ChipColor chipColor;

    /**
     * Constructor for a TakeChip action.
     * @param type The action type (should be TakeChip)
     * @param chipColor The color of chip to take
     */
    public GameAction(ActionType type, ChipColor chipColor) {
        this.type = type;
        this.chipColor = chipColor;
        this.cardID = -1;
    }

    /**
     * Constructor for a BuyCard action.
     * @param type The action type (should be BuyCard)
     * @param cardID The ID of the card to buy
     */
    public GameAction(ActionType type, int cardID) {
        this.type = type;
        this.cardID = cardID;
        this.chipColor = null;
    }

    public ActionType getType() {
        return type;
    }

    public int getCardID() {
        return cardID;
    }

    public ChipColor getChipColor() {
        return chipColor;
    }
}
