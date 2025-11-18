package structural.composite;

import java.util.ArrayList;
import java.util.List;

/**
 * Demo class showing Composite pattern in action
 *
 * The Composite pattern allows you to compose objects into tree structures
 * to represent part-whole hierarchies.
 */
public class CompositeDemo {
    public static void main(String[] args) {
        System.out.println("=== Composite Pattern Demo ===\n");

        System.out.println("--- File System Example ---");
        demonstrateFileSystem();

        System.out.println("\n--- Organization Hierarchy Example ---");
        demonstrateOrganization();

        System.out.println("\n--- Graphic System Example ---");
        demonstrateGraphics();

        System.out.println("\n--- Menu System Example ---");
        demonstrateMenu();
    }

    /**
     * Classic example - File system with files and folders
     */
    private static void demonstrateFileSystem() {
        // Create files
        FileSystemComponent file1 = new File("document.txt", 100);
        FileSystemComponent file2 = new File("image.jpg", 500);
        FileSystemComponent file3 = new File("video.mp4", 2000);
        FileSystemComponent file4 = new File("readme.md", 50);

        // Create folders
        Folder root = new Folder("root");
        Folder documents = new Folder("documents");
        Folder media = new Folder("media");

        // Build hierarchy
        root.add(documents);
        root.add(media);
        root.add(file4);

        documents.add(file1);
        media.add(file2);
        media.add(file3);

        // Display entire structure
        System.out.println("File System Structure:");
        root.display(0);
        System.out.println("\nTotal size: " + root.getSize() + " KB");

        // Remove a file
        System.out.println("\nRemoving image.jpg...");
        media.remove(file2);
        System.out.println("New total size: " + root.getSize() + " KB");
    }

    /**
     * Real-world example - Organization with employees and departments
     */
    private static void demonstrateOrganization() {
        // Create employees
        Employee dev1 = new Developer("John", 100000);
        Employee dev2 = new Developer("Jane", 110000);
        Employee manager = new Manager("Bob", 150000);
        Employee cto = new Manager("Alice", 200000);

        // Create departments
        Department engineering = new Department("Engineering");
        Department company = new Department("Company");

        // Build hierarchy
        company.add(cto);
        company.add(engineering);
        engineering.add(manager);
        engineering.add(dev1);
        engineering.add(dev2);

        // Display structure
        System.out.println("Organization Structure:");
        company.showDetails("");
        System.out.println("\nTotal Salary Budget: $" + company.getSalary());
    }

    /**
     * Graphics example - Shapes that can be grouped
     */
    private static void demonstrateGraphics() {
        // Create simple shapes
        GraphicComponent circle = new Circle();
        GraphicComponent square = new Square();
        GraphicComponent triangle = new Triangle();

        // Create composite shapes
        CompositeGraphic group1 = new CompositeGraphic();
        group1.add(circle);
        group1.add(square);

        CompositeGraphic group2 = new CompositeGraphic();
        group2.add(triangle);

        CompositeGraphic allGraphics = new CompositeGraphic();
        allGraphics.add(group1);
        allGraphics.add(group2);

        // Draw all graphics
        System.out.println("Drawing all graphics:");
        allGraphics.draw();
    }

    /**
     * Menu example - Nested menu structure
     */
    private static void demonstrateMenu() {
        // Create menu items
        MenuComponent newFile = new MenuItem("New", "Ctrl+N");
        MenuComponent openFile = new MenuItem("Open", "Ctrl+O");
        MenuComponent saveFile = new MenuItem("Save", "Ctrl+S");
        MenuComponent exit = new MenuItem("Exit", "Alt+F4");

        MenuComponent cut = new MenuItem("Cut", "Ctrl+X");
        MenuComponent copy = new MenuItem("Copy", "Ctrl+C");
        MenuComponent paste = new MenuItem("Paste", "Ctrl+V");

        // Create menus
        Menu fileMenu = new Menu("File");
        fileMenu.add(newFile);
        fileMenu.add(openFile);
        fileMenu.add(saveFile);
        fileMenu.add(exit);

        Menu editMenu = new Menu("Edit");
        editMenu.add(cut);
        editMenu.add(copy);
        editMenu.add(paste);

        Menu menuBar = new Menu("MenuBar");
        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        // Display menu structure
        menuBar.display(0);
    }
}

// ============================================
// File System Example
// ============================================

/**
 * Component interface - defines operations for both leaf and composite objects
 */
interface FileSystemComponent {
    String getName();
    int getSize();
    void display(int depth);
}

/**
 * Leaf class - represents a file
 */
class File implements FileSystemComponent {
    private String name;
    private int size;

