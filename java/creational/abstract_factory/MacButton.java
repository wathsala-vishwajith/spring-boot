package creational.abstract_factory;

/**
 * Concrete product A2 - Mac button
 */
public class MacButton implements Button {
    @Override
    public void render() {
        System.out.println("Rendering macOS-style button with rounded corners");
    }

    @Override
    public void onClick() {
        System.out.println("Mac button clicked - Smooth animation");
    }
}
