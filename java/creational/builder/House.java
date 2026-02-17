package creational.builder;

/**
 * Product class - represents the complex object being built
 */
public class House {
    private String foundation;
    private String structure;
    private String roof;
    private boolean hasGarage;
    private boolean hasSwimmingPool;
    private boolean hasGarden;
    private int numberOfRooms;
    private String interiorDesign;

    // Private constructor - only builder can create instances
    private House() {}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("House Configuration:\n");
        sb.append("  Foundation: ").append(foundation).append("\n");
        sb.append("  Structure: ").append(structure).append("\n");
        sb.append("  Roof: ").append(roof).append("\n");
        sb.append("  Rooms: ").append(numberOfRooms).append("\n");
        sb.append("  Interior: ").append(interiorDesign).append("\n");
        sb.append("  Garage: ").append(hasGarage ? "Yes" : "No").append("\n");
        sb.append("  Swimming Pool: ").append(hasSwimmingPool ? "Yes" : "No").append("\n");
        sb.append("  Garden: ").append(hasGarden ? "Yes" : "No");
        return sb.toString();
    }

    /**
     * Static Builder class
     */
    public static class Builder {
        private House house;

        public Builder() {
            house = new House();
        }

        public Builder foundation(String foundation) {
            house.foundation = foundation;
            return this;
        }

        public Builder structure(String structure) {
            house.structure = structure;
            return this;
        }

        public Builder roof(String roof) {
            house.roof = roof;
            return this;
        }

        public Builder rooms(int numberOfRooms) {
            house.numberOfRooms = numberOfRooms;
            return this;
        }

        public Builder interiorDesign(String interiorDesign) {
            house.interiorDesign = interiorDesign;
            return this;
        }

        public Builder withGarage(boolean hasGarage) {
            house.hasGarage = hasGarage;
            return this;
        }

        public Builder withSwimmingPool(boolean hasSwimmingPool) {
            house.hasSwimmingPool = hasSwimmingPool;
            return this;
        }

        public Builder withGarden(boolean hasGarden) {
            house.hasGarden = hasGarden;
            return this;
        }

        /**
         * Build method - returns the constructed object
         */
        public House build() {
            // Validation can be performed here
            if (house.foundation == null || house.structure == null) {
                throw new IllegalStateException("Foundation and structure are required!");
            }
            return house;
        }
    }
}
