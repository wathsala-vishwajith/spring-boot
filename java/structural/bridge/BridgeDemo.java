package structural.bridge;

/**
 * Demo class showing Bridge pattern in action
 *
 * The Bridge pattern demonstrates separation of abstraction and implementation,
 * allowing both to vary independently.
 */
public class BridgeDemo {
    public static void main(String[] args) {
        System.out.println("=== Bridge Pattern Demo ===\n");

        // Creating shapes with different colors (implementation)
        System.out.println("--- Shapes with Different Colors ---");
        Shape redCircle = new Circle(100, 100, 10, new RedColor());
        Shape greenCircle = new Circle(200, 200, 20, new GreenColor());
        Shape blueSquare = new Square(150, 150, 30, new BlueColor());

        redCircle.draw();
        greenCircle.draw();
        blueSquare.draw();

        System.out.println("\n--- Real-world Example: Remote Controls and Devices ---");
        demonstrateDeviceControl();

        System.out.println("\n--- Real-world Example: Message Sending ---");
        demonstrateMessageSending();
    }

    private static void demonstrateDeviceControl() {
        // Different remote controls (abstractions) for different devices (implementations)
        Device tv = new Television();
        Device radio = new Radio();

        RemoteControl basicRemote = new BasicRemote(tv);
        RemoteControl advancedRemote = new AdvancedRemote(radio);

        System.out.println("Testing Basic Remote with TV:");
        basicRemote.togglePower();
        basicRemote.volumeUp();
        basicRemote.volumeUp();
        basicRemote.channelUp();

        System.out.println("\nTesting Advanced Remote with Radio:");
        advancedRemote.togglePower();
        advancedRemote.volumeUp();
        ((AdvancedRemote) advancedRemote).mute();
    }

    private static void demonstrateMessageSending() {
        // Different message types (abstractions) with different platforms (implementations)
        MessageSender emailNotification = new NotificationMessage(new EmailSender());
        MessageSender smsAlert = new AlertMessage(new SMSSender());
        MessageSender pushNotification = new NotificationMessage(new PushNotificationSender());

        emailNotification.send("Welcome to our service!");
        smsAlert.send("Your code is 12345");
        pushNotification.send("New message received");
    }
}

// ============================================
// Shape Example - Classic Bridge Pattern
// ============================================

/**
 * Implementation interface - defines the interface for concrete implementations
 */
interface Color {
    void applyColor();
}

/**
 * Concrete implementation - Red color
 */
class RedColor implements Color {
    @Override
    public void applyColor() {
        System.out.println("Applying red color");
    }
}

/**
 * Concrete implementation - Green color
 */
class GreenColor implements Color {
    @Override
    public void applyColor() {
        System.out.println("Applying green color");
    }
}

/**
 * Concrete implementation - Blue color
 */
class BlueColor implements Color {
    @Override
    public void applyColor() {
        System.out.println("Applying blue color");
    }
}

/**
 * Abstraction - Shape
 * Contains a reference to the implementation (Color)
 */
abstract class Shape {
    protected Color color;

    protected Shape(Color color) {
        this.color = color;
    }

    abstract void draw();
}

/**
 * Refined abstraction - Circle
 */
class Circle extends Shape {
    private int x, y, radius;

    public Circle(int x, int y, int radius, Color color) {
        super(color);
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    @Override
    public void draw() {
        System.out.print("Drawing Circle at (" + x + ", " + y + ") with radius " + radius + " - ");
        color.applyColor();
    }
}

/**
 * Refined abstraction - Square
 */
class Square extends Shape {
    private int x, y, side;

    public Square(int x, int y, int side, Color color) {
        super(color);
        this.x = x;
        this.y = y;
        this.side = side;
    }

    @Override
    public void draw() {
        System.out.print("Drawing Square at (" + x + ", " + y + ") with side " + side + " - ");
        color.applyColor();
    }
}

// ============================================
// Device Control Example - Real-world scenario
// ============================================

/**
 * Implementation interface - Device
 */
interface Device {
    boolean isEnabled();
    void enable();
    void disable();
    int getVolume();
    void setVolume(int percent);
    int getChannel();
    void setChannel(int channel);
}

/**
 * Concrete implementation - Television
 */
class Television implements Device {
    private boolean on = false;
    private int volume = 30;
    private int channel = 1;

