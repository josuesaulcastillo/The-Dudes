import java.util.ArrayList;
import java.util.List;

public class Board {
    private Position[][] board;

    /**
     * Class constructor.
     */
    public Board() {
        board = new Position[8][8];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j].setFile(i);
                board[i][j].setRank(j);
            }
        }
    }

    /**
     * Returns the piece on the board at the specified position.
     * 
     * @param position a position with a rank and file on the board
     * @return Piece that sits at the specified positon
     */
    public Piece getPiece(Position position) {
        return null;
    }

    /**
     * If possible, moves a piece from one position on the board
     * to the other.
     * 
     * @param from the original position of the piece
     * @param to the position to move the piece to
     */
    public void movePiece(Position from, Position to) {
        Piece currentPiece = from.getPiece();
        if(currentPiece == null) {
            System.out.println("No piece at specified position.");
            return;
        }
        
        List<Position> possibleMoves = new ArrayList<>(currentPiece.possibleMoves());
        for (int i = 0; i < possibleMoves.size(); i++) {
            if (possibleMoves.get(i).equals(to)) {
                Piece targetPiece = to.getPiece();
                if (targetPiece != null && targetPiece.getColor() == currentPiece.getColor()) {
                    return;
                }

                // The destination's piece, if any, is captured by replacing it.
                from.setPiece(null);
                to.setPiece(currentPiece);
                currentPiece.setPosition(to);
                return;
            }
        }
    }
    
    /**
     * Returns a boolean of whether a specified color is in check.
     * 
     * @param color the color to check
     * @return boolean of whether that color is in check
     */
    public boolean isCheck(Color color) {
        return false;
    }
    
    /**
     * Returns a boolean of whether a specified color is checkmated.
     * 
     * @param color the color to check
     * @return boolean of whether that color is in checkmate
     */
    public boolean isCheckmate(Color color) {
        return false;
    }

    /**
     * Prints the current board state to the terminal.
     */
    public void display() {
        for (int rank = board.length - 1; rank >= 0; rank--) {
            System.out.print((rank + 1) + " ");
            for (int file = 0; file < board[rank].length; file++) {
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
