package creational.builder;

/**
 * Another product example - Car with fluent interface builder
 */
public class Car {
    private final String engine;
    private final int seats;
    private final String transmission;
    private final boolean hasGPS;
    private final boolean hasSunroof;
    private final String color;

    private Car(CarBuilder builder) {
        this.engine = builder.engine;
        this.seats = builder.seats;
        this.transmission = builder.transmission;
        this.hasGPS = builder.hasGPS;
        this.hasSunroof = builder.hasSunroof;
        this.color = builder.color;
    }

    @Override
    public String toString() {
        return String.format(
            "Car: %s engine, %d seats, %s transmission, Color: %s, GPS: %s, Sunroof: %s",
            engine, seats, transmission, color,
            hasGPS ? "Yes" : "No",
            hasSunroof ? "Yes" : "No"
        );
    }

    public static class CarBuilder {
        // Required parameters
        private final String engine;
        private final int seats;

        // Optional parameters with default values
        private String transmission = "Manual";
        private boolean hasGPS = false;
        private boolean hasSunroof = false;
        private String color = "White";

        public CarBuilder(String engine, int seats) {
            this.engine = engine;
            this.seats = seats;
        }

        public CarBuilder transmission(String transmission) {
            this.transmission = transmission;
            return this;
        }

        public CarBuilder gps(boolean hasGPS) {
            this.hasGPS = hasGPS;
            return this;
        }

        public CarBuilder sunroof(boolean hasSunroof) {
            this.hasSunroof = hasSunroof;
            return this;
        }

        public CarBuilder color(String color) {
            this.color = color;
            return this;
        }

        public Car build() {
            return new Car(this);
        }
    }
}
