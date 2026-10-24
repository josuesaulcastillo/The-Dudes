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
        return new ArrayList<>();
    }
}