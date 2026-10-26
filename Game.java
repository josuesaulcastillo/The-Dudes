
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

        initializePieces();

        play();
    }

    private void initializePieces() {
        Color white = new Color("white");
        Color black = new Color("black");
        String[] files = {"A", "B", "C", "D", "E", "F", "G", "H"};

        Piece[] whiteBackRank = {
            new Rook(white, new Position("A", 1)),
            new Knight(white, new Position("B", 1)),
            new Bishop(white, new Position("C", 1)),
            new Queen(white, new Position("D", 1)),
            new King(white, new Position("E", 1)),
            new Bishop(white, new Position("F", 1)),
            new Knight(white, new Position("G", 1)),
            new Rook(white, new Position("H", 1))
        };

        Piece[] blackBackRank = {
            new Rook(black, new Position("A", 8)),
            new Knight(black, new Position("B", 8)),
            new Bishop(black, new Position("C", 8)),
            new Queen(black, new Position("D", 8)),
                new King(black, new Position("E", 8)),
                new Bishop(black, new Position("F", 8)),
                new Knight(black, new Position("G", 8)),
                new Rook(black, new Position("H", 8))
            };

            for (int i = 0; i < files.length; i++) {
                board.placePiece(whiteBackRank[i]);
                board.placePiece(new Pawn(white, new Position(files[i], 2)));

                board.placePiece(new Pawn(black, new Position(files[i], 7)));
                board.placePiece(blackBackRank[i]);
            }
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

            System.out.println(currentTurn.color + "'s turn.");

            if (board.isCheckmate(currentTurn)) {
                System.out.println(currentTurn.color + " is in checkmate!");
                end();
                break;
            }

            if (board.isCheck(currentTurn)) {
                System.out.println(currentTurn.color + " is in check!");
            }

            if (currentTurn.isColor("white")) {
                whitePlayer.makeMove(board);
                currentTurn = new Color("black");
            } else {
                blackPlayer.makeMove(board);
                currentTurn = new Color("white");
            }
        }
    }
}
