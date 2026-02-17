package behavioral.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Demo class showing Observer pattern in action
 */
public class ObserverDemo {
    public static void main(String[] args) {
        System.out.println("=== Observer Pattern Demo ===\n");

        System.out.println("--- Weather Station Example ---");
        WeatherStation station = new WeatherStation();

        WeatherDisplay phoneDisplay = new PhoneDisplay();
        WeatherDisplay windowDisplay = new WindowDisplay();
        WeatherDisplay tvDisplay = new TVDisplay();

        station.addObserver(phoneDisplay);
        station.addObserver(windowDisplay);
        station.addObserver(tvDisplay);

        station.setTemperature(25.5f);
        System.out.println();
        station.setTemperature(30.0f);

        System.out.println("\n--- Stock Market Example ---");
        Stock apple = new Stock("AAPL", 150.0);

        Investor investor1 = new Investor("John");
        Investor investor2 = new Investor("Alice");

        apple.attach(investor1);
        apple.attach(investor2);

        apple.setPrice(155.0);
        apple.setPrice(145.0);
    }
}

// Observer interface
interface WeatherObserver {
    void update(float temperature);
}

// Subject
class WeatherStation {
    private List<WeatherObserver> observers = new ArrayList<>();
    private float temperature;

    public void addObserver(WeatherObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(WeatherObserver observer) {
        observers.remove(observer);
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
        notifyObservers();
    }

    private void notifyObservers() {
        System.out.println("Weather Station: Temperature changed to " + temperature + "°C");
        for (WeatherObserver observer : observers) {
            observer.update(temperature);
        }
    }
}

// Concrete Observers
interface WeatherDisplay extends WeatherObserver {
}

class PhoneDisplay implements WeatherDisplay {
    @Override
    public void update(float temperature) {
        System.out.println("Phone Display: " + temperature + "°C");
    }
}

class WindowDisplay implements WeatherDisplay {
    @Override
    public void update(float temperature) {
        System.out.println("Window Display: " + temperature + "°C");
    }
}

class TVDisplay implements WeatherDisplay {
    @Override
    public void update(float temperature) {
        System.out.println("TV Display: " + temperature + "°C");
    }
}

// Stock Market Example
interface StockObserver {
    void update(String symbol, double price);
}

class Stock {
    private List<StockObserver> observers = new ArrayList<>();
    private String symbol;
    private double price;

    public Stock(String symbol, double price) {
        this.symbol = symbol;
        this.price = price;
    }

    public void attach(StockObserver observer) {
        observers.add(observer);
    }

    public void detach(StockObserver observer) {
        observers.remove(observer);
    }

    public void setPrice(double price) {
        this.price = price;
        notifyObservers();
    }

    private void notifyObservers() {
        System.out.println("\n" + symbol + " price changed to $" + price);
        for (StockObserver observer : observers) {
            observer.update(symbol, price);
        }
    }
}

class Investor implements StockObserver {
    private String name;

    public Investor(String name) {
        this.name = name;
    }

    @Override
    public void update(String symbol, double price) {
        System.out.println(name + " notified: " + symbol + " is now $" + price);
    }
}
