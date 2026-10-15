public class Square {
    private int rank;
    private int file;
    private Piece piece;

    //TODO: Make Javadocs for Square methods   
    public Square() {
        rank = 0;
        file = 0;
        piece = null;
    }

    public Square(int r, int f) {
        rank = r;
        file = f;
        piece = null;
    }

    public Square(int r, int f, Piece p) {
        rank = r;
        file = f;
        piece = p;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int r) {
        rank = r;
    }

    public int getFile() {
        return file;
    }

    public void setFile(int f) {
        rank = f;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece p) {
        piece = p;
    }
}
