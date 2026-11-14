import java.awt.Component;
import java.awt.Container;
import java.awt.image.BufferedImage;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * Standard-library regression checks.
 * No desktop or external test library is needed.
 */
public final class GuiFeaturesTest {

    /**
     * Checks persistence, undo, settings,
     * model/view agreement, and failed file operations.
     */
    public static void main(String[] args) throws Exception {
        Path directory =
                Files.createTempDirectory("chess-tests-");

        Path save = directory.resolve("game.chess");

        try {
            ChessSession game = new ChessSession();

            check(!game.dirty(),
                    "Fresh game should be clean");

            check(!game.move("A8", "A6")
                            && !game.move("A1", "B1"),
                    "Reject invalid moves");

            check(game.moves().isEmpty(),
                    "Invalid moves must not enter history");

            check(game.move("E2", "E4"), "White move");
            check(game.move("D7", "D5"), "Black move");
            check(game.move("E4", "D5"), "Capture");

            check(game.moves().get(2).captured() instanceof Pawn,
                    "Record captured pawn");

            check(game.moves().get(2).captured().getPosition()
                            == null,
                    "Captured piece off board");

            game.setOptions(
                    new SettingsDialog.Options(2, 1, 2));

            String expected = snapshot(game);

            game.save(save);

            check(!game.dirty(),
                    "Successful save marks clean");

            ChessSession loaded = ChessSession.load(save);

            check(snapshot(loaded).equals(expected),
                    "Complete save/load round trip");

            check(loaded.undo() && loaded.whiteTurn(),
                    "Undo restores turn after load");

            check(loaded.board().getPiece(
                            ChessSession.position("D5"))
                            instanceof Pawn,
                    "Capture restored");

            check(loaded.board().getPiece(
                            ChessSession.position("E4"))
                            instanceof Pawn,
                    "Mover restored");

            check(loaded.move("E4", "E5"),
                    "Different move after undo");

            loaded.save(save);

            check(snapshot(loaded).equals(
                            snapshot(ChessSession.load(save))),
                    "Save new move sequence");

            while (loaded.undo()) {
                // Undo every remaining move.
            }

            check(loaded.moves().isEmpty()
                            && loaded.whiteTurn(),
                    "Repeated undo");

            check(!loaded.undo(),
                    "Undo on empty history");

            loaded.newGame();

            check(!loaded.dirty() && !loaded.finished(),
                    "New Game resets status");

            check(loaded.options().equals(game.options()),
                    "New Game retains settings");

            check(loaded.move("D1", "E8")
                            && loaded.finished(),
                    "King capture");

            check(!loaded.move("A7", "A6"),
                    "Game over blocks moves");

            loaded.save(save);

            check(ChessSession.load(save).finished(),
                    "Finished status survives loading");

            loaded.undo();

            check(!loaded.finished()
                            && loaded.board().getPiece(
                                    ChessSession.position("E8"))
                            instanceof King,
                    "Model undo restores king");

            loaded.newGame();
            loaded.move("E2", "E4");

            Path protectedDirectory =
                    directory.resolve("existing.chess");

            Files.createDirectory(protectedDirectory);

            Path sentinel =
                    protectedDirectory.resolve("keep.txt");

            Files.writeString(sentinel, "keep");

            expectFailure(() -> loaded.save(protectedDirectory));

            check(Files.readString(sentinel).equals("keep")
                            && loaded.dirty(),
                    "Failed save preserves target and dirty state");

            String[] invalidFiles = {
                "bad",
                "DUDES_GUI_2\n9 0 0\n",
                "DUDES_GUI_2\n0 0 0\nZ9 E4\n",
                "DUDES_GUI_2\n0 0 0\nA8 A6\n"
            };

            for (String invalid : invalidFiles) {
                Files.writeString(save, invalid);

                String before = snapshot(loaded);

                expectFailure(() -> ChessSession.load(save));

                check(snapshot(loaded).equals(before),
                        "Invalid load preserves active game");
            }

            SwingUtilities.invokeAndWait(() -> {
                checkViews(game);
                checkBoardInput();
            });

            System.out.println(
                    "All GUI-feature regression checks passed.");

        } finally {
            try (var paths = Files.walk(directory)) {
                for (Path path : paths.sorted(
                        java.util.Comparator.reverseOrder())
                        .toList()) {
                    Files.delete(path);
                }
            }
        }
    }

