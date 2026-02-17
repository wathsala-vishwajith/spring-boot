package behavioral.mediator;

import java.util.ArrayList;
import java.util.List;

/**
 * Demo class showing Mediator pattern in action
 */
public class MediatorDemo {
    public static void main(String[] args) {
        System.out.println("=== Mediator Pattern Demo ===\n");

        System.out.println("--- Chat Room Example ---");
        ChatMediator chatRoom = new ChatRoom();

        User john = new ChatUser(chatRoom, "John");
        User alice = new ChatUser(chatRoom, "Alice");
        User bob = new ChatUser(chatRoom, "Bob");

        chatRoom.addUser(john);
        chatRoom.addUser(alice);
        chatRoom.addUser(bob);

        john.send("Hello everyone!");
        alice.send("Hi John!");

        System.out.println("\n--- Air Traffic Control Example ---");
        AirTrafficControl atc = new AirportControl();

        Aircraft flight1 = new CommercialAircraft(atc, "Flight AA123");
        Aircraft flight2 = new CommercialAircraft(atc, "Flight BA456");

        atc.registerAircraft(flight1);
        atc.registerAircraft(flight2);

        flight1.requestLanding();
        flight2.requestLanding();
        flight1.land();
        flight2.land();
    }
}

// Mediator interface
interface ChatMediator {
    void sendMessage(String message, User user);
    void addUser(User user);
}

// Concrete Mediator
class ChatRoom implements ChatMediator {
    private List<User> users = new ArrayList<>();

    @Override
    public void addUser(User user) {
        users.add(user);
    }

    @Override
    public void sendMessage(String message, User sender) {
        for (User user : users) {
            if (user != sender) {
                user.receive(message);
            }
        }
    }
}

// Colleague
abstract class User {
    protected ChatMediator mediator;
    protected String name;

    public User(ChatMediator mediator, String name) {
        this.mediator = mediator;
        this.name = name;
    }

    public abstract void send(String message);
    public abstract void receive(String message);
}

// Concrete Colleague
class ChatUser extends User {
    public ChatUser(ChatMediator mediator, String name) {
        super(mediator, name);
    }

    @Override
    public void send(String message) {
        System.out.println(name + " sends: " + message);
        mediator.sendMessage(message, this);
    }

    @Override
    public void receive(String message) {
        System.out.println(name + " receives: " + message);
    }
}

// Air Traffic Control Example
interface AirTrafficControl {
    void registerAircraft(Aircraft aircraft);
    void requestLanding(Aircraft aircraft);
    void notifyLanding(Aircraft aircraft);
}

class AirportControl implements AirTrafficControl {
    private List<Aircraft> aircrafts = new ArrayList<>();
    private boolean runwayAvailable = true;

    @Override
    public void registerAircraft(Aircraft aircraft) {
        aircrafts.add(aircraft);
        System.out.println(aircraft.getName() + " registered with ATC");
    }

    @Override
    public void requestLanding(Aircraft aircraft) {
        if (runwayAvailable) {
            System.out.println("ATC: " + aircraft.getName() + " cleared for landing");
            runwayAvailable = false;
        } else {
            System.out.println("ATC: " + aircraft.getName() + " please hold, runway occupied");
        }
    }

    @Override
    public void notifyLanding(Aircraft aircraft) {
        System.out.println("ATC: " + aircraft.getName() + " has landed");
        runwayAvailable = true;

        // Notify waiting aircraft
        for (Aircraft a : aircrafts) {
            if (a != aircraft && !runwayAvailable) {
                System.out.println("ATC: Next aircraft cleared for landing");
                break;
            }
        }
    }
}

abstract class Aircraft {
    protected AirTrafficControl atc;
    protected String name;

    public Aircraft(AirTrafficControl atc, String name) {
        this.atc = atc;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void requestLanding();
    public abstract void land();
}

class CommercialAircraft extends Aircraft {
    public CommercialAircraft(AirTrafficControl atc, String name) {
        super(atc, name);
    }

    @Override
    public void requestLanding() {
        System.out.println(name + ": Requesting landing permission");
        atc.requestLanding(this);
    }

    @Override
    public void land() {
        System.out.println(name + ": Landing...");
        atc.notifyLanding(this);
    }
}
