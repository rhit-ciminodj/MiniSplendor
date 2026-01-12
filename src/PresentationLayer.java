import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles user interaction only using Java Swing GUI.
 * PresentationLayer must never create or modify Player, Card, or GameState directly.
 * GameAction objects are created only by PresentationLayer.
 * The Presentation Layer may reference cards only by ID, not by constructing new objects.
 */
public class PresentationLayer {
    private BusinessLogicLayer logic;
    
    // Main frame
    private JFrame frame;
    
    // Panels
    private JPanel mainPanel;
    private JPanel playersPanel;
    private JPanel cardsPanel;
    private JPanel chipsPanel;
    private JPanel actionsPanel;
    private JPanel statusPanel;
    
    // Labels for display
    private JLabel currentPlayerLabel;
    private JLabel chipsTakenLabel;
    private JLabel statusLabel;
    private JLabel[] playerScoreLabels;
    private JLabel[] playerChipsLabels;
    
    // Buttons for chip colors
    private Map<ChipColor, JButton> chipButtons;
    
    // Buttons for cards (by ID)
    private Map<Integer, JButton> cardButtons;
    
    // Action buttons
    private JButton endTurnButton;
    private JButton newGameButton;
    private JButton saveGameButton;
    private JButton loadGameButton;

    public PresentationLayer(BusinessLogicLayer logic) {
        this.logic = logic;
        this.chipButtons = new HashMap<>();
        this.cardButtons = new HashMap<>();
        initializeGUI();
    }

