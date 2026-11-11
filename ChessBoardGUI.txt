import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Swing view of an 8x8 chessboard, with White's pieces at the bottom.
 * Coordinates are standard chess positions: A1 at bottom left, H8 at top right.
 * This class never moves or captures pieces in the Board game model.
 */
public class ChessBoardGUI extends JPanel {
    private static final int BOARD_SIZE = 8;

    private final JPanel[][] squares = new JPanel[BOARD_SIZE][BOARD_SIZE];
    private final ChessPieceDisplay[][] pieceLabels =
            new ChessPieceDisplay[BOARD_SIZE][BOARD_SIZE];
    private final boolean[][] highlights = new boolean[BOARD_SIZE][BOARD_SIZE];

    private java.awt.Color lightColor = new java.awt.Color(238, 238, 210);
    private java.awt.Color darkColor = new java.awt.Color(118, 150, 86);
    private java.awt.Color highlightColor = new java.awt.Color(245, 235, 100);

    private Consumer<Position> squareClickListener;
    private BiConsumer<Position, Position> squareDragListener;
    private Position pressedPosition;

    /** Creates an empty board; call refresh(board) to display the game's pieces. */
    public ChessBoardGUI() {
        super(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        setSquareSize(76);

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Position position = positionFor(row, col);
                JPanel square = new JPanel(new BorderLayout());
                ChessPieceDisplay pieceLabel = new ChessPieceDisplay();
                pieceLabel.setPieceSize(62);
                square.add(pieceLabel, BorderLayout.CENTER);

                // Both components receive mouse input so clicks on the image work.
                MouseAdapter listener = createMouseListener(position);
                square.addMouseListener(listener);
                pieceLabel.addMouseListener(listener);

                squares[row][col] = square;
                pieceLabels[row][col] = pieceLabel;
                add(square);
            }
        }
        updateSquareColors();
    }

    /** Creates and immediately fills the display from the supplied Board. */
    public ChessBoardGUI(Board board) {
        this();
        refresh(board);
    }

    /**
     * Redraws every square using the current Board state.
     * Call this AFTER the controller has changed the game model.
     *
     * @param board board model to read; this method does not modify it
     */
    public void refresh(Board board) {
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null.");
        }
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Position position = positionFor(row, col);
                pieceLabels[row][col].setPiece(board.getPiece(position));
            }
        }
    }

    /** Replaces the visual contents of one square; does not change Board. */
    public void setPiece(Position position, Piece piece) {
        pieceLabels[rowOf(position)][colOf(position)].setPiece(piece);
    }

    /** Makes one square visually empty; does not remove anything from Board. */
    public void clearPiece(Position position) {
        setPiece(position, null);
    }

    /** Clears only the graphics; it does not reset the Board model. */
    public void clearPieces() {
        for (ChessPieceDisplay[] row : pieceLabels) {
            for (ChessPieceDisplay label : row) {
                label.setPiece(null);
            }
        }
    }

    /** Registers a callback for a left click, without performing any move. */
    public void setSquareClickListener(Consumer<Position> listener) {
        squareClickListener = listener;
    }

    /** Registers a callback reporting drag start and end positions only. */
    public void setSquareDragListener(BiConsumer<Position, Position> listener) {
        squareDragListener = listener;
    }

    /** Sets the colors of unhighlighted squares. */
    public void setBoardColors(java.awt.Color light, java.awt.Color dark) {
        if (light == null || dark == null) {
            throw new IllegalArgumentException("Board colors cannot be null.");
        }
        lightColor = light;
        darkColor = dark;
        updateSquareColors();
    }

    /** Adjusts board dimensions; call frame.pack() to resize the window. */
    public void setSquareSize(int pixels) {
        if (pixels < 32) {
            throw new IllegalArgumentException("Square size must be at least 32.");
        }
        setPreferredSize(new Dimension(pixels * BOARD_SIZE, pixels * BOARD_SIZE));
        for (ChessPieceDisplay[] row : pieceLabels) {
            for (ChessPieceDisplay label : row) {
                if (label != null) {
                    label.setPieceSize((int) (pixels * 0.82));
                }
            }
        }
        revalidate();
    }

    /** Updates the image folder on all piece labels. */
    public void setPieceImageDirectory(Path directory) {
        for (ChessPieceDisplay[] row : pieceLabels) {
            for (ChessPieceDisplay label : row) {
                label.setImageDirectory(directory);
            }
        }
    }

    /** Reloads the piece images after replacing files in the image folder. */
    public void reloadPieceImages() {
        for (ChessPieceDisplay[] row : pieceLabels) {
            for (ChessPieceDisplay label : row) {
                label.reloadImage();
            }
        }
    }

    /** Highlights a square, or clears that square's highlight. */
    public void highlightSquare(Position position, boolean show) {
        highlights[rowOf(position)][colOf(position)] = show;
        updateSquareColors();
    }

    /** Removes all visual highlights. */
    public void clearHighlights() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                highlights[row][col] = false;
            }
        }
        updateSquareColors();
    }

    private void updateSquareColors() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (squares[row][col] != null) {
                    java.awt.Color color = (row + col) % 2 == 0
                            ? lightColor : darkColor;
                    squares[row][col].setBackground(
                            highlights[row][col] ? highlightColor : color);
                }
            }
        }
        repaint();
    }

    private MouseAdapter createMouseListener(Position square) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (SwingUtilities.isLeftMouseButton(event) && squareClickListener != null) {
                    squareClickListener.accept(square);
                }
            }

            @Override
            public void mousePressed(MouseEvent event) {
                if (SwingUtilities.isLeftMouseButton(event)) {
                    pressedPosition = square;
                }
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                if (!SwingUtilities.isLeftMouseButton(event) || pressedPosition == null) {
                    return;
                }
                Point point = SwingUtilities.convertPoint(
                        event.getComponent(), event.getPoint(), ChessBoardGUI.this);
                Position releasedPosition = positionAt(point);
                if (releasedPosition != null && !pressedPosition.equals(releasedPosition)
                        && squareDragListener != null) {
                    squareDragListener.accept(pressedPosition, releasedPosition);
                }
                pressedPosition = null;
            }
        };
    }

    private Position positionAt(Point point) {
        if (getWidth() == 0 || getHeight() == 0 || point.x < 0 || point.y < 0
                || point.x >= getWidth() || point.y >= getHeight()) {
            return null;
        }
        int col = point.x * BOARD_SIZE / getWidth();
        int row = point.y * BOARD_SIZE / getHeight();
        return positionFor(row, col);
    }

    private static Position positionFor(int row, int col) {
        return new Position(String.valueOf((char) ('A' + col)), 8 - row);
    }

    private static int rowOf(Position position) {
        if (position == null || position.getRank() < 1 || position.getRank() > 8) {
            throw new IllegalArgumentException("Rank must be between 1 and 8.");
        }
        return 8 - position.getRank();
    }

    private static int colOf(Position position) {
        if (position == null || position.getFile() == null
                || position.getFile().length() != 1) {
            throw new IllegalArgumentException("File must be A through H.");
        }
        int col = Character.toUpperCase(position.getFile().charAt(0)) - 'A';
        if (col < 0 || col >= BOARD_SIZE) {
            throw new IllegalArgumentException("File must be A through H.");
        }
        return col;
    }
}
