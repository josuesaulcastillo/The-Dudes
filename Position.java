import java.util.Objects;

public class Position {
    private int rank;
    private String file;
    private Piece piece;

    private static final String FILES = "ABCDEFGH";


    /**
     * Class constructor specifying the rank, file, and piece of a position.
     * 
     * @param file String taking the horizontal row ("file") of the position
     * @param rank int taking the vertical column ("rank") of the position
     * @param piece Piece specifying which Piece is at the specified position
     */
    public Position(String file, int rank, Piece piece) {
        this.file = file;
        this.rank = rank;
        this.piece = piece;
    }

    /**
     * Class constructor specifying the rank, file, and piece of a position.
     * 
     * @param file int taking the horizontal row ("file") of the position
     * @param rank int taking the vertical column ("rank") of the position
     * @param piece Piece specifying which Piece is at the specified position
     */
    public Position(int file, int rank, Piece piece) {
        this(fileName(file), rank, piece);
    }

    /**
     * Class constructor specifying the rank and file of a position.
     * 
     * @param file String taking the horizontal row ("file") of the position
     * @param rank int taking the vertical column ("rank") of the position
     */
    public Position(String file, int rank) {
        this(file, rank, null);
    }

    /**
     * Class constructor specifying the rank and file of a position.
     * 
     * @param file int taking the horizontal row ("file") of the position
     * @param rank int taking the vertical column ("rank") of the position
     */
    public Position(int file, int rank) {
        this(fileName(file), rank, null);
    }

    private static String fileName(int file) {
        if (file < 1 || file > 8) {
            throw new IllegalArgumentException("File must be between 1 and 8.");
        }
        return FILES.substring(file - 1, file);
    }

    /**
     * Sets the file variable of this position.
     * 
     * @param file String of the vertical file ranging from A to G.
     */
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * Sets the file variable of this position.
     * 
     * @param file int of the vertical file ranging from 1 to 8.
     */
    public void setFile(int fileNum) {
        this.file = fileName(fileNum);
    }

    /**
     * Sets the rank variable of this position.
     * 
     * @param rank int of the  horizontal rank ranging from 1 to 8.
     */
    public void setRank(int rank) {
        this.rank = rank;
    }

    /**
     * Returns a String of the vertical file of the current position.
     * 
     * @return String of the vertical column ("file")  from A to G.
     */
    public String getFile() {
        return file;
    }

    /**
     * Returns n int of the horizontal rank of the current position.
     * 
     * @return int of the horizontal row ("rank") from 1 to 8.
     */
    public int getRank() {
        return rank;
    }

    /**
     * Returns the position as a string of two characters.
     * 
     * @return String of the position's rank and file from A to G and 1 to 8 respectively.
     */
    public String getPosition() {
        return file + rank;
    }

    /**
     * Returns the piece at the position.
     * 
     * @return Piece at the position
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * Sets a piece at this position.
     * 
     * @param piece Piece to put at this position
     */
    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Position)) {
            return false;
        }

        Position position = (Position) other;
        return rank == position.rank
            && file != null && position.file != null
            && file.equalsIgnoreCase(position.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file == null ? null : file.toUpperCase(), rank);
    }
}
