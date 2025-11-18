package behavioral.state;

/**
 * Demo class showing State pattern in action
 */
public class StateDemo {
    public static void main(String[] args) {
        System.out.println("=== State Pattern Demo ===\n");

        System.out.println("--- Document Workflow ---");
        Document doc = new Document();
        doc.render();
        doc.publish();

        doc.render();
        doc.publish();

        doc.render();
        doc.publish();

        System.out.println("\n--- Vending Machine ---");
        VendingMachine machine = new VendingMachine();
        machine.selectProduct();
        machine.insertMoney();
        machine.selectProduct();
        machine.dispense();
        machine.dispense();
    }
}

// State interface
interface DocumentState {
    void publish(Document doc);
}

// Concrete States
class DraftState implements DocumentState {
    @Override
    public void publish(Document doc) {
        System.out.println("Draft -> Moderation (publishing for review)");
        doc.setState(new ModerationState());
    }
}

class ModerationState implements DocumentState {
    @Override
    public void publish(Document doc) {
        System.out.println("Moderation -> Published (approved)");
        doc.setState(new PublishedState());
    }
}

class PublishedState implements DocumentState {
    @Override
    public void publish(Document doc) {
        System.out.println("Already published!");
    }
}

// Context
class Document {
    private DocumentState state;

    public Document() {
        state = new DraftState();
    }

    public void setState(DocumentState state) {
        this.state = state;
    }

    public void publish() {
        state.publish(this);
    }

    public void render() {
        System.out.println("Rendering document in current state");
    }
}

// Vending Machine Example
interface VendingMachineState {
    void selectProduct(VendingMachine machine);
    void insertMoney(VendingMachine machine);
    void dispense(VendingMachine machine);
}

class IdleState implements VendingMachineState {
    @Override
    public void selectProduct(VendingMachine machine) {
        System.out.println("Product selected. Please insert money.");
        machine.setState(machine.getProductSelectedState());
    }

    @Override
    public void insertMoney(VendingMachine machine) {
        System.out.println("Please select a product first");
    }

    @Override
    public void dispense(VendingMachine machine) {
        System.out.println("Please select a product first");
    }
}

class ProductSelectedState implements VendingMachineState {
    @Override
    public void selectProduct(VendingMachine machine) {
        System.out.println("Product already selected");
    }

    @Override
    public void insertMoney(VendingMachine machine) {
        System.out.println("Money received. Dispensing product...");
        machine.setState(machine.getDispensingState());
    }

    @Override
    public void dispense(VendingMachine machine) {
        System.out.println("Please insert money first");
    }
}

class DispensingState implements VendingMachineState {
    @Override
    public void selectProduct(VendingMachine machine) {
        System.out.println("Already dispensing");
    }

    @Override
    public void insertMoney(VendingMachine machine) {
        System.out.println("Already dispensing");
    }

    @Override
    public void dispense(VendingMachine machine) {
        System.out.println("Product dispensed! Thank you!");
        machine.setState(machine.getIdleState());
    }
}

class VendingMachine {
    private VendingMachineState idleState;
    private VendingMachineState productSelectedState;
    private VendingMachineState dispensingState;
    private VendingMachineState currentState;

    public VendingMachine() {
        idleState = new IdleState();
        productSelectedState = new ProductSelectedState();
        dispensingState = new DispensingState();
        currentState = idleState;
    }

    public void setState(VendingMachineState state) {
        currentState = state;
    }

    public void selectProduct() {
        currentState.selectProduct(this);
    }

    public void insertMoney() {
        currentState.insertMoney(this);
    }

    public void dispense() {
        currentState.dispense(this);
    }

    public VendingMachineState getIdleState() { return idleState; }
    public VendingMachineState getProductSelectedState() { return productSelectedState; }
    public VendingMachineState getDispensingState() { return dispensingState; }
}
