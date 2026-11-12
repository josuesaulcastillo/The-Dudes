
/**
 * Represents a chess game between two players.
 * This class manages the board, players, and turns.
 */
public class Game {

    /** The chessboard used in the game. */
    private Board board;

    /** The player using the white pieces. */
    private Player whitePlayer;

    /** The player using the black pieces. */
    private Player blackPlayer;

    /** Keeps track of whose turn it is. */
    private Color currentTurn;

    /**
     * Creates a new Game object.
     */
    public Game() {

    }

    /**
     * Initializes the chess game.
     */
    public void start() {

    }

    /**
     * Ends the game and declares a winner or a draw.
     */
    public void end() {

    }

    /**
     * Runs the main game loop.
     * Alternates turns, checks for check and checkmate,
     * and gets moves from the players.
     */
    public void makeMove(Board board) {

        boolean validMove = false;

        while (!validMove) {
            if (!input.hasNextLine()) {
                System.out.println();
                return;
            }

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

            if (!piece.getColor().isColor(color)) {
                System.out.println("You cannot move the other player's piece.");
                continue;
            }

            if (!piece.canMoveTo(board, to)) {
                System.out.println("That piece cannot move to that position.");
                continue;
            }

            boolean hasMoved = board.movePiece(from, to);
            if (hasMoved) {
                validMove = true;
                System.out.println("Move completed.");
            }
        }
    public void play() {

    }
}
