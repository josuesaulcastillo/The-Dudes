import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {

    /**
     * creates Queen piece
     */
    public Queen(Color color, Position position){
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
                while (isOnBoard(f, r)) {
                    moves.add(positionAt(f, r));
                    f += df;
                    r += dr;
                }
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
                addSlidingMoves(board, moves, df, dr);
            }
        }
        return moves;
    }

}