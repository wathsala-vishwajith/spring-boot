package creational.builder;

/**
 * Director class - constructs objects using the builder interface
 * Useful for creating predefined configurations
 */
public class Director {

    /**
     * Constructs a luxury house
     */
    public House constructLuxuryHouse(House.Builder builder) {
        return builder
            .foundation("Reinforced concrete foundation")
            .structure("Steel and concrete structure")
            .roof("Tile roof with solar panels")
            .rooms(6)
            .interiorDesign("Modern luxury")
            .withGarage(true)
            .withSwimmingPool(true)
            .withGarden(true)
            .build();
    }

    /**
     * Constructs a simple house
     */
    public House constructSimpleHouse(House.Builder builder) {
        return builder
            .foundation("Concrete slab foundation")
            .structure("Wood frame structure")
            .roof("Asphalt shingle roof")
            .rooms(3)
            .interiorDesign("Minimalist")
            .withGarage(false)
            .withSwimmingPool(false)
            .withGarden(true)
            .build();
    }

    /**
     * Constructs a sports car
     */
    public Car constructSportsCar(Car.CarBuilder builder) {
        return builder
            .transmission("Automatic")
            .gps(true)
            .sunroof(true)
            .color("Red")
            .build();
    }
}
