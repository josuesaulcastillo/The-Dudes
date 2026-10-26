
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
        whitePlayer = new Player(Color.WHITE);
        blackPlayer = new Player(Color.BLACK);
        currentTurn = Color.WHITE;
        gameRunning = false;
    }

    /**
     * Starts the chess game.
     */
    public void start() {
        currentTurn = Color.WHITE;
        gameRunning = true;

        System.out.println("Welcome to Chess!");
        System.out.println("White moves first.");

        play();
    }

    /**
     * Ends the chess game.
     * Announces the winner or a draw.
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

            System.out.println(currentTurn + "'s turn.");

            if (board.isCheckmate(currentTurn)) {
                System.out.println(currentTurn + " is in checkmate!");
                end();
                break;
            }

            if (board.isCheck(currentTurn)) {
                System.out.println(currentTurn + " is in check!");
            }

            if (currentTurn == Color.WHITE) {
                whitePlayer.makeMove(board);
                currentTurn = Color.BLACK;
            } else {
                blackPlayer.makeMove(board);
                currentTurn = Color.WHITE;
            }
        }
    }
}
