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
        return new ArrayList<>();
    }

}