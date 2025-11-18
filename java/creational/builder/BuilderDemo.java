package creational.builder;

/**
 * Demo class showing Builder pattern in action
 */
public class BuilderDemo {
    public static void main(String[] args) {
        System.out.println("=== Builder Pattern Demo ===\n");

        // Example 1: Building a house step by step
        System.out.println("--- Building Custom House ---");
        House customHouse = new House.Builder()
            .foundation("Deep pile foundation")
            .structure("Brick and mortar")
            .roof("Metal roof")
            .rooms(4)
            .interiorDesign("Contemporary")
            .withGarage(true)
            .withSwimmingPool(false)
            .withGarden(true)
            .build();
        System.out.println(customHouse);

        System.out.println("\n--- Using Director for Predefined Houses ---");

        // Example 2: Using Director for luxury house
        Director director = new Director();
        House luxuryHouse = director.constructLuxuryHouse(new House.Builder());
        System.out.println("Luxury House:");
        System.out.println(luxuryHouse);

        System.out.println();

        // Example 3: Using Director for simple house
        House simpleHouse = director.constructSimpleHouse(new House.Builder());
        System.out.println("Simple House:");
        System.out.println(simpleHouse);

        System.out.println("\n--- Building Cars ---");

        // Example 4: Building a sports car
        Car sportsCar = new Car.CarBuilder("V8 Turbo", 2)
            .transmission("Automatic")
            .gps(true)
            .sunroof(true)
            .color("Red")
            .build();
        System.out.println(sportsCar);

        // Example 5: Building a family car with minimal options
        Car familyCar = new Car.CarBuilder("V6", 7)
            .color("Blue")
            .gps(true)
            .build();
        System.out.println(familyCar);

        // Example 6: Building a budget car with defaults
        Car budgetCar = new Car.CarBuilder("4-cylinder", 5)
            .build();
        System.out.println(budgetCar);

        System.out.println("\n--- Real-world Example: SQL Query Builder ---");
        demonstrateSQLBuilder();
    }

    private static void demonstrateSQLBuilder() {
        SQLQuery query1 = new SQLQuery.Builder("users")
            .select("id", "name", "email")
            .where("age > 18")
            .orderBy("name")
            .limit(10)
            .build();
        System.out.println(query1.toSQL());

        System.out.println();

        SQLQuery query2 = new SQLQuery.Builder("products")
            .select("*")
            .where("price < 100")
            .where("category = 'Electronics'")
            .orderBy("price", "DESC")
            .build();
        System.out.println(query2.toSQL());
    }
}

/**
 * Real-world example: SQL Query Builder
 */
class SQLQuery {
    private final String table;
    private final String[] columns;
    private final String[] whereConditions;
    private final String orderByColumn;
    private final String orderDirection;
    private final int limit;

    private SQLQuery(Builder builder) {
        this.table = builder.table;
        this.columns = builder.columns;
        this.whereConditions = builder.whereConditions.toArray(new String[0]);
        this.orderByColumn = builder.orderByColumn;
        this.orderDirection = builder.orderDirection;
        this.limit = builder.limit;
    }

    public String toSQL() {
        StringBuilder sql = new StringBuilder("SELECT ");

        // Columns
        if (columns.length == 0 || (columns.length == 1 && columns[0].equals("*"))) {
            sql.append("*");
        } else {
            sql.append(String.join(", ", columns));
        }

        sql.append(" FROM ").append(table);

        // WHERE clause
        if (whereConditions.length > 0) {
            sql.append(" WHERE ").append(String.join(" AND ", whereConditions));
        }

        // ORDER BY clause
        if (orderByColumn != null) {
            sql.append(" ORDER BY ").append(orderByColumn);
            if (orderDirection != null) {
                sql.append(" ").append(orderDirection);
            }
        }

        // LIMIT clause
        if (limit > 0) {
            sql.append(" LIMIT ").append(limit);
        }

        return sql.toString();
    }

    public static class Builder {
        private final String table;
        private String[] columns = new String[]{"*"};
        private java.util.List<String> whereConditions = new java.util.ArrayList<>();
        private String orderByColumn;
        private String orderDirection;
        private int limit;

        public Builder(String table) {
            this.table = table;
        }

        public Builder select(String... columns) {
            this.columns = columns;
            return this;
        }

        public Builder where(String condition) {
            this.whereConditions.add(condition);
            return this;
        }

        public Builder orderBy(String column) {
            this.orderByColumn = column;
            return this;
        }

        public Builder orderBy(String column, String direction) {
            this.orderByColumn = column;
            this.orderDirection = direction;
            return this;
        }

        public Builder limit(int limit) {
            this.limit = limit;
            return this;
        }

        public SQLQuery build() {
            return new SQLQuery(this);
        }
    }
}
