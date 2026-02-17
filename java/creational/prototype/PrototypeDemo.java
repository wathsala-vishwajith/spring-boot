package creational.prototype;

/**
 * Demo class showing Prototype pattern in action
 */
public class PrototypeDemo {
    public static void main(String[] args) {
        System.out.println("=== Prototype Pattern Demo ===\n");

        // Create and register prototypes
        ShapeRegistry registry = new ShapeRegistry();
        registry.loadDefaultShapes();

        System.out.println("--- Cloning from Registry ---");

        // Clone blue circle
        Shape clonedCircle1 = registry.getShape("blue_circle");
        System.out.println("Cloned: " + clonedCircle1);
        clonedCircle1.draw();

        // Clone another blue circle and modify it
        Shape clonedCircle2 = registry.getShape("blue_circle");
        clonedCircle2.setId("4");
        clonedCircle2.setColor("Yellow");
        System.out.println("\nCloned and modified: " + clonedCircle2);
        clonedCircle2.draw();

        // Clone red circle
        Shape clonedCircle3 = registry.getShape("red_circle");
        System.out.println("\nCloned: " + clonedCircle3);
        clonedCircle3.draw();

        // Clone rectangle
        Shape clonedRectangle = registry.getShape("green_rectangle");
        System.out.println("\nCloned: " + clonedRectangle);
        clonedRectangle.draw();

        System.out.println("\n--- Performance Comparison ---");
        demonstratePerformance();

        System.out.println("\n--- Deep Copy vs Shallow Copy ---");
        DeepCopyExample.demonstrateShallowCopyProblem();
        DeepCopyExample.demonstrateDeepCopy();
    }

    private static void demonstratePerformance() {
        // Create a complex object
        Circle complexCircle = new Circle();
        complexCircle.setId("999");
        complexCircle.setColor("Purple");
        complexCircle.setRadius(100);

        long startTime, endTime;

        // Measure cloning time
        startTime = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            Circle cloned = complexCircle.clone();
        }
        endTime = System.nanoTime();
        System.out.println("Cloning 10,000 circles: " +
            (endTime - startTime) / 1_000_000.0 + " ms");

        // Measure creation time
        startTime = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            Circle newCircle = new Circle();
            newCircle.setId("999");
            newCircle.setColor("Purple");
            newCircle.setRadius(100);
        }
        endTime = System.nanoTime();
        System.out.println("Creating 10,000 circles: " +
            (endTime - startTime) / 1_000_000.0 + " ms");

        System.out.println("\nNote: Cloning is typically faster for complex objects");
    }
}
