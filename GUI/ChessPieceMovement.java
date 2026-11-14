package GUI;
import javax.swing.JOptionPane;

import GameState.Board;
import GameState.Position;
import Pieces.King;
import Pieces.Piece;

public class ChessPieceMovement {
    private final Board board;
    private final ChessBoardGUI chessBoardGUI;
    private Position selectedSquare;
    private boolean gameOver;

    public ChessPieceMovement(Board board, ChessBoardGUI chessBoardGUI) {
        this.board = board;
        this.chessBoardGUI = chessBoardGUI;
        chessBoardGUI.setSquareDragListener(this::movePiece);
        chessBoardGUI.setSquareClickListener(this::handleSquareClick);
    }

    private void handleSquareClick(Position square) {
        if (gameOver) {
            return;
        }

        if (selectedSquare == null) {
            if (board.getPiece(square) != null) {
                selectedSquare = square;
                chessBoardGUI.highlightSquare(square, true);
            }
            return;
        }

        if (selectedSquare.equals(square)) {
            clearSelection();
            return;
        }

        Position source = selectedSquare;
        clearSelection();
        movePiece(source, square);
    }

    private void movePiece(Position source, Position destination) {
        if (gameOver) {
            return;
        }

        Piece movingPiece = board.getPiece(source);
        if (movingPiece == null) {
            clearSelection();
            return;
        }

        Piece capturedPiece = board.movePieceWithoutRuleValidation(source, destination);
        clearSelection();
        chessBoardGUI.refresh(board);

        if (capturedPiece instanceof King) {
            gameOver = true;
            String winner = capturedPiece.getColor().isColor("white") ? "Black" : "White";
            JOptionPane.showMessageDialog(
                    chessBoardGUI,
                    winner + " wins!",
                    "Game over",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void clearSelection() {
        selectedSquare = null;
        chessBoardGUI.clearHighlights();
    }
}
