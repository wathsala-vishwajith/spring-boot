package creational.prototype;

/**
 * Concrete prototype - Rectangle
 */
public class Rectangle extends Shape {
    private int width;
    private int height;

    public Rectangle() {
        this.type = "Rectangle";
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void draw() {
        System.out.println("Drawing a " + color + " rectangle: " + width + "x" + height);
    }

    @Override
    public Rectangle clone() {
        return (Rectangle) super.clone();
    }

    @Override
    public String toString() {
        return String.format("Rectangle [id=%s, color=%s, width=%d, height=%d]",
            id, color, width, height);
    }
}
