import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {

    /**
     * creates Rook
     */
     public Rook(Color color, Position position) {
        super(color, position);
    }

    /**
     * puts possible moves into a list
     */
    @Override
    public List<Position> possibleMoves() {
        List<Position> moves = new ArrayList<>();
        int file = fileIndex(getPosition());
        int rank = getPosition().getRank();
        if (!isOnBoard(file, rank)) return moves;

        for (int f = 0; f < 8; f++) {
            if (f != file) moves.add(positionAt(f, rank));
        }
        for (int r = 1; r <= 8; r++) {
            if (r != rank) moves.add(positionAt(file, r));
        }
        return moves;
    }

    /**
     * puts possible moves into a list
     */
    @Override
    public List<Position> possibleMoves(Board board) {
        checkBoard(board);
        List<Position> moves = new ArrayList<>();
        int file = fileIndex(getPosition());
        int rank = getPosition().getRank();
        if (!isOnBoard(file, rank)) return moves;

        addSlidingMoves(board, moves, 1, 0);
        addSlidingMoves(board, moves, -1, 0);
        addSlidingMoves(board, moves, 0, 1);
        addSlidingMoves(board, moves, 0, -1);
        return moves;
    }

}