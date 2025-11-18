package creational.abstract_factory;

/**
 * Client code that works with factories and products through abstract interfaces
 */
public class Application {
    private Button button;
    private Checkbox checkbox;

    public Application(GUIFactory factory) {
        this.button = factory.createButton();
        this.checkbox = factory.createCheckbox();
    }

    public void render() {
        button.render();
        checkbox.render();
    }

    public void interact() {
        button.onClick();
        checkbox.onCheck();
    }
}
