import java.util.ArrayList;
import java.util.List;

public class Board {
    private Position[][] board;

    /**
     * Class constructor.
     */
    public Board() {
        board = new Position[8][8];
        for (int file = 0; file < 8; file++) {
            for (int rank = 1; rank <= 8; rank++) {
                board[file][rank - 1] =
                    new Position(String.valueOf((char) ('A' + file)), rank);
            }
        }
        initializePieces();
    }

    /**
     * Initializes the board with the standard chess starting positions.
     */
    public void initializePieces() {
        Color white = new Color("white");
        Color black = new Color("black");

        placePiece(new Rook(white, new Position("A", 1)), "A", 1);
        placePiece(new Knight(white, new Position("B", 1)), "B", 1);
        placePiece(new Bishop(white, new Position("C", 1)), "C", 1);
        placePiece(new Queen(white, new Position("D", 1)), "D", 1);
        placePiece(new King(white, new Position("E", 1)), "E", 1);
        placePiece(new Bishop(white, new Position("F", 1)), "F", 1);
        placePiece(new Knight(white, new Position("G", 1)), "G", 1);
        placePiece(new Rook(white, new Position("H", 1)), "H", 1);
        for (char file = 'A'; file <= 'H'; file++) {
            placePiece(new Pawn(white, new Position(String.valueOf(file), 2)), String.valueOf(file), 2);
        }

        placePiece(new Rook(black, new Position("A", 8)), "A", 8);
        placePiece(new Knight(black, new Position("B", 8)), "B", 8);
        placePiece(new Bishop(black, new Position("C", 8)), "C", 8);
        placePiece(new Queen(black, new Position("D", 8)), "D", 8);
        placePiece(new King(black, new Position("E", 8)), "E", 8);
        placePiece(new Bishop(black, new Position("F", 8)), "F", 8);
        placePiece(new Knight(black, new Position("G", 8)), "G", 8);
        placePiece(new Rook(black, new Position("H", 8)), "H", 8);
        for (char file = 'A'; file <= 'H'; file++) {
            placePiece(new Pawn(black, new Position(String.valueOf(file), 7)), String.valueOf(file), 7);
        }
    }

    private void placePiece(Piece piece, String file, int rank) {
        Position location = getBoardPosition(new Position(file, rank));
        if (location == null) {
            return;
        }
        location.setPiece(piece);
        piece.setPosition(location);
    }

    private Position getBoardPosition(Position position) {
        if (position == null) {
            return null;
        }
        String file = position.getFile();
        int rank = position.getRank();
        if (file == null || file.length() != 1) {
            return null;
        }
        int fileIndex = Character.toUpperCase(file.charAt(0)) - 'A';
        if (fileIndex < 0 || fileIndex > 7 || rank < 1 || rank > 8) {
            return null;
        }
        return board[fileIndex][rank - 1];
    }

    /**
     * Returns the piece on the board at the specified position.
     * 
     * @param position a position with a rank and file on the board
     * @return Piece that sits at the specified positon
     */
    public Piece getPiece(Position position) {
        Position slot = getBoardPosition(position);
        return slot == null ? null : slot.getPiece();
    }

    /**
     * If possible, moves a piece from one position on the board
     * to the other.
     * 
     * @param from the original position of the piece
     * @param to the position to move the piece to
     */
    public void movePiece(Position from, Position to) {
        Position fromSlot = getBoardPosition(from);
        Position toSlot = getBoardPosition(to);
        if (fromSlot == null || toSlot == null) {
            System.out.println("Invalid board position.");
            return;
        }

        Piece currentPiece = fromSlot.getPiece();
        if (currentPiece == null) {
            System.out.println("No piece at specified position.");
            return;
        }

        List<Position> possibleMoves = new ArrayList<>(currentPiece.possibleMoves(this));
        if (!possibleMoves.contains(toSlot)) {
            System.out.println("Illegal move.");
            return;
        }

        Piece targetPiece = toSlot.getPiece();
        if (targetPiece != null && targetPiece.getColor() != null && currentPiece.getColor() != null &&
            targetPiece.getColor().isColor(currentPiece.getColor())) {
            System.out.println("Cannot capture your own piece.");
            return;
        }

        fromSlot.setPiece(null);
        toSlot.setPiece(currentPiece);
        currentPiece.setPosition(toSlot);
    }
    
