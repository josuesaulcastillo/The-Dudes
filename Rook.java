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
        return new ArrayList<>();
    }

}