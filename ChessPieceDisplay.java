import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Team piece display with custom PNG support
 * and two visible color styles.
 */
public class ChessPieceDisplay extends JLabel {

    private Piece displayedPiece;

    private Path imageDirectory =
            Paths.get("assets", "pieces");

    private int pieceSize = 60;
    private int colorStyle;

    /**
     * Creates an empty transparent piece label.
     */
    public ChessPieceDisplay() {
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
        setOpaque(false);
    }

    /**
     * Updates the displayed piece without modifying the model.
     */
    public void setPiece(Piece piece) {
        displayedPiece = piece;
        updateDisplay();
    }

    /**
     * Returns the displayed model object.
     */
    public Piece getDisplayedPiece() {
        return displayedPiece;
    }

    /**
     * Changes the custom PNG directory and reloads the display.
     */
    public void setImageDirectory(Path directory) {
        imageDirectory =
                java.util.Objects.requireNonNull(directory);

        updateDisplay();
    }

    /**
     * Returns the custom image directory.
     */
    public Path getImageDirectory() {
        return imageDirectory;
    }

    /**
     * Sets the displayed piece dimensions.
     */
    public void setPieceSize(int pixels) {
        if (pixels < 12) {
            throw new IllegalArgumentException(
                    "Piece size must be at least 12.");
        }

        pieceSize = pixels;
        updateDisplay();
    }

    /**
     * Returns the displayed piece size.
     */
    public int getPieceSize() {
        return pieceSize;
    }

    /**
     * Selects classic/custom images (0)
     * or blue/red outlined symbols (1).
     */
    public void setColorStyle(int style) {
        if (style < 0 || style > 1) {
            throw new IllegalArgumentException(
                    "Invalid piece style.");
        }

        if (colorStyle != style) {
            colorStyle = style;
            updateDisplay();
        }
    }

    /**
     * Reloads the current custom image.
     */
    public void reloadImage() {
        updateDisplay();
    }

    /**
     * Uses custom art for classic mode and
     * readable outlined symbols otherwise.
     */
    private void updateDisplay() {
        setIcon(null);
        setText("");
        setToolTipText(null);

        if (displayedPiece == null) {
            return;
        }

        String type = displayedPiece.getClass()
                .getSimpleName()
                .toLowerCase(java.util.Locale.ROOT);

        boolean white =
                displayedPiece.getColor().isColor("white");

        String label = (white ? "white " : "black ") + type;

        BufferedImage image = colorStyle == 0
                ? loadImage(
                        (white ? "white_" : "black_")
                                + type + ".png")
                : null;

        if (image != null) {
            setIcon(new ImageIcon(
                    image.getScaledInstance(
                            pieceSize,
                            pieceSize,
                            Image.SCALE_SMOOTH)));
        } else {
            setIcon(new ImageIcon(symbol(type, white)));
        }

        setToolTipText(label);
        getAccessibleContext().setAccessibleName(label);
    }

    /**
     * Loads a PNG from the existing team file
     * or classpath locations.
     */
    private BufferedImage loadImage(String filename) {
        Path path = imageDirectory.resolve(filename);

        try {
            if (Files.isRegularFile(path)) {
                BufferedImage image =
                        ImageIO.read(path.toFile());

                if (image != null) {
                    return image;
                }
            }
        } catch (IOException ignored) {
            // Use the symbol fallback.
        }

        try (InputStream stream =
                     getClass().getResourceAsStream(
                             "/pieces/" + filename)) {

            return stream == null
                    ? null
                    : ImageIO.read(stream);

        } catch (IOException ignored) {
            return null;
        }
    }

    /**
     * Renders a scalable glyph with a contrasting outline.
     * Falls back to a letter if the font lacks chess symbols.
     */
    private BufferedImage symbol(String type, boolean white) {
        String types = "king queen rook bishop knight pawn";

        int index =
                java.util.List.of(types.split(" ")).indexOf(type);

        if (index < 0) {
            throw new IllegalArgumentException(
                    "Unsupported piece: " + type);
        }

        String glyph =
                String.valueOf((char) (0x265A + index));

        Font font =
                new Font(Font.SERIF, Font.PLAIN, pieceSize);

        if (!font.canDisplay(glyph.charAt(0))) {
            glyph = "KQRBNP".substring(index, index + 1);
        }

        BufferedImage image = new BufferedImage(
                pieceSize,
                pieceSize,
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = image.createGraphics();

        try {
            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            Shape shape = font.createGlyphVector(
                    g.getFontRenderContext(),
                    glyph).getOutline();

            java.awt.geom.Rectangle2D bounds =
                    shape.getBounds2D();

            double scale = Math.min(
                    (pieceSize - 4) / bounds.getWidth(),
                    (pieceSize - 4) / bounds.getHeight());

            g.translate(pieceSize / 2.0, pieceSize / 2.0);
            g.scale(scale, scale);

            g.translate(
                    -bounds.getCenterX(),
                    -bounds.getCenterY());

            g.setColor(new java.awt.Color(
                    colorStyle == 0
                            ? (white ? 0xFFFFFF : 0x202020)
                            : (white ? 0x2463CF : 0xB22222)));

            g.fill(shape);

            g.setColor(
                    white
                            ? java.awt.Color.DARK_GRAY
                            : java.awt.Color.WHITE);

            g.setStroke(
                    new BasicStroke((float) (1.0 / scale)));

            g.draw(shape);
        } finally {
            g.dispose();
        }

        return image;
    }
}
