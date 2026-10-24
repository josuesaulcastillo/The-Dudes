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
        return new ArrayList<>();
    }

}