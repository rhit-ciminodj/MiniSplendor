import java.io.*;
import java.util.*;

/**
 * Handles persistence only.
 * Saving and loading are triggered through BusinessLogicLayer.
 */
public class SaveGameStateToFile {

    /**
     * Saves the game state to a file.
     * @param gameState The game state to save
     * @param filename The name of the file to save to
     */
    public void save(GameState gameState, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Save current player index
            writer.println("CURRENT_PLAYER_INDEX:" + gameState.getCurrentPlayerIndex());

            // Save chips taken this turn
            writer.print("CHIPS_TAKEN:");
            Map<ChipColor, Integer> chipsTaken = gameState.getChipsTakenThisTurn();
            for (ChipColor color : ChipColor.values()) {
                writer.print(color.name() + "=" + chipsTaken.get(color) + ";");
            }
            writer.println();

            // Save players
            List<Player> players = gameState.getPlayers();
            writer.println("PLAYER_COUNT:" + players.size());
            for (int i = 0; i < players.size(); i++) {
                Player player = players.get(i);
                writer.println("PLAYER:" + i);
                writer.println("SCORE:" + player.getScore());
                writer.print("CHIPS:");
                Map<ChipColor, Integer> chips = player.getChips();
                for (ChipColor color : ChipColor.values()) {
                    writer.print(color.name() + "=" + chips.get(color) + ";");
                }
                writer.println();
            }

            // Save cards
            List<Card> cards = gameState.getCardsOnTable();
            writer.println("CARD_COUNT:" + cards.size());
            for (Card card : cards) {
                writer.println("CARD:" + card.getId());
                writer.println("POINTS:" + card.getPoints());
                writer.print("COST:");
                Map<ChipColor, Integer> cost = card.getChipsToBuy();
                for (ChipColor color : ChipColor.values()) {
                    writer.print(color.name() + "=" + cost.get(color) + ";");
                }
                writer.println();
            }

        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
        }
    }

    /**
     * Loads the game state from a file.
     * @param filename The name of the file to load from
     * @return The loaded game state, or null if loading fails
     */
    public GameState load(String filename) {
        GameState gameState = new GameState();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            List<Player> players = new ArrayList<>();
            List<Card> cards = new ArrayList<>();
            int currentPlayerIndex = 0;
            Map<ChipColor, Integer> chipsTakenThisTurn = new EnumMap<>(ChipColor.class);
            for (ChipColor color : ChipColor.values()) {
                chipsTakenThisTurn.put(color, 0);
            }

            Player currentPlayer = null;
            Card currentCard = null;
            int currentCardId = 0;
            int currentCardPoints = 0;
            Map<ChipColor, Integer> currentCardCost = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("CURRENT_PLAYER_INDEX:")) {
                    currentPlayerIndex = Integer.parseInt(line.substring("CURRENT_PLAYER_INDEX:".length()));
                } else if (line.startsWith("CHIPS_TAKEN:")) {
                    chipsTakenThisTurn = parseChipMap(line.substring("CHIPS_TAKEN:".length()));
                } else if (line.startsWith("PLAYER_COUNT:")) {
                    // Just read the count, players will be created as we parse
                } else if (line.startsWith("PLAYER:")) {
                    if (currentPlayer != null) {
                        players.add(currentPlayer);
                    }
                    currentPlayer = new Player();
                } else if (line.startsWith("SCORE:") && currentPlayer != null) {
                    currentPlayer.setScore(Integer.parseInt(line.substring("SCORE:".length())));
                } else if (line.startsWith("CHIPS:") && currentPlayer != null) {
                    currentPlayer.setChips(parseChipMap(line.substring("CHIPS:".length())));
                } else if (line.startsWith("CARD_COUNT:")) {
                    if (currentPlayer != null) {
                        players.add(currentPlayer);
                        currentPlayer = null;
                    }
                } else if (line.startsWith("CARD:")) {
                    if (currentCardCost != null) {
                        cards.add(new Card(currentCardId, currentCardCost, currentCardPoints));
                    }
                    currentCardId = Integer.parseInt(line.substring("CARD:".length()));
                    currentCardCost = new EnumMap<>(ChipColor.class);
                } else if (line.startsWith("POINTS:") && currentCardCost != null) {
                    currentCardPoints = Integer.parseInt(line.substring("POINTS:".length()));
                } else if (line.startsWith("COST:") && currentCardCost != null) {
                    currentCardCost = parseChipMap(line.substring("COST:".length()));
                }
            }

            // Add last card if exists
            if (currentCardCost != null) {
                cards.add(new Card(currentCardId, currentCardCost, currentCardPoints));
            }

            gameState.setPlayers(players);
            gameState.setCardsOnTable(cards);
            gameState.setCurrentPlayerIndex(currentPlayerIndex);
            gameState.setChipsTakenThisTurn(chipsTakenThisTurn);

            return gameState;

        } catch (IOException e) {
            System.err.println("Error loading game: " + e.getMessage());
            return null;
        }
    }

    /**
     * Parses a chip map from a string format.
     * @param data The string data in format "Color=count;Color=count;..."
     * @return The parsed chip map
     */
    private Map<ChipColor, Integer> parseChipMap(String data) {
        Map<ChipColor, Integer> chips = new EnumMap<>(ChipColor.class);
        for (ChipColor color : ChipColor.values()) {
            chips.put(color, 0);
        }

        String[] pairs = data.split(";");
        for (String pair : pairs) {
            if (pair.contains("=")) {
                String[] parts = pair.split("=");
                if (parts.length == 2) {
                    try {
                        ChipColor color = ChipColor.valueOf(parts[0].trim());
                        int count = Integer.parseInt(parts[1].trim());
                        chips.put(color, count);
                    } catch (IllegalArgumentException e) {
                        // Skip invalid entries
                    }
                }
            }
        }
        return chips;
    }
}
