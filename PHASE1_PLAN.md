
# Phase 1 Plan

## Mercury: Board & Position

### Milestones: 

1. Blank class methods
Each class and method should compile
2. Public interfaces & instance variables
Should have a working matrix for the board and variables for rank and file
3. Documentation
Javadoc comments for each method that work
4. Implementation
Each method should do its intended results: 
- getPiece(Position position): Returns the piece at the specified position.
- movePiece(Position from, Position to): Moves a piece from one square to another.
- isCheck(Color color): Checks if a given color is in check.
- isCheckmate(Color color): Checks if a given color is in checkmate.
- display(): Prints the board to the console.


## Josue: Game & Player

### Milestones: 

1. Blank class methods
Each class and method should compile
2. Public interfaces & instance variables
Should have variables for players, board, and current turn, as well as available pieces
3. Documentation
Javadoc comments for each method that work
4. Implementation
Each method should do its intended results: 
- start(): Initializes a game.
- end(): Ends a game and declares a winner or a draw.
- play(): Main loop for playing the game. Alternates turns, checks for check/checkmate, and gets moves from players.
- makeMove(): Allows the player to input a move and attempts to execute it on the board


## Rue: Pieces

### Milestones: 

1. Blank class methods
Each class and method should compile
2. Public interfaces & instance variables
Should have variables for color and position
3. Documentation
Javadoc comments for each method that work
4. Implementation
Each method should do its intended results: 
- possibleMoves(): Returns a list of possible moves from the current posiLon.
- move(PosiLon newPosiLon): Moves the piece to a new posiLon.