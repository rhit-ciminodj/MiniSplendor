import java.util.Map;

/**
 * Validates and executes actions.
 * BusinessLogicLayer must call rule verification methods before mutating GameState.
 * BusinessLogicLayer consumes but does not store GameAction.
 */
public class BusinessLogicLayer {
    private GameState gameState;
    private GameRulesVerifier rulesVerifier;
    private SaveGameStateToFile fileManager;

    public BusinessLogicLayer() {
        this.gameState = new GameState();
        this.rulesVerifier = new GameRulesVerifier(gameState);
        this.fileManager = new SaveGameStateToFile();
    }

    /**
     * Validates if the given action is allowed according to game rules.
     * All rule validation is delegated to GameRulesVerifier.
     * @param action The action to validate (transient, not stored)
     * @return true if the action is valid
     */
    public boolean validateInput(GameAction action) {
        if (action == null) {
            return false;
        }

        switch (action.getType()) {
            case TakeChip:
                return rulesVerifier.verifyTakeChipAction(action.getChipColor());
            case BuyCard:
                Card card = gameState.getCardById(action.getCardID());
                if (card == null) {
                    return false;
                }
                return rulesVerifier.verifyBuyAction(gameState.getCurrentPlayer(), card);
            default:
                return false;
        }
    }

    /**
     * Executes the given action after validation.
     * Action is consumed and discarded after execution.
     * @param action The action to execute (transient, not stored)
     */
    public void executeInput(GameAction action) {
        if (action == null) {
            return;
        }

        Player currentPlayer = gameState.getCurrentPlayer();
        if (currentPlayer == null) {
            return;
        }

        switch (action.getType()) {
            case TakeChip:
                // Add chip to player
                currentPlayer.updateChips(action.getChipColor());
                // Update chips taken this turn (via GameRulesVerifier which delegates to GameState)
                rulesVerifier.updateChipsTaken(action.getChipColor());
                break;
            case BuyCard:
                Card card = gameState.getCardById(action.getCardID());
                if (card != null) {
                    // Remove chips from player based on card cost
                    Map<ChipColor, Integer> cost = card.getChipsToBuy();
                    for (ChipColor color : ChipColor.values()) {
                        int amount = cost.getOrDefault(color, 0);
                        if (amount > 0) {
                            currentPlayer.removeChips(color, amount);
                        }
                    }
                    // Add points to player
                    currentPlayer.updateScore(card.getPoints());
                    // Remove card from table
                    gameState.removeCardAfterBought(card);
                }
                break;
        }
    }

    /**
     * Handles end-of-turn logic.
     * Checks if turn should end via GameRulesVerifier and advances to next player.
     */
    public void finalizeAction() {
        Map<ChipColor, Integer> chipsTaken = gameState.getChipsTakenThisTurn();
        
        if (rulesVerifier.verifyEndTurn(chipsTaken)) {
            // Advance to next player (this also resets chips taken)
            gameState.advanceToNextPlayer();
            rulesVerifier.resetChipsTaken();
        }
    }

    /**
     * Forces end of turn (used after BuyCard action or manual end turn).
     */
    public void endTurn() {
        gameState.advanceToNextPlayer();
        rulesVerifier.resetChipsTaken();
    }

    /**
     * Saves the current game state to a file.
     * Delegates to SaveGameStateToFile.
     * @param filename The name of the file to save to
     */
    public void saveGame(String filename) {
        fileManager.save(gameState, filename);
    }

    /**
     * Loads a game state from a file.
     * Delegates to SaveGameStateToFile.
     * @param filename The name of the file to load from
     */
    public void loadGame(String filename) {
        GameState loadedState = fileManager.load(filename);
        if (loadedState != null) {
            this.gameState = loadedState;
            this.rulesVerifier.setGameState(loadedState);
        }
    }

    /**
     * Starts a new game.
     */
    public void newGame() {
        gameState.newGame();
    }

    /**
     * Gets the current game state (for display purposes).
     * @return The current game state
     */
    public GameState getGameState() {
        return gameState;
    }
}
