package creational.prototype;

/**
 * Concrete prototype - Circle
 */
public class Circle extends Shape {
    private int radius;

    public Circle() {
        this.type = "Circle";
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    public void draw() {
        System.out.println("Drawing a " + color + " circle with radius " + radius);
    }

    @Override
    public Circle clone() {
        return (Circle) super.clone();
    }

    @Override
    public String toString() {
        return String.format("Circle [id=%s, color=%s, radius=%d]", id, color, radius);
    }
}