    /**
     * Returns a boolean of whether a specified color is in check.
     * 
     * @param color the color to check
     * @return boolean of whether that color is in check
     */
    public boolean isCheck(Color color) {
        if (color == null) {
            return false;
        }

        Position kingPosition = null;
        for (int file = 0; file < board.length; file++) {
            for (int rank = 0; rank < board[file].length; rank++) {
                Position currentPosition = board[file][rank];
                Piece piece = currentPosition == null ? null : currentPosition.getPiece();
                if (piece instanceof King && piece.getColor() != null && piece.getColor().isColor(color)) {
                    kingPosition = currentPosition;
                    break;
                }
            }
            if (kingPosition != null) {
                break;
            }
        }

        if (kingPosition == null) {
            return false;
        }

        for (int file = 0; file < board.length; file++) {
            for (int rank = 0; rank < board[file].length; rank++) {
                Position currentPosition = board[file][rank];
                Piece piece = currentPosition == null ? null : currentPosition.getPiece();
                if (piece == null || piece.getColor() == null || piece.getColor().isColor(color)) {
                    continue;
                }
                if (piece.possibleMoves(this).contains(kingPosition)) {
                    return true;
                }
            }
        }

        return false;
    }
    
    /**
     * Returns a boolean of whether a specified color is checkmated.
     * 
     * @param color the color to check
     * @return boolean of whether that color is in checkmate
     */
    public boolean isCheckmate(Color color) {
        if (!isCheck(color)) {
            return false;
        }

        for (int file = 0; file < board.length; file++) {
            for (int rank = 0; rank < board[file].length; rank++) {
                Position fromPosition = board[file][rank];
                Piece piece = fromPosition == null ? null : fromPosition.getPiece();
                if (piece == null || !piece.getColor().isColor(color)) {
                    continue;
                }

                List<Position> moves = piece.possibleMoves(this);
                for (Position toPosition : moves) {
                    Position toSlot = getBoardPosition(toPosition);
                    if (toSlot == null) {
                        continue;
                    }
                    Piece target = toSlot.getPiece();
                    Position fromSlot = fromPosition;
                    fromSlot.setPiece(null);
                    toSlot.setPiece(piece);
                    piece.setPosition(toSlot);
                    boolean stillInCheck = isCheck(color);
                    toSlot.setPiece(target);
                    fromSlot.setPiece(piece);
                    piece.setPosition(fromSlot);
                    if (!stillInCheck) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Places pieces at 
     */
    public void placePiece(Piece piece) {
        if (piece == null || piece.getPosition() == null) {
            throw new IllegalArgumentException("Piece and position must not be null.");
        }

        Position position = piece.getPosition();
        String fileName = position.getFile();
        int rank = position.getRank();

        if (fileName == null || fileName.length() != 1
                || rank < 1 || rank > 8) {
            throw new IllegalArgumentException("Position must be on the board.");
        }

        int file = Character.toUpperCase(fileName.charAt(0)) - 'A';
        if (file < 0 || file >= 8) {
            throw new IllegalArgumentException("File must be between A and H.");
        }

        Position square = board[file][rank - 1];
        if (square == null) {
            square = new Position(String.valueOf((char) ('A' + file)), rank);
            board[file][rank - 1] = square;
        }

        if (square.getPiece() != null) {
            throw new IllegalStateException("Square is already occupied.");
        }

        square.setPiece(piece);
        piece.setPosition(square);
    }

    /**
     * Prints the current board state to the terminal.
     */
    public void display() {
        for (int rank = board[0].length - 1; rank >= 0; rank--) {
            System.out.print((rank + 1) + " ");
            for (int file = 0; file < board.length; file++) {
                Position position = board[file][rank];
                Piece piece = position == null ? null : position.getPiece();
                char symbol = ' ';
                if (piece != null) {
                    String name = piece.getClass().getSimpleName();
                    symbol = name.isEmpty() ? '?' : name.charAt(0);
                    if (piece.getColor() != null && "black".equals(piece.getColor().color)) {
                        symbol = Character.toLowerCase(symbol);
                    }
                }
                System.out.print("|" + symbol);
            }
            System.out.println("|");
        }
        System.out.println("  A B C D E F G H");
    }
}
