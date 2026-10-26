import java.util.ArrayList;
import java.util.List;

public abstract class Piece{

//put in color class

private Color color;
private Position position;

public Piece(Color color, Position position){
    this.color = color;
    this.position = position;
}

public void setPosition(Position position){
    this.Position = position;
}

public Color getColor(){
    return color;
}

public Position getPosition(){
    return position;
}

public abstract List<Position> possibleMoves();

public abstract List<Position> possibleMoves(Board board);

protected boolean isOnBoard(int file, int rank) {
    return file >= 0 && file < 8 && rank >= 1 && rank <= 8;
}

protected int fileIndex(Position position) {
    String file = position.getFile();
    if (file == null || file.length() != 1) {
        return -1;
    }
    char letter = Character.toUpperCase(file.charAt(0));
    return letter >= 'A' && letter <= 'H' ? letter - 'A' : -1;
}

protected Position positionAt(int file, int rank) {
    return new Position(String.valueOf((char) ('A' + file)), rank);
}

protected boolean canMoveTo(Board board, Position destination) {
        Piece target = board.getPiece(destination);
        return target == null || !color.isColor(target.getColor());
    }

/** Adds squares in one direction, including an enemy piece but stopping at any piece. */
protected void addSlidingMoves(Board board, List<Position> moves,
                               int fileStep, int rankStep) {
    int file = fileIndex(position) + fileStep;
    int rank = position.getRank() + rankStep;

    while (isOnBoard(file, rank)) {
        Position destination = positionAt(file, rank);
        Piece target = board.getPiece(destination);

        if (target == null) {
            moves.add(destination);
        } else {
            if (!color.isColor(target.getColor())) {
                moves.add(destination);
            }
            break;
        }

       file += fileStep;
        rank += rankStep;
    }
}

protected void checkBoard(Board board) {
    if (board == null) {
        throw new IllegalArgumentException("Board cannot be null.");
    }
}

}