import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {

    /**
     * creates Knight piece
     */
    public Knight(Color color, Position position){
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

        for (int[] offset : OFFSETS) {
            int f = file + offset[0];
            int r = rank + offset[1];
            if (isOnBoard(f, r)) moves.add(positionAt(f, r));
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

        for (int[] offset : OFFSETS) {
            int f = file + offset[0];
            int r = rank + offset[1];
            if (!isOnBoard(f, r)) continue;
            Position destination = positionAt(f, r);
            if (canMoveTo(board, destination)) moves.add(destination);
        }
        return moves;
    }

}