    /**
     * Initializes the Swing GUI components.
     */
    private void initializeGUI() {
        // Create main frame
        frame = new JFrame("Mini Splendor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 750);
        frame.setLocationRelativeTo(null);
        
        // Add window listener for close event
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing();
            }
        });

        // Create main panel with BorderLayout
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create and add sub-panels
        createPlayersPanel();
        createCardsPanel();
        createChipsPanel();
        createActionsPanel();
        createStatusPanel();

        // Layout the main panel
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(cardsPanel, BorderLayout.CENTER);
        centerPanel.add(chipsPanel, BorderLayout.SOUTH);

        mainPanel.add(playersPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(actionsPanel, BorderLayout.EAST);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
    }

    /**
     * Creates the players information panel.
     */
    private void createPlayersPanel() {
        playersPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        playersPanel.setBorder(BorderFactory.createTitledBorder("Players"));

        currentPlayerLabel = new JLabel("Current Player: --", SwingConstants.CENTER);
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 16));

        playerScoreLabels = new JLabel[2];
        playerChipsLabels = new JLabel[2];

        for (int i = 0; i < 2; i++) {
            JPanel playerPanel = new JPanel(new GridLayout(3, 1));
            playerPanel.setBorder(BorderFactory.createTitledBorder("Player " + (i + 1)));
            
            playerScoreLabels[i] = new JLabel("Score: 0");
            playerScoreLabels[i].setFont(new Font("Arial", Font.PLAIN, 14));
            
            playerChipsLabels[i] = new JLabel("Chips: --");
            playerChipsLabels[i].setFont(new Font("Arial", Font.PLAIN, 12));
            
            playerPanel.add(playerScoreLabels[i]);
            playerPanel.add(playerChipsLabels[i]);
            
            playersPanel.add(playerPanel);
        }

        playersPanel.add(currentPlayerLabel);
    }

    /**
     * Creates the cards display panel for 15 cards.
     */
    private void createCardsPanel() {
        cardsPanel = new JPanel(new GridLayout(3, 5, 8, 8));
        cardsPanel.setBorder(BorderFactory.createTitledBorder("Available Cards (Click to Buy)"));
    }

    /**
     * Creates the chip selection panel with colored buttons.
     */
    private void createChipsPanel() {
        chipsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        chipsPanel.setBorder(BorderFactory.createTitledBorder("Take Chips (Click to Take)"));

        chipsTakenLabel = new JLabel("Chips taken this turn: 0/3");
        chipsTakenLabel.setFont(new Font("Arial", Font.BOLD, 12));
        chipsPanel.add(chipsTakenLabel);

        // Create colored buttons for each chip color
        for (ChipColor color : ChipColor.values()) {
            JButton chipButton = createChipButton(color);
            chipButtons.put(color, chipButton);
            chipsPanel.add(chipButton);
        }
    }

    /**
     * Creates a colored button for a chip color.
     */
    private JButton createChipButton(ChipColor color) {
        JButton button = new JButton(color.name());
        button.setPreferredSize(new Dimension(80, 40));
        button.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Set button colors
        switch (color) {
            case Red:
                button.setBackground(Color.RED);
                button.setForeground(Color.WHITE);
                break;
            case Blue:
                button.setBackground(Color.BLUE);
                button.setForeground(Color.WHITE);
                break;
            case Green:
                button.setBackground(new Color(0, 150, 0));
                button.setForeground(Color.WHITE);
                break;
            case Black:
                button.setBackground(Color.BLACK);
                button.setForeground(Color.WHITE);
                break;
            case White:
                button.setBackground(Color.WHITE);
                button.setForeground(Color.BLACK);
                break;
        }
        
        button.setOpaque(true);
        button.setBorderPainted(true);

        // Add action listener - creates GameAction (only PresentationLayer creates GameAction)
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleUserInput(ActionType.TakeChip, color, -1);
            }
        });

        return button;
    }

    /**
     * Creates the actions panel with game control buttons.
     */
    private void createActionsPanel() {
        actionsPanel = new JPanel(new GridLayout(6, 1, 5, 10));
        actionsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        actionsPanel.setPreferredSize(new Dimension(150, 0));

        endTurnButton = new JButton("End Turn");
        endTurnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleEndTurnButton();
            }
        });

        newGameButton = new JButton("New Game");
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleNewGameButton();
            }
        });

        saveGameButton = new JButton("Save Game");
        saveGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSaveGameButton();
            }
        });

        loadGameButton = new JButton("Load Game");
        loadGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLoadGameButton();
            }
        });

        actionsPanel.add(endTurnButton);
        actionsPanel.add(new JLabel()); // Spacer
        actionsPanel.add(newGameButton);
        actionsPanel.add(saveGameButton);
        actionsPanel.add(loadGameButton);
    }

    /**
     * Creates the status panel for messages.
     */
    private void createStatusPanel() {
        statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createTitledBorder("Status"));
        
        statusLabel = new JLabel("Welcome to Mini Splendor! Click 'New Game' to start.");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusPanel.add(statusLabel);
    }

    /**
     * Displays the current game state to the user.
     */
    public void display() {
        GameState gameState = logic.getGameState();
        
        // Update current player label
        int currentIndex = gameState.getCurrentPlayerIndex();
        currentPlayerLabel.setText("▶ Current Turn: Player " + (currentIndex + 1));

        // Update player information
        List<Player> players = gameState.getPlayers();
        for (int i = 0; i < players.size() && i < 2; i++) {
            Player player = players.get(i);
            String marker = (i == currentIndex) ? " ◀ ACTIVE" : "";
            playerScoreLabels[i].setText("Score: " + player.getScore() + marker);
            
            StringBuilder chipsStr = new StringBuilder("Chips: ");
            Map<ChipColor, Integer> chips = player.getChips();
            for (ChipColor color : ChipColor.values()) {
                int count = chips.get(color);
                if (count > 0) {
                    chipsStr.append(color.name().charAt(0)).append("=").append(count).append(" ");
                }
            }
            playerChipsLabels[i].setText(chipsStr.toString());
        }

        // Update chips taken this turn
        Map<ChipColor, Integer> chipsTaken = gameState.getChipsTakenThisTurn();
        int totalTaken = 0;
        int maxOfOne = 0;
        StringBuilder takenStr = new StringBuilder();
        for (ChipColor color : ChipColor.values()) {
            int count = chipsTaken.get(color);
            totalTaken += count;
            maxOfOne = Math.max(maxOfOne, count);
            if (count > 0) {
                takenStr.append(color.name()).append("x").append(count).append(" ");
            }
        }
        String mode = (maxOfOne >= 2) ? "(2 same - DONE)" : 
                       (totalTaken >= 3) ? "(3 diff - DONE)" : 
                       (totalTaken > 0 && maxOfOne == 1) ? "(taking different)" : "";
        chipsTakenLabel.setText("Taken: " + takenStr.toString() + mode);

        // Update cards panel
        updateCardsPanel();

        // Refresh the frame
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Updates the cards panel with current available cards.
     */
    private void updateCardsPanel() {
        cardsPanel.removeAll();
        cardButtons.clear();

        GameState gameState = logic.getGameState();
        List<Card> cards = gameState.getCardsOnTable();

        if (cards.isEmpty()) {
            JLabel noCardsLabel = new JLabel("No cards available", SwingConstants.CENTER);
            cardsPanel.add(noCardsLabel);
        } else {
            for (Card card : cards) {
                JButton cardButton = createCardButton(card);
                cardButtons.put(card.getId(), cardButton);
                cardsPanel.add(cardButton);
            }
        }

        // Fill empty slots (15 card slots total)
        for (int i = cards.size(); i < 15; i++) {
            cardsPanel.add(new JLabel());
        }
    }

    /**
     * Creates a button for a card (referenced by ID only).
     */
    private JButton createCardButton(Card card) {
        StringBuilder buttonText = new StringBuilder("<html><center>");
        buttonText.append("<b>Card ID: ").append(card.getId()).append("</b><br>");
        buttonText.append("Points: ").append(card.getPoints()).append("<br>");
        buttonText.append("Cost:<br>");
        
        Map<ChipColor, Integer> cost = card.getChipsToBuy();
        for (ChipColor color : ChipColor.values()) {
            int amount = cost.get(color);
            if (amount > 0) {
                buttonText.append(color.name()).append("=").append(amount).append(" ");
            }
        }
        buttonText.append("</center></html>");

        JButton button = new JButton(buttonText.toString());
        button.setFont(new Font("Arial", Font.PLAIN, 11));
        
        // Store card ID for action handling (reference by ID only)
        final int cardId = card.getId();
        
        // Add action listener - creates GameAction referencing card by ID only
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleUserInput(ActionType.BuyCard, null, cardId);
            }
        });

        return button;
    }

    /**
     * Updates the display after an action.
     */
    public void update() {
        display();
    }

    /**
     * Handles user input from button clicks and creates appropriate GameAction objects.
     * GameAction objects are created only by PresentationLayer.
     * No rule logic exists in the Presentation Layer - all validation is done by BusinessLogicLayer.
     */
    public void handleUserInput(ActionType actionType, ChipColor chipColor, int cardId) {
        GameAction action;
        
        if (actionType == ActionType.TakeChip) {
            // Create GameAction for taking a chip (only PresentationLayer creates GameAction)
            action = new GameAction(ActionType.TakeChip, chipColor);
            
            // Validate through BusinessLogicLayer (no rule logic in Presentation Layer)
            if (logic.validateInput(action)) {
                logic.executeInput(action);
                logic.finalizeAction();
                setStatus("Took a " + chipColor.name() + " chip.");
            } else {
                setStatus("Cannot take " + chipColor.name() + " chip. Invalid action.");
            }
        } else if (actionType == ActionType.BuyCard) {
            // Create GameAction referencing card by ID only (not by constructing new objects)
            action = new GameAction(ActionType.BuyCard, cardId);
            
            // Validate through BusinessLogicLayer (no rule logic in Presentation Layer)
            if (logic.validateInput(action)) {
                logic.executeInput(action);
                logic.endTurn(); // Buying a card ends the turn
                setStatus("Bought card ID " + cardId + ". Turn ended.");
            } else {
                setStatus("Cannot buy card ID " + cardId + ". Not enough chips.");
            }
        }
        
        // Update the display
        update();
    }

    /**
     * Handles the End Turn button click.
     */
    private void handleEndTurnButton() {
        logic.endTurn();
        setStatus("Turn ended.");
        update();
    }

    /**
     * Handles the New Game button click.
     */
    private void handleNewGameButton() {
        logic.newGame();
        setStatus("New game started!");
        update();
    }

    /**
     * Handles the Save Game button click.
     */
    private void handleSaveGameButton() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Game");
        
        int result = fileChooser.showSaveDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            String filename = fileChooser.getSelectedFile().getAbsolutePath();
            logic.saveGame(filename);
            setStatus("Game saved to " + filename);
        }
    }

    /**
     * Handles the Load Game button click.
     */
    private void handleLoadGameButton() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Game");
        
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            String filename = fileChooser.getSelectedFile().getAbsolutePath();
            logic.loadGame(filename);
            setStatus("Game loaded from " + filename);
            update();
        }
    }

    /**
     * Handles window closing event.
     */
    private void handleWindowClosing() {
        int result = JOptionPane.showConfirmDialog(
            frame,
            "Do you want to save the game before exiting?",
            "Exit Game",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            handleSaveGameButton();
            System.exit(0);
        } else if (result == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
        // Cancel - do nothing, stay in game
    }

    /**
     * Sets the status message.
     */
    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    /**
     * Runs the game - makes the frame visible and starts a new game.
     */
    public void run() {
        // Use SwingUtilities to ensure thread safety
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                logic.newGame();
                frame.setVisible(true);
                display();
            }
        });
    }
}
