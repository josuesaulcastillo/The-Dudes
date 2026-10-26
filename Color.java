public class Color {
    /**
     * String saying either "white" or "black"
     */
    public String color;

    /**
     * Class constructor.
     * 
     * @param color String that MUST be "black" or "white"
     */
    public Color(String color) {
        String normalized = color == null ? null : color.trim().toLowerCase();
        if ("white".equals(normalized) || "black".equals(normalized)) {
            this.color = normalized;
        } else {
            throw new IllegalArgumentException("Invalid color: " + color);
        }
    }

    /**
     * Returns whether one color equals another color.
     * 
     * @param other String that is either "white" or "black"
     * @return Boolean stating whether this color equals another color
     */
    public boolean isColor(String other) {
        if (other == null) {
            return false;
        }
        return this.color != null && this.color.equals(other.trim().toLowerCase());
    }

    /**
     * Returns whether one color equals another color.
     * 
     * @param other Color that is either "white" or "black"
     * @return Boolean stating whether this color equals another color
     */
    public boolean isColor(Color other) {
        if (other == null || this.color == null || other.color == null) {
            return false;
        }
        return this.color.equals(other.color);
    }
}
