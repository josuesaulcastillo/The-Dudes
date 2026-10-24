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
        return new ArrayList<>();
    }

}