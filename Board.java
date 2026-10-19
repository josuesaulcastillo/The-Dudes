public class Board {
    private Position[][] board;

    //constructor
    public Board() {
        board = new Position[8][8];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j].setFile(i);
                board[i][j].setRank(j);
            }
        }
    }

    //check piece at position, returns null if empty
    public Piece getPiece(Position position) {
        return null;
    }

    //moves piece from pos1 to pos2 if possible
    public void movePiece(Position from, Position to) {

    }
    
    //returns whether a color is in check
    public boolean isCheck(Color color) {
        return false;
    }

    //returns whether a color is in checkmate
    public boolean isCheckmate(Color color) {
        return false;
    }

    //displays current boardstate
    public void display() {

    }
}