    @Override
    public boolean isEnabled() {
        return on;
    }

    @Override
    public void enable() {
        on = true;
        System.out.println("TV is now ON");
    }

    @Override
    public void disable() {
        on = false;
        System.out.println("TV is now OFF");
    }

    @Override
    public int getVolume() {
        return volume;
    }

    @Override
    public void setVolume(int percent) {
        this.volume = Math.max(0, Math.min(100, percent));
        System.out.println("TV volume set to " + this.volume);
    }

    @Override
    public int getChannel() {
        return channel;
    }

    @Override
    public void setChannel(int channel) {
        this.channel = channel;
        System.out.println("TV channel set to " + channel);
    }
}

/**
 * Concrete implementation - Radio
 */
class Radio implements Device {
    private boolean on = false;
    private int volume = 50;
    private int channel = 1;

    @Override
    public boolean isEnabled() {
        return on;
    }

    @Override
    public void enable() {
        on = true;
        System.out.println("Radio is now ON");
    }

    @Override
    public void disable() {
        on = false;
        System.out.println("Radio is now OFF");
    }

    @Override
    public int getVolume() {
        return volume;
    }

    @Override
    public void setVolume(int percent) {
        this.volume = Math.max(0, Math.min(100, percent));
        System.out.println("Radio volume set to " + this.volume);
    }

    @Override
    public int getChannel() {
        return channel;
    }

    @Override
    public void setChannel(int channel) {
        this.channel = channel;
        System.out.println("Radio frequency set to " + channel);
    }
}

/**
 * Abstraction - Remote Control
 */
class RemoteControl {
    protected Device device;

    public RemoteControl(Device device) {
        this.device = device;
    }

    public void togglePower() {
        if (device.isEnabled()) {
            device.disable();
        } else {
            device.enable();
        }
    }

    public void volumeDown() {
        device.setVolume(device.getVolume() - 10);
    }

    public void volumeUp() {
        device.setVolume(device.getVolume() + 10);
    }

    public void channelDown() {
        device.setChannel(device.getChannel() - 1);
    }

    public void channelUp() {
        device.setChannel(device.getChannel() + 1);
    }
}

/**
 * Refined abstraction - Basic Remote
 */
class BasicRemote extends RemoteControl {
    public BasicRemote(Device device) {
        super(device);
    }
}

/**
 * Refined abstraction - Advanced Remote with additional features
 */
class AdvancedRemote extends RemoteControl {
    public AdvancedRemote(Device device) {
        super(device);
    }

    public void mute() {
        System.out.println("Muting device");
        device.setVolume(0);
    }
}

// ============================================
// Message Sending Example - Another real-world scenario
// ============================================

/**
 * Implementation interface - Message Platform
 */
interface MessagePlatform {
    void sendMessage(String message);
}

/**
 * Concrete implementation - Email
 */
class EmailSender implements MessagePlatform {
    @Override
    public void sendMessage(String message) {
        System.out.println("Email sent: " + message);
    }
}

/**
 * Concrete implementation - SMS
 */
class SMSSender implements MessagePlatform {
    @Override
    public void sendMessage(String message) {
        System.out.println("SMS sent: " + message);
    }
}

/**
 * Concrete implementation - Push Notification
 */
class PushNotificationSender implements MessagePlatform {
    @Override
    public void sendMessage(String message) {
        System.out.println("Push notification sent: " + message);
    }
}

/**
 * Abstraction - Message Sender
 */
abstract class MessageSender {
    protected MessagePlatform platform;

    protected MessageSender(MessagePlatform platform) {
        this.platform = platform;
    }

    abstract void send(String message);
}

/**
 * Refined abstraction - Notification Message
 */
class NotificationMessage extends MessageSender {
    public NotificationMessage(MessagePlatform platform) {
        super(platform);
    }

    @Override
    public void send(String message) {
        System.out.print("[NOTIFICATION] ");
        platform.sendMessage(message);
    }
}

/**
 * Refined abstraction - Alert Message
 */
class AlertMessage extends MessageSender {
    public AlertMessage(MessagePlatform platform) {
        super(platform);
    }

    @Override
    public void send(String message) {
        System.out.print("[ALERT] ");
        platform.sendMessage(message.toUpperCase());
    }
}
