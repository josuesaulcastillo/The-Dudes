
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
        input.close();
    }

    /** Places all 32 pieces on their normal starting squares. */
    private static void setupBoard() {
        String blackPieces = "rnbqkbnr";
        String whitePieces = "RNBQKBNR";

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = '.';
            }
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

        System.out.println("     A  B  C  D  E  F  G  H\n");
    }

    private static boolean onBoard(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    private static boolean isWhite(char piece) {
        return Character.isUpperCase(piece);
    }

    /** Checks how a piece moves, including blocked paths and pawn captures. */
    private static boolean pieceCanMove(int fr, int fc, int tr, int tc, boolean attackOnly) {
        if (!onBoard(fr, fc) || !onBoard(tr, tc) || (fr == tr && fc == tc)) {
            return false;
        }

        char piece = board[fr][fc];

        if (piece == '.') {
            return false;
        }

        char target = board[tr][tc];

        if (!attackOnly && target != '.' && isWhite(piece) == isWhite(target)) {
            return false;
        }

        int dr = tr - fr;
        int dc = tc - fc;
        int ar = Math.abs(dr);
        int ac = Math.abs(dc);

        switch (Character.toUpperCase(piece)) {
            case 'P':
                int direction = isWhite(piece) ? -1 : 1;

                if (attackOnly) {
                    return dr == direction && ac == 1;
                }

                if (dc != 0) {
                    return dr == direction && ac == 1 && target != '.';
                }

                if (target != '.') {
                    return false;
                }

                if (dr == direction) {
                    return true;
                }

                int startingRow = isWhite(piece) ? 6 : 1;

                return fr == startingRow && dr == 2 * direction
                        && board[fr + direction][fc] == '.';

            case 'N':
                return (ar == 2 && ac == 1) || (ar == 1 && ac == 2);

            case 'K':
                return ar <= 1 && ac <= 1;

            case 'B':
                return ar == ac && pathClear(fr, fc, tr, tc);

            case 'R':
                return (dr == 0 || dc == 0) && pathClear(fr, fc, tr, tc);

            case 'Q':
                return (ar == ac || dr == 0 || dc == 0)
                        && pathClear(fr, fc, tr, tc);

            default:
                return false;
        }
    }

    /** Determines whether all squares between two positions are empty. */
    private static boolean pathClear(int fr, int fc, int tr, int tc) {
        int sr = Integer.compare(tr, fr);
        int sc = Integer.compare(tc, fc);
        int row = fr + sr;
        int col = fc + sc;

        while (row != tr || col != tc) {
            if (board[row][col] != '.') {
                return false;
            }

            row += sr;
            col += sc;
        }

        return true;
    }

    /** Returns true if the selected king is being attacked. */
    private static boolean isInCheck(boolean white) {
        char king = white ? 'K' : 'k';
        int kr = -1;
        int kc = -1;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (board[r][c] == king) {
                    kr = r;
                    kc = c;
                }
            }
        }

        if (kr == -1) {
            return true;
        }

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                char piece = board[r][c];

                if (piece != '.' && isWhite(piece) != white
                        && pieceCanMove(r, c, kr, kc, true)) {
                    return true;
                }
            }
        }

        return false;
    }

    /** Validates a move without allowing a player to leave their king in check. */
    private static boolean legalMove(int fr, int fc, int tr, int tc, boolean white) {
        char piece = board[fr][fc];
        char target = board[tr][tc];

        if (piece == '.' || isWhite(piece) != white) {
            return false;
        }

        if (Character.toUpperCase(target) == 'K') {
            return false;
        }

        if (!pieceCanMove(fr, fc, tr, tc, false)) {
            return false;
        }

        // Try the move, check the king, and undo the temporary change.
        board[tr][tc] = piece;
        board[fr][fc] = '.';

        boolean leavesKingInCheck = isInCheck(white);

        board[fr][fc] = piece;
        board[tr][tc] = target;

        return !leavesKingInCheck;
    }

    /** Checks whether a player has at least one legal move remaining. */
    private static boolean hasLegalMove(boolean white) {
        for (int fr = 0; fr < 8; fr++) {
            for (int fc = 0; fc < 8; fc++) {

                if (board[fr][fc] == '.' || isWhite(board[fr][fc]) != white) {
                    continue;
                }

                for (int tr = 0; tr < 8; tr++) {
                    for (int tc = 0; tc < 8; tc++) {
                        if (legalMove(fr, fc, tr, tc, white)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /** Applies a legal move and automatically promotes pawns to queens. */
    private static void makeMove(int fr, int fc, int tr, int tc) {
        char piece = board[fr][fc];
        board[fr][fc] = '.';

        if (piece == 'P' && tr == 0) {
            piece = 'Q';
        }

        if (piece == 'p' && tr == 7) {
            piece = 'q';
        }

        board[tr][tc] = piece;
    }
}
