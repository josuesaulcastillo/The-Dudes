import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Owns the GUI's only Board, its turn, completed moves, and settings.
 */
public final class ChessSession {

    private final Board board = new Board();
    private final List<Move> moves = new ArrayList<>();

    private boolean whiteTurn = true;
    private boolean finished;

    private SettingsDialog.Options options =
            new SettingsDialog.Options(0, 0, 1);

    private String savedState = document();

    /**
     * A completed move.
     * captured is null when the destination was empty.
     */
    public record Move(
            String from,
            String to,
            String pieceName,
            boolean white,
            Piece captured) {

        /**
         * Returns the text shown in the move list.
         */
        public String description() {
            return (white ? "White " : "Black ")
                    + pieceName + " " + from + " -> " + to
                    + (captured == null
                    ? ""
                    : " captures "
                    + captured.getClass().getSimpleName());
        }
    }

    /**
     * Returns the board for rendering.
     * All GUI moves must use move().
     */
    public Board board() {
        return board;
    }

    /**
     * Returns whether White moves next.
     */
    public boolean whiteTurn() {
        return whiteTurn;
    }

    /**
     * Returns whether a king has been captured.
     */
    public boolean finished() {
        return finished;
    }

    /**
     * Returns a read-only copy of the move list.
     */
    public List<Move> moves() {
        return List.copyOf(moves);
    }

    /**
     * Returns the applied appearance settings.
     */
    public SettingsDialog.Options options() {
        return options;
    }

    /**
     * Applies appearance settings without changing play data.
     */
    public void setOptions(SettingsDialog.Options value) {
        options = java.util.Objects.requireNonNull(value);
    }

    /**
     * Compares current data with the last successful save
     * or fresh game.
     */
    public boolean dirty() {
        return !document().equals(savedState);
    }

    /**
     * Converts a validated coordinate into the team's
     * Position type.
     */
    public static Position position(String square) {
        if (square == null || !square.matches("[A-H][1-8]")) {
            throw new IllegalArgumentException(
                    "Expected a square such as E2.");
        }

        return new Position(
                square.substring(0, 1),
                square.charAt(1) - '0');
    }

    /**
     * Resets all play data while retaining appearance preferences.
     */
    public void newGame() {
        board.initializePieces();
        moves.clear();

        whiteTurn = true;
        finished = false;

        savedState = document();
    }

    /**
     * Applies a Phase 2 move.
     * Rejects empty sources, wrong turns, and friendly captures.
     */
    public boolean move(String from, String to) {
        Position source = position(from);
        Position destination = position(to);

        if (finished || from.equals(to)) {
            return false;
        }

        Piece moving = board.getPiece(source);
        Piece target = board.getPiece(destination);

        if (moving == null
                || moving.getColor().isColor("white") != whiteTurn) {
            return false;
        }

        if (target != null
                && moving.getColor().isColor(target.getColor())) {
            return false;
        }

        Piece captured =
                board.movePieceWithoutRuleValidation(
                        source, destination);

        moves.add(new Move(
                from,
                to,
                moving.getClass().getSimpleName(),
                whiteTurn,
                captured));

        whiteTurn = !whiteTurn;
        finished = captured instanceof King;

        return true;
    }

    /**
     * Replays all but the last move into the same Board
     * to restore its complete state.
     */
    public boolean undo() {
        if (moves.isEmpty()) {
            return false;
        }

        List<Move> remaining =
                new ArrayList<>(
                        moves.subList(0, moves.size() - 1));

        board.initializePieces();
        moves.clear();

        whiteTurn = true;
        finished = false;

        for (Move move : remaining) {
            move(move.from(), move.to());
        }

        return true;
    }

    /**
     * Encodes settings and the active move sequence
     * in a versioned text format.
     */
    private String document() {
        StringBuilder text =
                new StringBuilder("DUDES_GUI_2\n");

        text.append(options.theme())
                .append(' ')
                .append(options.pieces())
                .append(' ')
                .append(options.size())
                .append('\n');

        for (Move move : moves) {
            text.append(move.from())
                    .append(' ')
                    .append(move.to())
                    .append('\n');
        }

        return text.toString();
    }

    /**
     * Writes a temporary file, then atomically replaces
     * the destination.
     * A failed save does not mark the game as saved.
     */
    public void save(Path destination) throws IOException {
        Path target = destination.toAbsolutePath();

        Path temporary = Files.createTempFile(
                target.getParent(), ".chess-", ".tmp");

        String text = document();

        try {
            Files.writeString(
                    temporary, text, StandardCharsets.UTF_8);

            Files.move(
                    temporary,
                    target,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);

            savedState = text;
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    /**
     * Validates a separate candidate before the caller
     * replaces the active session.
     */
    public static ChessSession load(Path path)
            throws IOException {

        if (Files.size(path) > 1_000_000) {
            throw new IOException("Save file is too large.");
        }

        List<String> lines =
                Files.readAllLines(
                        path, StandardCharsets.UTF_8);

        if (lines.size() < 2
                || !lines.get(0).equals("DUDES_GUI_2")) {
            throw new IOException(
                    "Not a supported chess save file.");
        }

        ChessSession candidate = new ChessSession();

        try {
            String[] settings =
                    lines.get(1).trim().split("\\s+");

            if (settings.length != 3) {
                throw new IllegalArgumentException();
            }

            candidate.setOptions(
                    new SettingsDialog.Options(
                            Integer.parseInt(settings[0]),
                            Integer.parseInt(settings[1]),
                            Integer.parseInt(settings[2])));

            for (int i = 2; i < lines.size(); i++) {
                String[] move =
                        lines.get(i).trim().split("\\s+");

                if (move.length != 2
                        || !candidate.move(move[0], move[1])) {
                    throw new IllegalArgumentException();
                }
            }
        } catch (IllegalArgumentException ex) {
            throw new IOException(
                    "Invalid settings or moves in save file.", ex);
        }

        candidate.savedState = candidate.document();
        return candidate;
    }
}
