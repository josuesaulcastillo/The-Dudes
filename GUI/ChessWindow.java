package GUI;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import Pieces.Piece;

/**
 * Connects the team's board view to menus,
 * settings, history, and one session.
 */
public final class ChessWindow extends JFrame {

    private ChessSession session = new ChessSession();

    private final ChessBoardGUI boardView =
            new ChessBoardGUI();

    private final GameHistoryPanel history =
            new GameHistoryPanel(this::undo);

    private final JLabel status = new JLabel();

    private String selected;

    /**
     * Builds a window using the existing
     * team board and piece classes.
     */
    public ChessWindow() {
        super("The Dudes - Chess");

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        setJMenuBar(new GameMenuBar(
                this::newGame,
                () -> saveGame(),
                this::loadGame,
                this::settings));

        add(boardView, BorderLayout.CENTER);
        add(history, BorderLayout.EAST);

        status.setBorder(
                BorderFactory.createEmptyBorder(8, 10, 8, 10));

        add(status, BorderLayout.SOUTH);

        boardView.setSquareClickListener(
                position -> click(position.toString()));

        boardView.setSquareDragListener(
                (from, to) ->
                        move(from.toString(), to.toString()));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                if (mayDiscard()) {
                    dispose();
                }
            }
        });

        setResizable(false);

        applyAppearance();
        refresh();

        setLocationRelativeTo(null);
    }

    /**
     * Handles selection without creating a history entry.
     */
    private void click(String square) {
        if (session.finished()) {
            return;
        }

        Piece piece = session.board().getPiece(
                ChessSession.position(square));

        if (piece != null
                && piece.getColor().isColor("white")
                == session.whiteTurn()) {

            selected = square.equals(selected) ? null : square;
            refresh();

        } else if (selected != null) {
            move(selected, square);
        }
    }

    /**
     * Sends both click and drag moves
     * through the same model operation.
     */
    private void move(String from, String to) {
        boolean moved = session.move(from, to);

        selected = null;
        refresh();

        if (!moved && !session.finished()) {
            status.setText(
                    "Choose your piece and an empty "
                            + "or opponent's square.");
        }

        if (moved && session.finished()) {
            finishGame();
        }
    }

    /**
     * Shows the required winner popup,
     * then closes this game and its dialogs.
     */
    private void finishGame() {
        String winner =
                session.whiteTurn() ? "Black" : "White";

        JOptionPane.showMessageDialog(
                this,
                winner
                        + " wins! The opponent's King was captured.",
                "Game Over",
                JOptionPane.INFORMATION_MESSAGE);

        dispose();
    }

    /**
     * Reverses one move and removes any old selection.
     */
    private void undo() {
        if (session.finished()) {
            return;
        }

        session.undo();
        selected = null;

        refresh();
    }

    /**
     * Resets play data only after
     * unsaved changes have been handled.
     */
    private void newGame() {
        if (!mayDiscard()) {
            return;
        }

        session.newGame();
        selected = null;

        refresh();
    }

    /**
     * Prompts to save, discard, or cancel.
     * Cancelled or failed saves return false.
     */
    private boolean mayDiscard() {
        if (!session.dirty()) {
            return true;
        }

        int answer = JOptionPane.showOptionDialog(
                this,
                "Save your changes first?",
                "Unsaved game",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[] {"Save", "Discard", "Cancel"},
                "Save");

        return answer == 1
                || (answer == 0 && saveGame());
    }

    /**
     * Creates a chooser with a chess-save filter.
     */
    private JFileChooser chooser() {
        JFileChooser chooser = new JFileChooser();

        chooser.setFileFilter(
                new FileNameExtensionFilter(
                        "Chess saves (*.chess)", "chess"));

        return chooser;
    }

    /**
     * Saves to the chosen filename.
     * Returns false on cancellation or a file error.
     */
    private boolean saveGame() {
        JFileChooser chooser = chooser();

        if (chooser.showSaveDialog(this)
                != JFileChooser.APPROVE_OPTION) {
            return false;
        }

        Path path = chooser.getSelectedFile().toPath();

        if (!path.getFileName()
                .toString()
                .toLowerCase(Locale.ROOT)
                .endsWith(".chess")) {

            path = path.resolveSibling(
                    path.getFileName() + ".chess");
        }

        if (java.nio.file.Files.exists(path)
                && JOptionPane.showConfirmDialog(
                        this,
                        "Replace " + path.getFileName() + "?",
                        "Overwrite save",
                        JOptionPane.YES_NO_OPTION)
                != JOptionPane.YES_OPTION) {
            return false;
        }

        try {
            session.save(path);

            refresh();
            status.setText("Game saved.");

            return true;
        } catch (IOException | SecurityException ex) {
            showError("Could not save game", ex);
            return false;
        }
    }

    /**
     * Validates a candidate before asking to discard
     * or replacing the current game.
     */
    private void loadGame() {
        JFileChooser chooser = chooser();

        if (chooser.showOpenDialog(this)
                != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            ChessSession loaded = ChessSession.load(
                    chooser.getSelectedFile().toPath());

            if (!mayDiscard()) {
                return;
            }

            session = loaded;
            selected = null;

            applyAppearance();
            refresh();

            if (session.finished()) {
                finishGame();
            }
        } catch (IOException | SecurityException ex) {
            showError("Could not load game", ex);
        }
    }

    /**
     * Opens a fresh dialog initialized
     * with the applied settings.
     */
    private void settings() {
        new SettingsDialog(
                this,
                session.options(),
                options -> {
                    boolean resized =
                            options.size()
                                    != session.options().size();

                    session.setOptions(options);

                    boardView.setBoardColors(
                            options.light(),
                            options.dark());

                    SettingsDialog.stylePieces(
                            boardView, options.pieces());

                    if (resized) {
                        boardView.setSquareSize(options.pixels());
                        pack();
                    }

                    refresh();
                }).setVisible(true);
    }

    /**
     * Applies loaded settings and packs
     * during initialization or loading.
     */
    private void applyAppearance() {
        SettingsDialog.Options options = session.options();

        boardView.setBoardColors(
                options.light(),
                options.dark());

        boardView.setSquareSize(options.pixels());

        SettingsDialog.stylePieces(
                boardView, options.pieces());

        pack();
    }

    /**
     * Redraws data without resizing the frame on every move.
     */
    private void refresh() {
        boardView.refresh(session.board());
        boardView.clearHighlights();

        if (selected != null) {
            boardView.highlightSquare(
                    ChessSession.position(selected), true);
        }

        history.refresh(session);

        status.setText(
                session.finished()
                        ? "Game over"
                        : (session.whiteTurn() ? "White" : "Black")
                        + " to move - click or drag a piece.");

        setTitle(
                "The Dudes - Chess"
                        + (session.dirty() ? " *" : ""));
    }

    /**
     * Reports a recoverable file error
     * without replacing the game.
     */
    private void showError(String title, Exception error) {
        JOptionPane.showMessageDialog(
                this,
                error.getMessage(),
                title,
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Launches the GUI on Swing's event dispatch thread.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                () -> new ChessWindow().setVisible(true));
    }
}
