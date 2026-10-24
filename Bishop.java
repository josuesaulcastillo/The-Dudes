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
        return new ArrayList<>();
    }

}