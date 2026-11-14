import javax.swing.SwingUtilities;

import GUI.ChessWindow;

/**
 * Entry point for the chess game.
 */
public class Main {
    /** Starts the graphical chess game. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChessWindow().setVisible(true);
        });
    }
}
