package behavioral.visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Demo class showing Visitor pattern in action
 */
public class VisitorDemo {
    public static void main(String[] args) {
        System.out.println("=== Visitor Pattern Demo ===\n");

        System.out.println("--- Shape Rendering ---");
        List<Shape> shapes = new ArrayList<>();
        shapes.add(new Circle(10));
        shapes.add(new Rectangle(5, 7));
        shapes.add(new Triangle(3, 4, 5));

        ShapeVisitor areaCalculator = new AreaCalculator();
        ShapeVisitor perimeter Calculator = new PerimeterCalculator();
        ShapeVisitor renderer = new ShapeRenderer();

        System.out.println("Calculating Areas:");
        for (Shape shape : shapes) {
            shape.accept(areaCalculator);
        }

        System.out.println("\nCalculating Perimeters:");
        for (Shape shape : shapes) {
            shape.accept(perimeterCalculator);
        }

        System.out.println("\nRendering Shapes:");
        for (Shape shape : shapes) {
            shape.accept(renderer);
        }

        System.out.println("\n--- File System Operations ---");
        FileSystemElement root = new Directory("root");
        FileSystemElement file1 = new File("document.txt", 100);
        FileSystemElement file2 = new File("image.jpg", 500);
        FileSystemElement folder = new Directory("photos");

        List<FileSystemElement> elements = new ArrayList<>();
        elements.add(root);
        elements.add(file1);
        elements.add(file2);
        elements.add(folder);

        FileSystemVisitor sizeCalculator = new SizeCalculator();
        FileSystemVisitor searchVisitor = new SearchVisitor("jpg");

        System.out.println("Calculating total size:");
        for (FileSystemElement element : elements) {
            element.accept(sizeCalculator);
        }

        System.out.println("\nSearching for .jpg files:");
        for (FileSystemElement element : elements) {
            element.accept(searchVisitor);
        }
    }
}

// Visitor interface
interface ShapeVisitor {
    void visit(Circle circle);
    void visit(Rectangle rectangle);
    void visit(Triangle triangle);
}

// Element interface
interface Shape {
    void accept(ShapeVisitor visitor);
}

// Concrete Elements
class Circle implements Shape {
    private double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public void accept(ShapeVisitor visitor) {
        visitor.visit(this);
    }
}

class Rectangle implements Shape {
    private double width;
    private double height;

    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    @Override
    public void accept(ShapeVisitor visitor) {
        visitor.visit(this);
    }
}

class Triangle implements Shape {
    private double a, b, c;

    public Triangle(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public double getA() { return a; }
    public double getB() { return b; }
    public double getC() { return c; }

    @Override
    public void accept(ShapeVisitor visitor) {
        visitor.visit(this);
    }
}

// Concrete Visitors
class AreaCalculator implements ShapeVisitor {
    @Override
    public void visit(Circle circle) {
        double area = Math.PI * circle.getRadius() * circle.getRadius();
        System.out.println("Circle area: " + String.format("%.2f", area));
    }

    @Override
    public void visit(Rectangle rectangle) {
        double area = rectangle.getWidth() * rectangle.getHeight();
        System.out.println("Rectangle area: " + area);
    }

    @Override
    public void visit(Triangle triangle) {
        double s = (triangle.getA() + triangle.getB() + triangle.getC()) / 2;
        double area = Math.sqrt(s * (s - triangle.getA()) * (s - triangle.getB()) * (s - triangle.getC()));
        System.out.println("Triangle area: " + String.format("%.2f", area));
    }
}

class PerimeterCalculator implements ShapeVisitor {
    @Override
    public void visit(Circle circle) {
        double perimeter = 2 * Math.PI * circle.getRadius();
        System.out.println("Circle perimeter: " + String.format("%.2f", perimeter));
    }

    @Override
    public void visit(Rectangle rectangle) {
        double perimeter = 2 * (rectangle.getWidth() + rectangle.getHeight());
        System.out.println("Rectangle perimeter: " + perimeter);
    }

    @Override
    public void visit(Triangle triangle) {
        double perimeter = triangle.getA() + triangle.getB() + triangle.getC();
        System.out.println("Triangle perimeter: " + perimeter);
    }
}

class ShapeRenderer implements ShapeVisitor {
    @Override
    public void visit(Circle circle) {
        System.out.println("Rendering Circle with radius " + circle.getRadius());
    }

    @Override
    public void visit(Rectangle rectangle) {
        System.out.println("Rendering Rectangle " + rectangle.getWidth() + "x" + rectangle.getHeight());
    }

    @Override
    public void visit(Triangle triangle) {
        System.out.println("Rendering Triangle with sides " + triangle.getA() + ", " + triangle.getB() + ", " + triangle.getC());
    }
}

// File System Example
interface FileSystemVisitor {
    void visit(File file);
    void visit(Directory directory);
}

interface FileSystemElement {
    void accept(FileSystemVisitor visitor);
}

class File implements FileSystemElement {
    private String name;
    private int size;

    public File(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public String getName() { return name; }
    public int getSize() { return size; }

    @Override
    public void accept(FileSystemVisitor visitor) {
        visitor.visit(this);
    }
}

class Directory implements FileSystemElement {
    private String name;

    public Directory(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    @Override
    public void accept(FileSystemVisitor visitor) {
        visitor.visit(this);
    }
}

class SizeCalculator implements FileSystemVisitor {
    private int totalSize = 0;

    @Override
    public void visit(File file) {
        totalSize += file.getSize();
        System.out.println("File: " + file.getName() + " - " + file.getSize() + " KB");
    }

    @Override
    public void visit(Directory directory) {
        System.out.println("Directory: " + directory.getName());
    }

    public int getTotalSize() {
        return totalSize;
    }
}

class SearchVisitor implements FileSystemVisitor {
    private String extension;

    public SearchVisitor(String extension) {
        this.extension = extension;
    }

    @Override
    public void visit(File file) {
        if (file.getName().endsWith(extension)) {
            System.out.println("Found: " + file.getName());
        }
    }

    @Override
    public void visit(Directory directory) {
        // Skip directories in search
    }
}
