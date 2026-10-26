import java.util.ArrayList;
import java.util.List;

public class Board {
    private final Position[][] board = new Position[8][8];

    public Board() {
        for (int file = 0; file < board.length; file++) {
            for (int rank = 0; rank < board[file].length; rank++) {
                board[file][rank] = new Position(
                    String.valueOf((char) ('A' + file)), rank + 1);
            }
        }

        setupPieces();
    }

    private void setupPieces() {
        Color white = new Color("white");
        Color black = new Color("black");
        Class<?>[] backRank = {
            Rook.class, Knight.class, Bishop.class, Queen.class,
            King.class, Bishop.class, Knight.class, Rook.class
        };

        for (int file = 0; file < 8; file++) {
            placePiece(createPiece(backRank[file], white, file, 1), file, 1);
            placePiece(new Pawn(white, positionAt(file, 2)), file, 2);
            placePiece(new Pawn(black, positionAt(file, 7)), file, 7);
            placePiece(createPiece(backRank[file], black, file, 8), file, 8);
        }
    }

    private void placePiece(Piece piece, int file, int rank) {
        positionAt(file, rank).setPiece(piece);
    }

    private Piece createPiece(Class<?> pieceType, Color color, int file, int rank) {
        Position position = positionAt(file, rank);
        if (pieceType == Rook.class) return new Rook(color, position);
        if (pieceType == Knight.class) return new Knight(color, position);
        if (pieceType == Bishop.class) return new Bishop(color, position);
        if (pieceType == Queen.class) return new Queen(color, position);
        return new King(color, position);
    }

    private Position positionAt(int file, int rank) {
        return board[file][rank - 1];
    }

    private int fileIndex(Position position) {
        if (position == null || position.getFile() == null
                || position.getFile().length() != 1) {
            return -1;
        }

        char file = Character.toUpperCase(position.getFile().charAt(0));
        return file >= 'A' && file <= 'H' ? file - 'A' : -1;
    }

    private boolean isOnBoard(Position position) {
        return fileIndex(position) >= 0
            && position.getRank() >= 1 && position.getRank() <= 8;
    }

    /**
     * Returns the piece on the board at the specified position.
     *
     * @param position a position with a rank and file on the board
     * @return the piece at that position, or null if the square is empty or invalid
     */
    public Piece getPiece(Position position) {
        if (!isOnBoard(position)) {
            return null;
        }

        return positionAt(fileIndex(position), position.getRank()).getPiece();
    }

    /**
     * Moves a piece if the move is legal, including keeping its own king safe.
     *
     * @param from the original position of the piece
     * @param to the destination position
     * @return true if the piece was moved
     */
    public boolean movePiece(Position from, Position to) {
        Piece piece = getPiece(from);
        if (!isLegalMove(piece, from, to)) {
            return false;
        }

        Position source = positionAt(fileIndex(from), from.getRank());
        Position destination = positionAt(fileIndex(to), to.getRank());
        source.setPiece(null);

        if (piece instanceof Pawn && (to.getRank() == 1 || to.getRank() == 8)) {
            piece = new Queen(piece.getColor(), destination);
        } else {
            piece.setPosition(destination);
        }

        destination.setPiece(piece);
        return true;
    }

    private boolean isLegalMove(Piece piece, Position from, Position to) {
        if (piece == null || !isOnBoard(from) || !isOnBoard(to)) {
            return false;
        }

        Piece target = getPiece(to);
        if ((target != null && piece.getColor().isColor(target.getColor()))
                || target instanceof King) {
            return false;
        }

        Position destination = positionAt(fileIndex(to), to.getRank());
        if (!piece.possibleMoves(this).contains(destination)) {
            return false;
        }

        Position source = positionAt(fileIndex(from), from.getRank());
        Piece capturedPiece = destination.getPiece();
        source.setPiece(null);
        destination.setPiece(piece);
        piece.setPosition(destination);
        boolean leavesKingInCheck = isCheck(piece.getColor());
        source.setPiece(piece);
        destination.setPiece(capturedPiece);
        piece.setPosition(source);

        return !leavesKingInCheck;
    }

    /**
     * Returns whether the specified color's king is attacked.
     *
     * @param color the color to check
     * @return true if that color's king is in check
     */
    public boolean isCheck(Color color) {
        Position kingPosition = null;
        for (int file = 0; file < 8; file++) {
            for (int rank = 0; rank < 8; rank++) {
                Piece piece = board[file][rank].getPiece();
                if (piece instanceof King && piece.getColor().isColor(color)) {
                    kingPosition = board[file][rank];
                    break;
                }
            }
            if (kingPosition != null) {
                break;
            }
        }

        if (kingPosition == null) {
            return true;
        }

        for (int file = 0; file < 8; file++) {
            for (int rank = 0; rank < 8; rank++) {
                Piece piece = board[file][rank].getPiece();
                if (piece != null && !piece.getColor().isColor(color)
                        && piece.possibleMoves(this).contains(kingPosition)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns whether the specified color has no legal moves while in check.
     *
     * @param color the color to check
     * @return true if that color is checkmated
     */
    public boolean isCheckmate(Color color) {
        return isCheck(color) && !hasLegalMove(color);
    }

    /**
     * Returns whether the specified color has any legal move.
     *
     * @param color the color to check
     * @return true if at least one legal move exists
     */
    public boolean hasLegalMove(Color color) {
        for (int file = 0; file < 8; file++) {
            for (int rank = 0; rank < 8; rank++) {
                Piece piece = board[file][rank].getPiece();
                if (piece == null || !piece.getColor().isColor(color)) {
                    continue;
                }

                for (Position destination : piece.possibleMoves(this)) {
                    if (isLegalMove(piece, board[file][rank], destination)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Prints the current board state to the terminal.
     */
    public void display() {
        System.out.println("\n  A B C D E F G H");
        for (int rank = 8; rank >= 1; rank--) {
            System.out.print(rank + " ");
            for (int file = 0; file < 8; file++) {
                Piece piece = positionAt(file, rank).getPiece();
                char symbol = piece == null ? '.' : pieceSymbol(piece);
                if (piece != null && piece.getColor().isColor("black")) {
                    symbol = Character.toLowerCase(symbol);
                }
                System.out.print(symbol + " ");
            }
            System.out.println(rank);
        }
        System.out.println("  A B C D E F G H\n");
    }

    private char pieceSymbol(Piece piece) {
        if (piece instanceof Knight) {
            return 'N';
        }
        return piece.getClass().getSimpleName().charAt(0);
    }
}
