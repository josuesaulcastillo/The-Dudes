public class Position {
    private int rank;
    private String file;
    private Piece piece;

    private static String files = "ABCDEFG";

    //constructors
    public Position(String file, int rank) {
        this.file = file;
        this.rank = rank;
        this.piece = null;
    }

    public Position(String file, int rank, Piece piece) {
        this.file = file;
        this.rank = rank;
        this.piece = piece;
    }

    //changes file var for this position
    public void setFile(String file) {
        
    }

    //changes rank var for this position
    public void setRank(int rank) {
        
    }

    //returns file for this position
    public String getFile() {
        return file;
    }

    //returns rank for this position
    public int getRank() {
        return rank;
    }

    //returns position as string
    public String getPosition() {
        return file + rank;
    }

}