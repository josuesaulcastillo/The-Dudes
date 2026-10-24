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

    }
}
