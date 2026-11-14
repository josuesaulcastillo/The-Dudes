# Class diagram

The diagram shows the main application classes and their relationships.
The current GUI flow is centered on `ChessWindow` and `ChessSession`; the
console `Game` model and `ChessPieceMovement` controller are separate
implementations.

```mermaid
classDiagram
    direction LR

    namespace Application {
        class Main {
            +main(args) void
        }
    }

    namespace GUI {
        class ChessWindow {
            -ChessSession session
            -ChessBoardGUI boardView
            -GameHistoryPanel history
            +main(args) void
        }
        class ChessSession {
            -Board board
            -List~Move~ moves
            -Options options
            +move(from, to) boolean
            +undo() boolean
            +save(destination) void
            +load(path)$ ChessSession
        }
        class SessionMove {
            <<record>>
            +String from
            +String to
            +String pieceName
            +boolean white
            +Piece captured
        }
        class ChessBoardGUI {
            -ChessPieceDisplay[][] pieceLabels
            +refresh(board) void
            +setPiece(position, piece) void
        }
        class ChessPieceDisplay {
            -Piece displayedPiece
            +setPiece(piece) void
            +getDisplayedPiece() Piece
        }
        class GameHistoryPanel {
            +refresh(session) void
        }
        class GameMenuBar
        class SettingsDialog
        class SettingsOptions {
            <<record>>
            +int theme
            +int pieces
            +int size
            +pixels() int
            +light() Color
            +dark() Color
        }
        class ChessPieceMovement {
            -Board board
            -ChessBoardGUI chessBoardGUI
            -Position selectedSquare
            -boolean gameOver
        }
        class GuiFeaturesTest {
            <<test>>
        }
    }

    namespace GameState {
        class Game {
            -Board board
            -Player whitePlayer
            -Player blackPlayer
            -Color currentTurn
            -boolean gameRunning
            +start() void
            +play() void
            +end() void
        }
        class Board {
            -Position[][] board
            +movePiece(from, to) boolean
            +movePieceWithoutRuleValidation(from, to) Piece
            +isCheck(color) boolean
            +isCheckmate(color) boolean
        }
        class Player {
            -Color color
            -List~Piece~ availablePieces
            +makeMove(board) void
        }
        class Position {
            -String file
            -int rank
            -Piece piece
            +getPosition() String
        }
        class Color {
            +String color
            +isColor(other) boolean
        }
    }

    namespace Pieces {
        class Piece {
            <<abstract>>
            -Color color
            -Position position
            +possibleMoves() List~Position~
            +possibleMoves(board) List~Position~
        }
        class King
        class Queen
        class Rook
        class Bishop
        class Knight
        class Pawn
    }

    JFrame <|-- ChessWindow
    JPanel <|-- ChessBoardGUI
    JLabel <|-- ChessPieceDisplay
    JPanel <|-- GameHistoryPanel
    JMenuBar <|-- GameMenuBar
    JDialog <|-- SettingsDialog

    Main ..> ChessWindow : launches
    ChessWindow *-- ChessSession
    ChessWindow *-- ChessBoardGUI
    ChessWindow *-- GameHistoryPanel
    ChessWindow *-- GameMenuBar
    ChessWindow ..> SettingsDialog : opens
    ChessSession *-- Board
    ChessSession *-- SessionMove : move history
    ChessSession ..> SettingsOptions : stores
    SessionMove --> Piece : captured piece, optional
    ChessBoardGUI *-- ChessPieceDisplay : square labels
    ChessBoardGUI ..> Board : renders
    ChessBoardGUI ..> Position : input callbacks
    ChessPieceDisplay --> Piece : displays
    GameHistoryPanel ..> ChessSession : reads moves and settings
    GameHistoryPanel ..> ChessPieceDisplay : captured-piece views
    SettingsDialog ..> SettingsOptions : applies
    ChessPieceMovement ..> Board : moves pieces
    ChessPieceMovement ..> ChessBoardGUI : alternate controller

    Game *-- Board
    Game *-- Player : white and black
    Game --> Color : current turn
    Player --> Color
    Player ..> Board : submits moves
    Player ..> Piece : available pieces
    Board *-- Position : 64 squares
    Board ..> Piece : creates and moves
    Position --> Piece : occupant, optional
    Piece --> Position : current position
    Piece --> Color

    Piece <|-- King
    Piece <|-- Queen
    Piece <|-- Rook
    Piece <|-- Bishop
    Piece <|-- Knight
    Piece <|-- Pawn

    GuiFeaturesTest ..> ChessSession : tests
    GuiFeaturesTest ..> ChessBoardGUI : tests
    GuiFeaturesTest ..> GameHistoryPanel : tests
    GuiFeaturesTest ..> SettingsOptions : tests
```

`SessionMove` represents the nested `ChessSession.Move` record, and
`SettingsOptions` represents `SettingsDialog.Options`. `ChessPieceMovement`
is an alternate controller; the current window handles moves through
`ChessSession`.
