package creational.abstract_factory;

/**
 * Concrete product A1 - Windows button
 */
public class WindowsButton implements Button {
    @Override
    public void render() {
        System.out.println("Rendering Windows-style button");
    }

    @Override
    public void onClick() {
        System.out.println("Windows button clicked - Playing Windows sound");
    }
}
