
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Represents a player in the chess game.
 * Each player has a color and a list of available pieces.
 */
public class Player {

    /** The color assigned to the player. */
    private Color color;

    /** The pieces that have not been captured. */
    private List<Piece> availablePieces;

    /** Scanner used to read player moves. */
    private static final Scanner input = new Scanner(System.in);

    /**
     * Creates a player with the given color.
     *
     * @param color the player's color
     */
    public Player(Color color) {
        this.color = color;
        availablePieces = new ArrayList<>();
    }

    /**
     * Returns the player's color.
     *
     * @return the color of the player
     */
    public Color getColor() {
        return color;
    }

    /**
     * Returns the player's available pieces.
     *
     * @return list of pieces that have not been captured
     */
    public List<Piece> getAvailablePieces() {
        return availablePieces;
    }

    /**
     * Reads a move from the player and attempts to
     * move the piece on the board.
     *
     * @param board the chessboard
     */
    public void makeMove(Board board) {

        boolean validMove = false;

        while (!validMove) {

            System.out.print("Enter move (example: E2 E4): ");
            String move = input.nextLine().trim().toUpperCase();

            String[] positions = move.split("\\s+");

            if (positions.length != 2) {
                System.out.println("Invalid format. Try again.");
                continue;
            }

            String fromSquare = positions[0];
            String toSquare = positions[1];

            if (!fromSquare.matches("[A-H][1-8]") ||
                !toSquare.matches("[A-H][1-8]")) {

                System.out.println("Invalid position. Try again.");
                continue;
            }

            Position from = new Position(
                fromSquare.substring(0, 1),
                Integer.parseInt(fromSquare.substring(1))
            );

            Position to = new Position(
                toSquare.substring(0, 1),
                Integer.parseInt(toSquare.substring(1))
            );

            Piece piece = board.getPiece(from);

            if (piece == null) {
                System.out.println("There is no piece at that position.");
                continue;
            }

            if (piece.getColor() != color) {
                System.out.println("You cannot move the other player's piece.");
                continue;
            }

            board.movePiece(from, to);
            validMove = true;

            System.out.println("Move completed.");
        }
    }
}
