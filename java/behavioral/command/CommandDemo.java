package behavioral.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Demo class showing Command pattern in action
 *
 * The Command pattern encapsulates a request as an object, thereby letting you
 * parameterize clients with different requests, queue or log requests, and support undoable operations.
 */
public class CommandDemo {
    public static void main(String[] args) {
        System.out.println("=== Command Pattern Demo ===\n");

        System.out.println("--- Text Editor Example (with Undo) ---");
        demonstrateTextEditor();

        System.out.println("\n--- Remote Control Example ---");
        demonstrateRemoteControl();

        System.out.println("\n--- Restaurant Order System ---");
        demonstrateRestaurantOrders();

        System.out.println("\n--- Smart Home Example ---");
        demonstrateSmartHome();
    }

    private static void demonstrateTextEditor() {
        TextEditor editor = new TextEditor();
        CommandHistory history = new CommandHistory();

        // Execute commands
        Command type1 = new TypeCommand(editor, "Hello ");
        Command type2 = new TypeCommand(editor, "World!");
        Command delete = new DeleteCommand(editor, 6);

        history.execute(type1);
        System.out.println("After typing 'Hello ': " + editor.getText());

        history.execute(type2);
        System.out.println("After typing 'World!': " + editor.getText());

        history.execute(delete);
        System.out.println("After deleting 6 characters: " + editor.getText());

        // Undo
        System.out.println("\nUndoing last command:");
        history.undo();
        System.out.println("After undo: " + editor.getText());

        history.undo();
        System.out.println("After another undo: " + editor.getText());
    }

    private static void demonstrateRemoteControl() {
        // Receivers
        Television tv = new Television();
        Stereo stereo = new Stereo();

        // Commands
        Command tvOn = new TurnOnCommand(tv);
        Command tvOff = new TurnOffCommand(tv);
        Command stereoOn = new TurnOnCommand(stereo);
        Command stereoOff = new TurnOffCommand(stereo);
        Command volumeUp = new VolumeUpCommand(stereo);

        // Invoker
        RemoteControl remote = new RemoteControl();
        remote.setCommand(tvOn);
        remote.pressButton();

        remote.setCommand(stereoOn);
        remote.pressButton();

        remote.setCommand(volumeUp);
        remote.pressButton();
        remote.pressButton();

        remote.setCommand(stereoOff);
        remote.pressButton();

        remote.setCommand(tvOff);
        remote.pressButton();
    }

    private static void demonstrateRestaurantOrders() {
        // Receiver
        Kitchen kitchen = new Kitchen();

        // Commands
        Order order1 = new Order(kitchen, "Burger", 1);
        Order order2 = new Order(kitchen, "Pizza", 2);
        Order order3 = new Order(kitchen, "Pasta", 1);

        // Invoker
        Waiter waiter = new Waiter();
        waiter.takeOrder(order1);
        waiter.takeOrder(order2);
        waiter.takeOrder(order3);

        System.out.println("Processing all orders:");
        waiter.placeOrders();
    }

    private static void demonstrateSmartHome() {
        // Receivers
        Light light = new Light();
        Thermostat thermostat = new Thermostat();
        SecuritySystem security = new SecuritySystem();

        // Commands
        Command lightsOn = new LightOnCommand(light);
        Command heatUp = new SetTemperatureCommand(thermostat, 72);
        Command armSecurity = new ArmSecurityCommand(security);

        // Macro command
        List<Command> eveningRoutine = new ArrayList<>();
        eveningRoutine.add(lightsOn);
        eveningRoutine.add(heatUp);
        eveningRoutine.add(armSecurity);
        Command eveningMacro = new MacroCommand(eveningRoutine);

        System.out.println("Executing evening routine:");
        eveningMacro.execute();
    }
}

// ============================================
// Command Interface
// ============================================

/**
 * Command interface
 */
interface Command {
    void execute();
    void undo();
}

// ============================================
// Text Editor Example
// ============================================

/**
 * Receiver - Text Editor
 */
class TextEditor {
    private StringBuilder text = new StringBuilder();

    public void type(String words) {
        text.append(words);
    }

    public void delete(int characters) {
        int length = text.length();
        int start = Math.max(0, length - characters);
        text.delete(start, length);
    }

    public String getText() {
        return text.toString();
    }
}

/**
 * Concrete Command - Type
 */
class TypeCommand implements Command {
    private TextEditor editor;
    private String text;

    public TypeCommand(TextEditor editor, String text) {
        this.editor = editor;
        this.text = text;
    }

    @Override
    public void execute() {
        editor.type(text);
    }

    @Override
    public void undo() {
        editor.delete(text.length());
    }
}

/**
 * Concrete Command - Delete
 */
class DeleteCommand implements Command {
    private TextEditor editor;
    private int characters;
    private String deletedText;

    public DeleteCommand(TextEditor editor, int characters) {
        this.editor = editor;
        this.characters = characters;
    }

    @Override
    public void execute() {
        String current = editor.getText();
        int length = current.length();
        int start = Math.max(0, length - characters);
        deletedText = current.substring(start);
        editor.delete(characters);
    }

    @Override
    public void undo() {
        editor.type(deletedText);
    }
}

/**
 * Invoker - Command History with Undo
 */
class CommandHistory {
    private Stack<Command> history = new Stack<>();

    public void execute(Command command) {
        command.execute();
        history.push(command);
    }

    public void undo() {
        if (!history.isEmpty()) {
            Command command = history.pop();
            command.undo();
        }
    }
}

// ============================================
// Remote Control Example
// ============================================

/**
 * Receiver - Television
 */
