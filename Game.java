
/**
 * Represents a chess game between two players.
 * Manages the board, players, and turns.
 */
public class Game {

    /** The chessboard used in the game. */
    private Board board;

    /** The player using white pieces. */
    private Player whitePlayer;

    /** The player using black pieces. */
    private Player blackPlayer;

    /** Keeps track of whose turn it is. */
    private Color currentTurn;

    /** Keeps track of whether the game is running. */
    private boolean gameRunning;

    /**
     * Creates a new chess game.
     */
    public Game() {
        board = new Board();
        whitePlayer = new Player(new Color("white"));
        blackPlayer = new Player(new Color("black"));
        currentTurn = new Color("white");
        gameRunning = false;
    }

    /**
     * Starts the chess game.
     */
    public void start() {
        currentTurn = new Color("white");
        gameRunning = true;

        System.out.println("Welcome to Chess!");
        System.out.println("White moves first.");

        play();
    }

    /**
     * Ends the chess game.
     * Announces that the game is over.
     */
    public void end() {
        gameRunning = false;
        System.out.println("Game Over!");
    }

    /**
     * Runs the main game loop.
     * Displays the board, gets moves, and switches turns.
     */
    public void play() {

        while (gameRunning) {

            board.display();

            if (!board.hasLegalMove(currentTurn)) {
                if (board.isCheckmate(currentTurn)) {
                    System.out.println("Checkmate! "
                        + (currentTurn.isColor("white") ? "Black" : "White")
                        + " wins.");
                } else {
                    System.out.println("Stalemate! The game is a draw.");
                }
                end();
                break;
            }

            if (board.isCheck(currentTurn)) {
                System.out.println(currentTurn.color + " is in check!");
            }

            String side = currentTurn.isColor("white") ? "White" : "Black";
            System.out.println(side + "'s turn.");

            boolean moved;
            if (currentTurn.isColor("white")) {
                moved = whitePlayer.makeMove(board);
            } else {
                moved = blackPlayer.makeMove(board);
            }

            if (!moved) {
                end();
                break;
            }

            currentTurn = currentTurn.isColor("white")
                ? new Color("black")
                : new Color("white");
        }
    }
}
