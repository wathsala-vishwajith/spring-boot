package creational.abstract_factory;

/**
 * Concrete product B1 - Windows checkbox
 */
public class WindowsCheckbox implements Checkbox {
    @Override
    public void render() {
        System.out.println("Rendering Windows-style checkbox");
    }

    @Override
    public void onCheck() {
        System.out.println("Windows checkbox checked - Square style");
    }
}
