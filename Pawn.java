import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

    /**
     * creates Pawn piece
     */
    public Pawn(Color color, Position position){
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

        int direction = getColor().isColor("white") ? 1 : -1;
        int nextRank = rank + direction;
        if (!isOnBoard(file, nextRank)) return moves;

        Position forward = positionAt(file, nextRank);
        if (board.getPiece(forward) == null) {
            moves.add(forward);
            int startingRank = direction == 1 ? 2 : 7;
            int twoStepRank = rank + 2 * direction;
            if (rank == startingRank && isOnBoard(file, twoStepRank)) {
                Position twoForward = positionAt(file, twoStepRank);
                if (board.getPiece(twoForward) == null) moves.add(twoForward);
            }
        }

        for (int df : new int[] {-1, 1}) {
            int targetFile = file + df;
            if (!isOnBoard(targetFile, nextRank)) continue;
            Position diagonal = positionAt(targetFile, nextRank);
            Piece target = board.getPiece(diagonal);
            if (target != null && !getColor().isColor(target.getColor())) {
                moves.add(diagonal);
            }
        }
        return moves;
    }
}