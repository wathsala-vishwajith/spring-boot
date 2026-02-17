package creational.prototype;

import java.util.HashMap;
import java.util.Map;

/**
 * Prototype Registry - stores and manages prototype objects
 */
public class ShapeRegistry {
    private Map<String, Shape> shapeMap = new HashMap<>();

    /**
     * Register a prototype
     */
    public void registerShape(String key, Shape shape) {
        shapeMap.put(key, shape);
    }

    /**
     * Get a clone of the registered prototype
     */
    public Shape getShape(String key) {
        Shape prototype = shapeMap.get(key);
        if (prototype == null) {
            throw new IllegalArgumentException("Shape not found: " + key);
        }
        return prototype.clone();
    }

    /**
     * Load default shapes into registry
     */
    public void loadDefaultShapes() {
        Circle blueCircle = new Circle();
        blueCircle.setId("1");
        blueCircle.setColor("Blue");
        blueCircle.setRadius(10);
        registerShape("blue_circle", blueCircle);

        Circle redCircle = new Circle();
        redCircle.setId("2");
        redCircle.setColor("Red");
        redCircle.setRadius(15);
        registerShape("red_circle", redCircle);

        Rectangle greenRectangle = new Rectangle();
        greenRectangle.setId("3");
        greenRectangle.setColor("Green");
        greenRectangle.setWidth(20);
        greenRectangle.setHeight(10);
        registerShape("green_rectangle", greenRectangle);
    }
}