    /**
     * Checks click and drag gestures when press and release
     * land on different Swing components of the board.
     */
    private static void checkBoardInput() {
        ChessBoardGUI view = new ChessBoardGUI();
        view.setSize(640, 640);
        layout(view);

        List<String> clicks = new ArrayList<>();
        List<String> drags = new ArrayList<>();

        view.setSquareClickListener(
                position -> clicks.add(position.toString()));

        view.setSquareDragListener(
                (from, to) ->
                        drags.add(from + "->" + to));

        Container sourceSquare =
                (Container) view.getComponent(0);
        Component sourcePiece = sourceSquare.getComponent(0);

        sendMouseEvent(
                sourcePiece,
                MouseEvent.MOUSE_PRESSED,
                sourcePiece.getWidth() / 2,
                sourcePiece.getHeight() / 2);

        sendMouseEvent(
                sourceSquare,
                MouseEvent.MOUSE_RELEASED,
                2,
                2);

        check(clicks.equals(List.of("A8")) && drags.isEmpty(),
                "Click works across a piece label and its square");

        Container targetSquare =
                (Container) view.getComponent(1);
        Component targetPiece = targetSquare.getComponent(0);

        sendMouseEvent(
                sourceSquare,
                MouseEvent.MOUSE_PRESSED,
                2,
                2);

        sendMouseEvent(
                targetPiece,
                MouseEvent.MOUSE_RELEASED,
                targetPiece.getWidth() / 2,
                targetPiece.getHeight() / 2);

        check(drags.equals(List.of("A8->B8")),
                "Drag works across board squares");
    }

    /**
     * Sends a mouse event directly to listeners for a deterministic
     * off-screen Swing input check.
     */
    private static void sendMouseEvent(
            Component component,
            int id,
            int x,
            int y) {

        MouseEvent event = new MouseEvent(
                component,
                id,
                System.currentTimeMillis(),
                0,
                x,
                y,
                1,
                false,
                MouseEvent.BUTTON1);

        for (MouseListener listener : component.getMouseListeners()) {
            if (id == MouseEvent.MOUSE_PRESSED) {
                listener.mousePressed(event);
            } else {
                listener.mouseReleased(event);
            }
        }
    }

    /**
     * Checks all appearance combinations against
     * the unchanged position on the Swing thread.
     */
    private static void checkViews(ChessSession game) {
        ChessBoardGUI view =
                new ChessBoardGUI(game.board());

        GameHistoryPanel history =
                new GameHistoryPanel(() -> { });

        String before = boardText(game);

        for (int theme = 0; theme < 3; theme++) {
            for (int pieces = 0; pieces < 2; pieces++) {
                for (int size = 0; size < 3; size++) {
                    var options =
                            new SettingsDialog.Options(
                                    theme, pieces, size);

                    game.setOptions(options);

                    view.setBoardColors(
                            options.light(),
                            options.dark());

                    view.setSquareSize(options.pixels());

                    SettingsDialog.stylePieces(view, pieces);

                    view.refresh(game.board());
                    history.refresh(game);

                    check(boardText(game).equals(before),
                            "Appearance must preserve play data");

                    check(view.getPreferredSize().width
                                    == options.pixels() * 8,
                            "Board size applied");

                    check(countPieces(view) == 31
                                    && countPieces(history) == 1,
                            "Board and captures synchronized");

                    view.setSize(view.getPreferredSize());
                    layout(view);

                    BufferedImage image = new BufferedImage(
                            view.getWidth(),
                            view.getHeight(),
                            BufferedImage.TYPE_INT_RGB);

                    var graphics = image.createGraphics();
                    view.printAll(graphics);
                    graphics.dispose();
                }
            }
        }
    }

    /**
     * Lays out nested components for off-screen painting.
     */
    private static void layout(Container container) {
        container.doLayout();

        for (Component child : container.getComponents()) {
            if (child instanceof Container nested) {
                layout(nested);
            }
        }
    }

    /**
     * Counts displayed model pieces,
     * including captured-piece labels.
     */
    private static int countPieces(Component component) {
        int count =
                component instanceof ChessPieceDisplay display
                        && display.getDisplayedPiece() != null
                        ? 1 : 0;

        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                count += countPieces(child);
            }
        }

        return count;
    }

    /**
     * Returns all play data in a comparable form.
     */
    private static String boardText(ChessSession game) {
        StringBuilder text = new StringBuilder();

        for (int rank = 1; rank <= 8; rank++) {
            for (char file = 'A'; file <= 'H'; file++) {
                Piece piece = game.board().getPiece(
                        new Position(
                                String.valueOf(file), rank));

                text.append(
                        piece == null
                                ? "."
                                : piece.getClass().getSimpleName()
                                + piece.getColor().color
                                + piece.getPosition());
            }
        }

        text.append(game.whiteTurn())
                .append(game.finished());

        for (ChessSession.Move move : game.moves()) {
            text.append(move.description());
        }

        return text.toString();
    }

    /**
     * Includes appearance settings in a comparison.
     */
    private static String snapshot(ChessSession game) {
        return boardText(game) + game.options();
    }

    /**
     * A file operation expected to fail.
     */
    private interface FileOperation {
        void run() throws IOException;
    }

    /**
     * Requires an IOException from an invalid file operation.
     */
    private static void expectFailure(
            FileOperation operation) throws IOException {

        try {
            operation.run();
        } catch (IOException expected) {
            return;
        }

        throw new AssertionError(
                "Expected file operation to fail");
    }

    /**
     * Fails a check independently of Java's assertion settings.
     */
    private static void check(
            boolean condition,
            String message) {

        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