class Television {
    public void turnOn() {
        System.out.println("TV is ON");
    }

    public void turnOff() {
        System.out.println("TV is OFF");
    }
}

/**
 * Receiver - Stereo
 */
class Stereo {
    private int volume = 5;

    public void turnOn() {
        System.out.println("Stereo is ON");
    }

    public void turnOff() {
        System.out.println("Stereo is OFF");
    }

    public void volumeUp() {
        volume++;
        System.out.println("Stereo volume: " + volume);
    }

    public void volumeDown() {
        volume--;
        System.out.println("Stereo volume: " + volume);
    }
}

/**
 * Concrete Command - Turn On
 */
class TurnOnCommand implements Command {
    private Object device;

    public TurnOnCommand(Object device) {
        this.device = device;
    }

    @Override
    public void execute() {
        if (device instanceof Television) {
            ((Television) device).turnOn();
        } else if (device instanceof Stereo) {
            ((Stereo) device).turnOn();
        }
    }

    @Override
    public void undo() {
        if (device instanceof Television) {
            ((Television) device).turnOff();
        } else if (device instanceof Stereo) {
            ((Stereo) device).turnOff();
        }
    }
}

/**
 * Concrete Command - Turn Off
 */
class TurnOffCommand implements Command {
    private Object device;

    public TurnOffCommand(Object device) {
        this.device = device;
    }

    @Override
    public void execute() {
        if (device instanceof Television) {
            ((Television) device).turnOff();
        } else if (device instanceof Stereo) {
            ((Stereo) device).turnOff();
        }
    }

    @Override
    public void undo() {
        if (device instanceof Television) {
            ((Television) device).turnOn();
        } else if (device instanceof Stereo) {
            ((Stereo) device).turnOn();
        }
    }
}

/**
 * Concrete Command - Volume Up
 */
class VolumeUpCommand implements Command {
    private Stereo stereo;

    public VolumeUpCommand(Stereo stereo) {
        this.stereo = stereo;
    }

    @Override
    public void execute() {
        stereo.volumeUp();
    }

    @Override
    public void undo() {
        stereo.volumeDown();
    }
}

/**
 * Invoker - Remote Control
 */
class RemoteControl {
    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void pressButton() {
        command.execute();
    }
}

// ============================================
// Restaurant Example
// ============================================

/**
 * Receiver - Kitchen
 */
class Kitchen {
    public void prepareDish(String dish, int quantity) {
        System.out.println("Kitchen: Preparing " + quantity + "x " + dish);
    }
}

/**
 * Concrete Command - Order
 */
class Order implements Command {
    private Kitchen kitchen;
    private String dish;
    private int quantity;

    public Order(Kitchen kitchen, String dish, int quantity) {
        this.kitchen = kitchen;
        this.dish = dish;
        this.quantity = quantity;
    }

    @Override
    public void execute() {
        kitchen.prepareDish(dish, quantity);
    }

    @Override
    public void undo() {
        System.out.println("Cancelling order: " + quantity + "x " + dish);
    }
}

/**
 * Invoker - Waiter
 */
class Waiter {
    private List<Order> orders = new ArrayList<>();

    public void takeOrder(Order order) {
        orders.add(order);
    }

    public void placeOrders() {
        for (Order order : orders) {
            order.execute();
        }
        orders.clear();
    }
}

// ============================================
// Smart Home Example
// ============================================

/**
 * Receiver - Light
 */
class Light {
    public void on() {
        System.out.println("Lights turned ON");
    }

    public void off() {
        System.out.println("Lights turned OFF");
    }
}

/**
 * Receiver - Thermostat
 */
class Thermostat {
    private int temperature = 68;

    public void setTemperature(int temp) {
        this.temperature = temp;
        System.out.println("Temperature set to " + temp + "°F");
    }

    public int getTemperature() {
        return temperature;
    }
}

/**
 * Receiver - Security System
 */
class SecuritySystem {
    public void arm() {
        System.out.println("Security system ARMED");
    }

    public void disarm() {
        System.out.println("Security system DISARMED");
    }
}

/**
 * Concrete Command - Light On
 */
class LightOnCommand implements Command {
    private Light light;

    public LightOnCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.on();
    }

    @Override
    public void undo() {
        light.off();
    }
}

/**
 * Concrete Command - Set Temperature
 */
class SetTemperatureCommand implements Command {
    private Thermostat thermostat;
    private int temperature;
    private int previousTemperature;

    public SetTemperatureCommand(Thermostat thermostat, int temperature) {
        this.thermostat = thermostat;
        this.temperature = temperature;
    }

    @Override
    public void execute() {
        previousTemperature = thermostat.getTemperature();
        thermostat.setTemperature(temperature);
    }

    @Override
    public void undo() {
        thermostat.setTemperature(previousTemperature);
    }
}

/**
 * Concrete Command - Arm Security
 */
class ArmSecurityCommand implements Command {
    private SecuritySystem security;

    public ArmSecurityCommand(SecuritySystem security) {
        this.security = security;
    }

    @Override
    public void execute() {
        security.arm();
    }

    @Override
    public void undo() {
        security.disarm();
    }
}

/**
 * Macro Command - Executes multiple commands
 */
class MacroCommand implements Command {
    private List<Command> commands;

    public MacroCommand(List<Command> commands) {
        this.commands = commands;
    }

    @Override
    public void execute() {
        for (Command command : commands) {
            command.execute();
        }
    }

    @Override
    public void undo() {
        for (int i = commands.size() - 1; i >= 0; i--) {
            commands.get(i).undo();
        }
    }
}
