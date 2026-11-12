import java.awt.*;
import javax.swing.*;

/**
 * Displays completed moves, captured pieces,
 * and the Undo control.
 */
public final class GameHistoryPanel extends JPanel {

    private final JTextArea history = new JTextArea();

    private final JPanel whiteCaptures =
            new JPanel(new FlowLayout(FlowLayout.LEFT));

    private final JPanel blackCaptures =
            new JPanel(new FlowLayout(FlowLayout.LEFT));

    private final JButton undo = new JButton("Undo");

    /**
     * Builds the panel and connects Undo to the controller.
     */
    public GameHistoryPanel(Runnable undoAction) {
        super(new BorderLayout(6, 6));

        setPreferredSize(new Dimension(300, 380));

        setBorder(
                BorderFactory.createEmptyBorder(8, 8, 8, 8));

        history.setEditable(false);
        history.setLineWrap(true);
        history.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(history);

        scroll.setBorder(
                BorderFactory.createTitledBorder("Move history"));

        add(scroll, BorderLayout.CENTER);

        JPanel captures =
                new JPanel(new GridLayout(2, 1, 4, 4));

        captures.add(
                captureRow(whiteCaptures, "Captured by White"));

        captures.add(
                captureRow(blackCaptures, "Captured by Black"));

        captures.setPreferredSize(new Dimension(280, 150));

        add(captures, BorderLayout.NORTH);

        undo.addActionListener(event -> undoAction.run());
        add(undo, BorderLayout.SOUTH);
    }

    /**
     * Wraps captured pieces in a horizontally scrollable row.
     */
    private JScrollPane captureRow(
            JPanel panel,
            String title) {

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(
                BorderFactory.createTitledBorder(title));

        return scroll;
    }

    /**
     * Rebuilds history and captures from the authoritative session.
     */
    public void refresh(ChessSession session) {
        StringBuilder text = new StringBuilder();

        whiteCaptures.removeAll();
        blackCaptures.removeAll();

        int number = 1;

        for (ChessSession.Move move : session.moves()) {
            text.append(number++)
                    .append(". ")
                    .append(move.description())
                    .append('\n');

            if (move.captured() != null) {
                ChessPieceDisplay display =
                        new ChessPieceDisplay();

                display.setPieceSize(28);
                display.setColorStyle(
                        session.options().pieces());

                display.setPiece(move.captured());

                if (move.white()) {
                    whiteCaptures.add(display);
                } else {
                    blackCaptures.add(display);
                }
            }
        }

        history.setText(text.toString());

        history.setCaretPosition(
                history.getDocument().getLength());

        undo.setEnabled(
                !session.moves().isEmpty()
                        && !session.finished());

        revalidate();
        repaint();
    }
}
