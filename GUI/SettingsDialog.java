package GUI;
import java.awt.*;
import java.util.function.Consumer;
import javax.swing.*;

/**
 * Edits appearance choices.
 * Only Apply sends them to the game window.
 */
public final class SettingsDialog extends JDialog {

    /**
     * Immutable, validated appearance choices
     * saved with the game.
     */
    public record Options(int theme, int pieces, int size) {

        /**
         * Validates the available choices.
         */
        public Options {
            if (theme < 0 || theme > 2
                    || pieces < 0 || pieces > 1
                    || size < 0 || size > 2) {
                throw new IllegalArgumentException(
                        "Invalid appearance settings.");
            }
        }

        /**
         * Returns the square width for the selected size.
         */
        public int pixels() {
            return new int[] {48, 64, 80}[size];
        }

        /**
         * Returns the light-square color.
         */
        public java.awt.Color light() {
            return new java.awt.Color(
                    new int[] {
                        0xF0D9B5,
                        0xEBEBEB,
                        0xEEEED2
                    }[theme]);
        }

        /**
         * Returns the dark-square color.
         */
        public java.awt.Color dark() {
            return new java.awt.Color(
                    new int[] {
                        0xB58863,
                        0x78828C,
                        0x769656
                    }[theme]);
        }
    }

    /**
     * Creates a dialog initialized with the
     * currently applied settings.
     */
    public SettingsDialog(
            JFrame owner,
            Options current,
            Consumer<Options> apply) {

        super(owner, "Settings", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JComboBox<String> themes = new JComboBox<>(
                new String[] {"Wooden", "Gray", "Green"});

        JComboBox<String> pieces = new JComboBox<>(
                new String[] {
                    "Classic / custom images",
                    "Blue / red"
                });

        JComboBox<String> sizes = new JComboBox<>(
                new String[] {"Small", "Medium", "Large"});

        themes.setSelectedIndex(current.theme());
        pieces.setSelectedIndex(current.pieces());
        sizes.setSelectedIndex(current.size());

        JPanel panel =
                new JPanel(new GridLayout(4, 2, 10, 10));

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        16, 16, 16, 16));

        panel.add(new JLabel("Board style"));
        panel.add(themes);

        panel.add(new JLabel("Piece style"));
        panel.add(pieces);

        panel.add(new JLabel("Board size"));
        panel.add(sizes);

        JButton applyButton = new JButton("Apply");

        applyButton.addActionListener(event ->
                apply.accept(new Options(
                        themes.getSelectedIndex(),
                        pieces.getSelectedIndex(),
                        sizes.getSelectedIndex())));

        JButton close = new JButton("Close");
        close.addActionListener(event -> dispose());

        panel.add(applyButton);
        panel.add(close);

        setContentPane(panel);
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    /**
     * Applies the piece style to existing team display
     * components, including captured-piece displays.
     */
    public static void stylePieces(
            Component component,
            int style) {

        if (component instanceof ChessPieceDisplay display) {
            display.setColorStyle(style);
        }

        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                stylePieces(child, style);
            }
        }
    }
}
