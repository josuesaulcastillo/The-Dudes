import java.util.Scanner;

/**
 * Runs a two-player console chess game.
 * This temporary standalone version does not depend on the unfinished team classes.
 */
public class Main {
    private static final char[][] board = new char[8][8];
    private static boolean whiteTurn = true;

    /** Starts the game and reads moves from both players. */
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        setupBoard();
        System.out.println("Welcome to Chess! White moves first.");
        System.out.println("Enter moves like E2 E4, type RESIGN to quit, or HELP for instructions.");

        while (true) {
            displayBoard();
            String side = whiteTurn ? "White" : "Black";

            if (!hasLegalMove(whiteTurn)) {
                if (isInCheck(whiteTurn)) {
                    System.out.println("Checkmate! " + (whiteTurn ? "Black" : "White") + " wins.");
                } else {
                    System.out.println("Stalemate! The game is a draw.");
                }
                break;
            }
            if (isInCheck(whiteTurn)) {
                System.out.println(side + " is in check!");
            }

            System.out.print(side + " move: ");
            if (!input.hasNextLine()) {
                System.out.println("Input closed. Game ended.");
                break;
            }
            String move = input.nextLine().trim().toUpperCase();
            if (move.equals("RESIGN") || move.equals("QUIT")) {
                System.out.println(side + " resigns. " + (whiteTurn ? "Black" : "White") + " wins.");
                break;
            }
            if (move.equals("HELP")) {
                System.out.println("Enter FROM TO, for example E2 E4 or G1 F3.");
                System.out.println("Castling and en passant are not supported in this Phase 1 version.");
                System.out.println("Pawns promote automatically to queens.");
                continue;
            }

            String[] squares = move.split("\\s+");
            if (squares.length != 2 || !squares[0].matches("[A-H][1-8]")
                    || !squares[1].matches("[A-H][1-8]")) {
                System.out.println("Invalid input. Use the format E2 E4.");
                continue;
            }

            int fromRow = '8' - squares[0].charAt(1);
            int fromCol = squares[0].charAt(0) - 'A';
            int toRow = '8' - squares[1].charAt(1);
            int toCol = squares[1].charAt(0) - 'A';

            if (!legalMove(fromRow, fromCol, toRow, toCol, whiteTurn)) {
                System.out.println("Illegal move. Try again.");
                continue;
            }
            makeMove(fromRow, fromCol, toRow, toCol);
            whiteTurn = !whiteTurn;
        }
    }

    /** Places all 32 pieces on their normal starting squares. */
    private static void setupBoard() {
        String blackPieces = "rnbqkbnr";
        String whitePieces = "RNBQKBNR";
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) board[row][col] = '.';
        }
        for (int col = 0; col < 8; col++) {
            board[0][col] = blackPieces.charAt(col);
            board[1][col] = 'p';
            board[6][col] = 'P';
            board[7][col] = whitePieces.charAt(col);
        }
    }

    /** Prints the current chessboard. Capital letters are White's pieces. */
    private static void displayBoard() {
        System.out.println("\n     A  B  C  D  E  F  G  H");
        for (int row = 0; row < 8; row++) {
            System.out.print(" " + (8 - row) + "  ");
            for (int col = 0; col < 8; col++) {
                System.out.print(" " + board[row][col] + " ");
            }
            System.out.println(" " + (8 - row));
        }
