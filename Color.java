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
        if(color.equals("white") || color.equals("black")) {
            this.color = color;
        }
        else {
            System.out.println("Invalid color.");
        }
    }

    /**
     * Returns whether one color equals another color.
     * 
     * @param other String that is either "white" or "black"
     * @return Boolean stating whether this color equals another color
     */
    public boolean isColor(String other) {
        if (this.color.equals(other)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Returns whether one color equals another color.
     * 
     * @param other Color that is either "white" or "black"
     * @return Boolean stating whether this color equals another color
     */
    public boolean isColor(Color other) {
        if (this.color.equals(other.color)) {
            return true;
        }
        else {
            return false;
        }
    }
}
