import javax.swing.SwingUtilities;

/**
 * Main entry point for the Mini Splendor game.
 * Demonstrates the layered architecture:
 * UI (PresentationLayer) → BusinessLogic (BusinessLogicLayer) → GameState
 */
public class Main {
    public static void main(String[] args) {
        // Use SwingUtilities.invokeLater to ensure thread safety for Swing components
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Create the business logic layer (which creates GameState, GameRulesVerifier, SaveGameStateToFile)
                BusinessLogicLayer businessLogic = new BusinessLogicLayer();
                
                // Create the presentation layer with reference to business logic
                // Dependencies: UI → BusinessLogic → GameState (no reverse dependencies)
                PresentationLayer presentation = new PresentationLayer(businessLogic);
                
                // Run the game (displays the Swing GUI)
                presentation.run();
            }
        });
    }
}
