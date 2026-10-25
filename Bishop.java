import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {

    /**
     * creates Bishop piece
     */
    public Bishop(Color color, Position position){
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

        for (int df : new int[] {-1, 1}) {
            for (int dr : new int[] {-1, 1}) {
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

        addSlidingMoves(board, moves, 1, 1);
        addSlidingMoves(board, moves, 1, -1);
        addSlidingMoves(board, moves, -1, 1);
        addSlidingMoves(board, moves, -1, -1);
        return moves;
    }

}