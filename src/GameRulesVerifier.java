import java.util.Map;

/**
 * Enforces game rules but does not store long-term state.
 * All rule validation must occur in GameRulesVerifier.
 */
public class GameRulesVerifier {
    private GameState gameState;

    public GameRulesVerifier(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * Verifies if a player can buy a specific card.
     * @param player The player attempting to buy
     * @param card The card to buy
     * @return true if the player has enough chips to buy the card
     */
    public boolean verifyBuyAction(Player player, Card card) {
        if (player == null || card == null) {
            return false;
        }

        Map<ChipColor, Integer> playerChips = player.getChips();
        Map<ChipColor, Integer> cardCost = card.getChipsToBuy();

        for (ChipColor color : ChipColor.values()) {
            int required = cardCost.getOrDefault(color, 0);
            int available = playerChips.getOrDefault(color, 0);
            if (available < required) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verifies if a chip of the specified color can be taken.
     * Rules: 
     * - Take 3 chips of different colors (one of each), OR
     * - Take 2 chips of the same color
     * @param color The color of chip to take
     * @return true if the chip can be taken
     */
    public boolean verifyTakeChipAction(ChipColor color) {
        if (color == null) {
            return false;
        }

        Map<ChipColor, Integer> chipsTaken = gameState.getChipsTakenThisTurn();
        int totalChipsTaken = 0;
        int colorsWithChips = 0;
        int maxOfOneColor = 0;
        
        for (ChipColor c : ChipColor.values()) {
            int count = chipsTaken.get(c);
            totalChipsTaken += count;
            if (count > 0) {
                colorsWithChips++;
                maxOfOneColor = Math.max(maxOfOneColor, count);
            }
        }
        
        int currentColorCount = chipsTaken.get(color);
        
        // If already took 2 of the same color, turn chip-taking is done
        if (maxOfOneColor >= 2) {
            return false;
        }
        
        // If already took 3 chips (must be 3 different colors), no more chips
        if (totalChipsTaken >= 3) {
            return false;
        }
        
        // If we have chips of multiple colors, we're in "3 different colors" mode
        if (colorsWithChips > 1) {
            // Already took chips of different colors - can only take different colors
            if (currentColorCount > 0) {
                return false; // Can't take same color again
            }
            return true; // Can take a new color (up to 3 total)
        }
        
        // If we have 1 chip of one color
        if (colorsWithChips == 1) {
            if (currentColorCount == 1) {
                // Taking second of same color - allowed (2 same color mode)
                return true;
            } else {
                // Taking a different color - switches to "3 different" mode
                return true;
            }
        }
        
        // No chips taken yet - can take any color
        return true;
    }

    /**
     * Updates the chips taken this turn (delegates to GameState).
     * @param color The color of chip that was taken
     */
    public void updateChipsTaken(ChipColor color) {
        gameState.addChipTakenThisTurn(color);
    }

    /**
     * Resets the chips taken counter (delegates to GameState).
     */
    public void resetChipsTaken() {
        gameState.resetChipsTakenThisTurn();
    }

    /**
     * Verifies if the turn should end based on chips taken.
     * Turn ends after:
     * - Taking 3 chips of different colors, OR
     * - Taking 2 chips of the same color, OR
     * - Buying a card (handled separately in BusinessLogicLayer)
     * @param chipsTaken The map of chips taken this turn
     * @return true if the turn should end due to chip taking
     */
    public boolean verifyEndTurn(Map<ChipColor, Integer> chipsTaken) {
        int totalChipsTaken = 0;
        int maxOfOneColor = 0;
        
        for (int count : chipsTaken.values()) {
            totalChipsTaken += count;
            maxOfOneColor = Math.max(maxOfOneColor, count);
        }
        
        // Turn ends when player has taken 2 of the same color
        if (maxOfOneColor >= 2) {
            return true;
        }
        
        // Turn ends when player has taken 3 different chips
        if (totalChipsTaken >= 3) {
            return true;
        }
        
        return false;
    }

    /**
     * Sets the game state reference (used when loading a game).
     * @param gameState The new game state
     */
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
}
