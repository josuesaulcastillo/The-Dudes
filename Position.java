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
        setFile(file);
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
        this(normalizeFileIndex(file), rank, piece);
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
        this(normalizeFileIndex(file), rank, null);
    }

    private static String normalizeFileIndex(int fileNum) {
        if (fileNum >= 0 && fileNum < FILES.length()) {
            return String.valueOf(FILES.charAt(fileNum));
        }
        if (fileNum >= 1 && fileNum <= FILES.length()) {
            return String.valueOf(FILES.charAt(fileNum - 1));
        }
        throw new IllegalArgumentException("File index must be between 0 and 7 or 1 and 8.");
    }

    /**
     * Sets the file variable of this position.
     * 
     * @param file String of the vertical file ranging from A to H.
     */
    public void setFile(String file) {
        String normalized = file == null ? null : file.trim();
        if (normalized == null || normalized.length() != 1) {
            throw new IllegalArgumentException("File must be a single letter from A to H.");
        }
        char letter = Character.toUpperCase(normalized.charAt(0));
        if (letter < 'A' || letter > 'H') {
            throw new IllegalArgumentException("File must be a single letter from A to H.");
        }
        this.file = String.valueOf(letter);
    }

    /**
     * Sets the file variable of this position.
     * 
     * @param fileNum int of the vertical file ranging from 0 to 7.
     */
    public void setFile(int fileNum) {
        this.file = normalizeFileIndex(fileNum);
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
     * @return String of the vertical column ("file")  from A to H.
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
     * @return String of the position's rank and file from A to H and 1 to 8 respectively.
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
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Position)) {
            return false;
        }
        Position other = (Position) obj;
        return this.rank == other.rank && Objects.equals(this.file, other.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, rank);
    }

    @Override
    public String toString() {
        return getPosition();
    }

}
