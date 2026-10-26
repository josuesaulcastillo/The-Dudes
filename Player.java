
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
    public boolean makeMove(Board board) {

        while (true) {

            System.out.print("Enter move (example: E2 E4, HELP, or RESIGN): ");
            if (!input.hasNextLine()) {
                System.out.println("Input closed. Game ended.");
                return false;
            }
            String move = input.nextLine().trim().toUpperCase();

            if (move.equals("RESIGN") || move.equals("QUIT")) {
                System.out.println(color.color + " resigns. "
                    + (color.isColor("white") ? "Black" : "White") + " wins.");
                return false;
            }

            if (move.equals("HELP")) {
                System.out.println("Enter FROM TO, for example E2 E4 or G1 F3.");
                System.out.println("Castling and en passant are not supported.");
                System.out.println("Pawns promote automatically to queens.");
                continue;
            }

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

            if (!piece.getColor().isColor(color)) {
                System.out.println("You cannot move the other player's piece.");
                continue;
            }

            if (!board.movePiece(from, to)) {
                System.out.println("Illegal move. Try again.");
                continue;
            }

            System.out.println("Move completed.");
            return true;
        }
    }
}
