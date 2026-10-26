import java.util.ArrayList;
import java.util.List;

public class King extends Piece {

    /**
     * creates King piece
     */
    public King(Color color, Position position){
        super(color,position);
    }

     /**
     * puts all possible moves into array list
     */
    @Override
    public List<Position> possibleMoves() {
        List<Position> moves = new ArrayList<>();
        int file = fileIndex(getPosition());
        int rank = getPosition().getRank();
        if (!isOnBoard(file, rank)) return moves;

        for (int df = -1; df <= 1; df++) {
            for (int dr = -1; dr <= 1; dr++) {
                if (df == 0 && dr == 0) continue;
                int f = file + df;
                int r = rank + dr;
                if (isOnBoard(f, r)) moves.add(positionAt(f, r));
            }
        }
        return moves;
    }

    @Override
    public List<Position> possibleMoves(Board board) {
        checkBoard(board);
        List<Position> moves = new ArrayList<>();
        int file = fileIndex(getPosition());
        int rank = getPosition().getRank();
        if (!isOnBoard(file, rank)) return moves;

        for (int df = -1; df <= 1; df++) {
            for (int dr = -1; dr <= 1; dr++) {
                if (df == 0 && dr == 0) continue;
                int f = file + df;
                int r = rank + dr;
                if (!isOnBoard(f, r)) continue;
                Position destination = positionAt(f, r);
                if (canMoveTo(board, destination)) moves.add(destination);
            }
        }
        return moves;
    }
}