    public File(String name, int size) {
        this.name = name;
        this.size = size;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void display(int depth) {
        System.out.println(" ".repeat(depth * 2) + "📄 " + name + " (" + size + " KB)");
    }
}

/**
 * Composite class - represents a folder that can contain files and other folders
 */
class Folder implements FileSystemComponent {
    private String name;
    private List<FileSystemComponent> children = new ArrayList<>();

    public Folder(String name) {
        this.name = name;
    }

    public void add(FileSystemComponent component) {
        children.add(component);
    }

    public void remove(FileSystemComponent component) {
        children.remove(component);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getSize() {
        int totalSize = 0;
        for (FileSystemComponent child : children) {
            totalSize += child.getSize();
        }
        return totalSize;
    }

    @Override
    public void display(int depth) {
        System.out.println(" ".repeat(depth * 2) + "📁 " + name + "/");
        for (FileSystemComponent child : children) {
            child.display(depth + 1);
        }
    }
}

// ============================================
// Organization Example
// ============================================

/**
 * Component interface for organization hierarchy
 */
interface Employee {
    String getName();
    double getSalary();
    void showDetails(String indent);
}

/**
 * Leaf - Individual developer
 */
class Developer implements Employee {
    private String name;
    private double salary;

    public Developer(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getSalary() {
        return salary;
    }

    @Override
    public void showDetails(String indent) {
        System.out.println(indent + "Developer: " + name + " ($" + salary + ")");
    }
}

/**
 * Leaf - Individual manager
 */
class Manager implements Employee {
    private String name;
    private double salary;

    public Manager(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getSalary() {
        return salary;
    }

    @Override
    public void showDetails(String indent) {
        System.out.println(indent + "Manager: " + name + " ($" + salary + ")");
    }
}

/**
 * Composite - Department containing employees and sub-departments
 */
class Department implements Employee {
    private String name;
    private List<Employee> employees = new ArrayList<>();

    public Department(String name) {
        this.name = name;
    }

    public void add(Employee employee) {
        employees.add(employee);
    }

    public void remove(Employee employee) {
        employees.remove(employee);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getSalary() {
        double totalSalary = 0;
        for (Employee employee : employees) {
            totalSalary += employee.getSalary();
        }
        return totalSalary;
    }

    @Override
    public void showDetails(String indent) {
        System.out.println(indent + "Department: " + name);
        for (Employee employee : employees) {
            employee.showDetails(indent + "  ");
        }
    }
}

// ============================================
// Graphics Example
// ============================================

/**
 * Component interface for graphics
 */
interface GraphicComponent {
    void draw();
}

/**
 * Leaf - Circle
 */
class Circle implements GraphicComponent {
    @Override
    public void draw() {
        System.out.println("  Drawing a Circle");
    }
}

/**
 * Leaf - Square
 */
class Square implements GraphicComponent {
    @Override
    public void draw() {
        System.out.println("  Drawing a Square");
    }
}

/**
 * Leaf - Triangle
 */
class Triangle implements GraphicComponent {
    @Override
    public void draw() {
        System.out.println("  Drawing a Triangle");
    }
}

/**
 * Composite - Group of graphics
 */
class CompositeGraphic implements GraphicComponent {
    private List<GraphicComponent> graphics = new ArrayList<>();

    public void add(GraphicComponent graphic) {
        graphics.add(graphic);
    }

    public void remove(GraphicComponent graphic) {
        graphics.remove(graphic);
    }

    @Override
    public void draw() {
        for (GraphicComponent graphic : graphics) {
            graphic.draw();
        }
    }
}

// ============================================
// Menu Example
// ============================================

/**
 * Component interface for menu system
 */
interface MenuComponent {
    void display(int depth);
}

/**
 * Leaf - Menu item
 */
class MenuItem implements MenuComponent {
    private String name;
    private String shortcut;

    public MenuItem(String name, String shortcut) {
        this.name = name;
        this.shortcut = shortcut;
    }

    @Override
    public void display(int depth) {
        System.out.println(" ".repeat(depth * 2) + "- " + name + " (" + shortcut + ")");
    }
}

/**
 * Composite - Menu containing items and sub-menus
 */
class Menu implements MenuComponent {
    private String name;
    private List<MenuComponent> components = new ArrayList<>();

    public Menu(String name) {
        this.name = name;
    }

    public void add(MenuComponent component) {
        components.add(component);
    }

    public void remove(MenuComponent component) {
        components.remove(component);
    }

    @Override
    public void display(int depth) {
        System.out.println(" ".repeat(depth * 2) + "+ " + name);
        for (MenuComponent component : components) {
            component.display(depth + 1);
        }
    }
}
