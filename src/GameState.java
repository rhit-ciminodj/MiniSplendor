import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Stores all mutable game state.
 * GameState must not know about file I/O.
 */
public class GameState {
    private List<Player> players;
    private List<Card> cardsOnTable;
    private int currentPlayerIndex;
    private Map<ChipColor, Integer> chipsTakenThisTurn;

    public GameState() {
        this.players = new ArrayList<>();
        this.cardsOnTable = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.chipsTakenThisTurn = new EnumMap<>(ChipColor.class);
        resetChipsTakenThisTurn();
    }

    public Player getCurrentPlayer() {
        if (players.isEmpty()) {
            return null;
        }
        return players.get(currentPlayerIndex);
    }

    public void advanceToNextPlayer() {
        if (!players.isEmpty()) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }
        resetChipsTakenThisTurn();
    }

    /**
     * Initializes a new game with players and cards.
     */
    public void newGame() {
        resetPlayers();
        resetCards();
        currentPlayerIndex = 0;
        resetChipsTakenThisTurn();
    }

    /**
     * Resets and initializes players for a new game.
     */
    public void resetPlayers() {
        players.clear();
        // Create 2 players for a standard game
        players.add(new Player());
        players.add(new Player());
    }

    /**
     * Resets and initializes 15 cards for a new game.
     * Each card costs either one chip color (1-3) or two chip colors (1-3 each).
     * Cards are worth 1-5 victory points.
     */
    public void resetCards() {
        cardsOnTable.clear();
        int cardId = 0;
        
        // Generate 15 cards with varied costs and points
        // Cards 1-3: Single color costs (1 point each)
        cardsOnTable.add(createCard(cardId++, 1, ChipColor.Red, 2, null, 0));
        cardsOnTable.add(createCard(cardId++, 1, ChipColor.Blue, 3, null, 0));
        cardsOnTable.add(createCard(cardId++, 1, ChipColor.Green, 2, null, 0));
        
        // Cards 4-6: Two color costs (1-2 points each)
        cardsOnTable.add(createCard(cardId++, 1, ChipColor.Black, 1, ChipColor.White, 2));
        cardsOnTable.add(createCard(cardId++, 2, ChipColor.Red, 2, ChipColor.Blue, 1));
        cardsOnTable.add(createCard(cardId++, 2, ChipColor.Green, 1, ChipColor.Black, 2));
        
        // Cards 7-9: Single color costs (2-3 points each)
        cardsOnTable.add(createCard(cardId++, 2, ChipColor.White, 3, null, 0));
        cardsOnTable.add(createCard(cardId++, 3, ChipColor.Black, 3, null, 0));
        cardsOnTable.add(createCard(cardId++, 3, ChipColor.Red, 3, null, 0));
        
        // Cards 10-12: Two color costs (3-4 points each)
        cardsOnTable.add(createCard(cardId++, 3, ChipColor.Blue, 2, ChipColor.Green, 2));
        cardsOnTable.add(createCard(cardId++, 4, ChipColor.White, 2, ChipColor.Red, 3));
        cardsOnTable.add(createCard(cardId++, 4, ChipColor.Black, 3, ChipColor.Blue, 2));
        
        // Cards 13-15: Higher point cards (4-5 points each)
        cardsOnTable.add(createCard(cardId++, 4, ChipColor.Green, 3, ChipColor.White, 2));
        cardsOnTable.add(createCard(cardId++, 5, ChipColor.Red, 3, ChipColor.Black, 3));
        cardsOnTable.add(createCard(cardId++, 5, ChipColor.Blue, 3, ChipColor.Green, 3));
    }
    
    /**
     * Helper method to create a card with one or two color costs.
     * @param id Card ID
     * @param points Victory points (1-5)
     * @param color1 First chip color
     * @param amount1 Amount of first color (1-3)
     * @param color2 Second chip color (null for single color cards)
     * @param amount2 Amount of second color (0 if single color)
     * @return The created card
     */
    private Card createCard(int id, int points, ChipColor color1, int amount1, ChipColor color2, int amount2) {
        Map<ChipColor, Integer> cost = new EnumMap<>(ChipColor.class);
        initializeCostMap(cost);
        cost.put(color1, amount1);
        if (color2 != null && amount2 > 0) {
            cost.put(color2, amount2);
        }
        return new Card(id, cost, points);
    }

    private void initializeCostMap(Map<ChipColor, Integer> cost) {
        for (ChipColor color : ChipColor.values()) {
            cost.put(color, 0);
        }
    }

    /**
     * Removes a card from the table after it has been bought.
     * @param card The card to remove
     */
    public void removeCardAfterBought(Card card) {
        cardsOnTable.remove(card);
    }

    public Map<ChipColor, Integer> getChipsTakenThisTurn() {
        return new EnumMap<>(chipsTakenThisTurn);
    }

    /**
     * Records that a chip of the specified color was taken this turn.
     * @param color The color of chip taken
     */
    public void addChipTakenThisTurn(ChipColor color) {
        chipsTakenThisTurn.put(color, chipsTakenThisTurn.get(color) + 1);
    }

    /**
     * Resets the chips taken this turn counter.
     */
    public void resetChipsTakenThisTurn() {
        for (ChipColor color : ChipColor.values()) {
            chipsTakenThisTurn.put(color, 0);
        }
    }

    // Getters for persistence and game logic
    public List<Player> getPlayers() {
        return players;
    }

    public List<Card> getCardsOnTable() {
        return cardsOnTable;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    /**
     * Finds a card by its ID.
     * @param cardID The ID of the card to find
     * @return The card with the specified ID, or null if not found
     */
    public Card getCardById(int cardID) {
        for (Card card : cardsOnTable) {
            if (card.getId() == cardID) {
                return card;
            }
        }
        return null;
    }

    // Setters for loading game state
    public void setPlayers(List<Player> players) {
        this.players = new ArrayList<>(players);
    }

    public void setCardsOnTable(List<Card> cards) {
        this.cardsOnTable = new ArrayList<>(cards);
    }

    public void setCurrentPlayerIndex(int index) {
        this.currentPlayerIndex = index;
    }

    public void setChipsTakenThisTurn(Map<ChipColor, Integer> chipsTaken) {
        this.chipsTakenThisTurn = new EnumMap<>(chipsTaken);
    }
}
