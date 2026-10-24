public abstract class Piece{

//put in color class

private Color color;
private Position position;

public Piece(Color color, Position position){
    this.color = color;
    this.position = position;
}

public Color getColor(){
    return color;
}

public Position getPosition(){
    return position;
}

public abstract List<Position> possibleMoves();

}