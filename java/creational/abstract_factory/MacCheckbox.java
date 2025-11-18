package creational.abstract_factory;

/**
 * Concrete product B2 - Mac checkbox
 */
public class MacCheckbox implements Checkbox {
    @Override
    public void render() {
        System.out.println("Rendering macOS-style checkbox with gradient");
    }

    @Override
    public void onCheck() {
        System.out.println("Mac checkbox checked - Checkmark with animation");
    }
}
