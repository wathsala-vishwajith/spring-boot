package creational.abstract_factory;

/**
 * Abstract Factory interface - declares creation methods for each product type
 */
public interface GUIFactory {
    Button createButton();
    Checkbox createCheckbox();
}
