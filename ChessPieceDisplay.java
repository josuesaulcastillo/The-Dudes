import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Displays one chess piece, using a custom PNG when available.
 * This class does not change piece positions or handle game rules.
 */
public class ChessPieceDisplay extends JLabel {
    private Piece displayedPiece;
    private Path imageDirectory = Paths.get("assets", "pieces");
    private int pieceSize = 60;

    /** Creates an empty piece display. */
    public ChessPieceDisplay() {
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
        setOpaque(false);
        setFont(new Font("DejaVu Sans", Font.PLAIN, pieceSize));
    }

    /**
     * Displays a piece, or clears the label when piece is null.
     * This only changes the image on the screen, not the game model.
     *
     * @param piece the piece to show, or null for an empty square
     */
    public void setPiece(Piece piece) {
        displayedPiece = piece;
        updateDisplay();
    }

    /** Returns the piece object represented by this label. */
    public Piece getDisplayedPiece() {
        return displayedPiece;
    }

    /**
     * Sets the folder containing custom piece images.
     * Example: Paths.get("assets", "pieces").
     * Images are loaded again immediately.
     *
     * @param directory path to the folder with the 12 PNG files
     */
    public void setImageDirectory(Path directory) {
        if (directory == null) {
            throw new IllegalArgumentException("Image directory cannot be null.");
        }
        imageDirectory = directory;
        updateDisplay();
    }

    /** Returns the current directory searched for piece images. */
    public Path getImageDirectory() {
        return imageDirectory;
    }

    /** Sets the image width/height and Unicode font size in pixels. */
    public void setPieceSize(int pixels) {
        if (pixels < 12) {
            throw new IllegalArgumentException("Piece size must be at least 12.");
        }
        pieceSize = pixels;
        setFont(new Font("DejaVu Sans", Font.PLAIN, pixels));
        updateDisplay();
    }

    /** Returns the current piece display size. */
    public int getPieceSize() {
        return pieceSize;
    }

    /** Reloads the current image, useful after replacing image files. */
    public void reloadImage() {
        updateDisplay();
    }

    private void updateDisplay() {
        setIcon(null);
        setText("");
        setToolTipText(null);
        if (displayedPiece == null) {
            return;
        }

        String type = pieceType(displayedPiece);
        boolean isWhite = displayedPiece.getColor().isColor("white");
        String color = isWhite ? "white" : "black";
        String imageFile = color + "_" + type + ".png";
        BufferedImage image = loadImage(imageFile);

        if (image != null) {
            Image scaled = image.getScaledInstance(pieceSize, pieceSize, Image.SCALE_SMOOTH);
            setIcon(new ImageIcon(scaled));
        } else {
            setText(unicodeSymbol(type, isWhite));
        }
        setToolTipText(color + " " + type);
    }

    private BufferedImage loadImage(String filename) {
        // Option 1: files in assets/pieces/ under the project root.
        Path imagePath = imageDirectory.resolve(filename);
        if (Files.isRegularFile(imagePath)) {
            try {
                return ImageIO.read(imagePath.toFile());
            } catch (IOException exception) {
                System.err.println("Could not read image: " + imagePath);
            }
        }

        // Option 2: src/main/resources/pieces/ on the classpath (Gradle/Maven).
        try (InputStream stream = getClass().getResourceAsStream("/pieces/" + filename)) {
            if (stream != null) {
                return ImageIO.read(stream);
            }
        } catch (IOException exception) {
            System.err.println("Could not read classpath image: " + filename);
        }
        return null; // Missing image: fall back to the Unicode symbol.
    }

    private static String pieceType(Piece piece) {
        if (piece instanceof King) return "king";
        if (piece instanceof Queen) return "queen";
        if (piece instanceof Rook) return "rook";
        if (piece instanceof Bishop) return "bishop";
        if (piece instanceof Knight) return "knight";
        if (piece instanceof Pawn) return "pawn";
        throw new IllegalArgumentException("Unsupported piece class: "
                + piece.getClass().getSimpleName());
    }

    private static String unicodeSymbol(String type, boolean white) {
        switch (type) {
            case "king":   return white ? "\u2654" : "\u265A";
            case "queen":  return white ? "\u2655" : "\u265B";
            case "rook":   return white ? "\u2656" : "\u265C";
            case "bishop": return white ? "\u2657" : "\u265D";
            case "knight": return white ? "\u2658" : "\u265E";
            case "pawn":   return white ? "\u2659" : "\u265F";
            default: return "?";
        }
    }
}
