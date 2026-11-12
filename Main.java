import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Entry point for the chess game.
 */
public class Main {
    /** Starts the graphical chess game. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Board board = new Board();
            ChessBoardGUI chessBoardGUI = new ChessBoardGUI(board);
            new ChessPieceMovement(board, chessBoardGUI);

            JFrame frame = new JFrame("Chess");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(chessBoardGUI);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
