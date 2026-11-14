package GUI;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.*;

import Pieces.Piece;

/**
 * Piece display with classic, blue/red, and custom image styles.
 */
public class ChessPieceDisplay extends JLabel {

    private record ImageKey(Path directory, String filename, int size) { }

    private static final Map<ImageKey, ImageIcon> IMAGE_CACHE =
            new HashMap<>();
    private static final Set<ImageKey> MISSING_IMAGES =
            new HashSet<>();

    private Piece displayedPiece;

    private Path imageDirectory =
            Paths.get("Pieces", "Images");

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
     * Selects classic symbols (0), blue/red symbols (1),
     * or custom images (2).
     */
    public void setColorStyle(int style) {
        if (style < 0 || style > 2) {
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
        if (displayedPiece != null) {
            String type = displayedPiece.getClass()
                    .getSimpleName()
                    .toLowerCase(java.util.Locale.ROOT);
            String filename = (displayedPiece.getColor().isColor("white")
                    ? "white_" : "black_") + type + ".png";
            ImageKey key = new ImageKey(
                    imageDirectory.toAbsolutePath().normalize(),
                    filename,
                    pieceSize);
            synchronized (IMAGE_CACHE) {
                IMAGE_CACHE.remove(key);
                MISSING_IMAGES.remove(key);
            }
        }
        updateDisplay();
    }

    /** Updates this label using its current style and piece. */
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

        if (colorStyle == 2) {
            ImageIcon image = loadImage(
                    (white ? "white_" : "black_") + type + ".png");
            if (image != null) {
                setIcon(image);
            } else {
                setIcon(new ImageIcon(symbol(type, white)));
            }
        } else {
            setIcon(new ImageIcon(symbol(type, white)));
        }

        setToolTipText(label);
        getAccessibleContext().setAccessibleName(label);
    }

    /**
     * Loads and scales a PNG once for each folder, filename, and size.
     */
    private ImageIcon loadImage(String filename) {
        Path directory = imageDirectory.toAbsolutePath().normalize();
        ImageKey key = new ImageKey(directory, filename, pieceSize);

        synchronized (IMAGE_CACHE) {
            ImageIcon cached = IMAGE_CACHE.get(key);
            if (cached != null) {
                return cached;
            }
            if (MISSING_IMAGES.contains(key)) {
                return null;
            }

            BufferedImage image = readImage(directory.resolve(filename), filename);
            if (image == null) {
                MISSING_IMAGES.add(key);
                return null;
            }

            BufferedImage scaled = new BufferedImage(
                    pieceSize, pieceSize, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = scaled.createGraphics();
            try {
                graphics.setRenderingHint(
                        RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                graphics.setRenderingHint(
                        RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY);
                graphics.drawImage(
                        image, 0, 0, pieceSize, pieceSize, null);
            } finally {
                graphics.dispose();
            }

            ImageIcon icon = new ImageIcon(scaled);
            IMAGE_CACHE.put(key, icon);
            return icon;
        }
    }

    private BufferedImage readImage(Path path, String filename) {
        try {
            if (Files.isRegularFile(path)) {
                BufferedImage image = ImageIO.read(path.toFile());
                if (image != null) {
                    return image;
                }
            }
        } catch (IOException exception) {
            System.err.println("Could not read piece image: " + path
                    + " (" + exception.getMessage() + ")");
        }

        String[] resources = {
            "/Pieces/Images/" + filename,
            "/pieces/" + filename
        };
        for (String resource : resources) {
            try (InputStream stream =
                         getClass().getResourceAsStream(resource)) {
                if (stream != null) {
                    BufferedImage image = ImageIO.read(stream);
                    if (image != null) {
                        return image;
                    }
                }
            } catch (IOException exception) {
                System.err.println("Could not read piece image resource: "
                        + resource + " (" + exception.getMessage() + ")");
            }
        }
        return null;
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
                    colorStyle != 1
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
