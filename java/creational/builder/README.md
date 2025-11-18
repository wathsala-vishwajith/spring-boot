# Builder Pattern

## Overview
The Builder pattern is a creational design pattern that lets you construct complex objects step by step. It allows you to produce different types and representations of an object using the same construction code.

## When to Use

### Use Builder when:

1. **Creating objects with many parameters (especially optional ones)**
   - Avoids "telescoping constructor" anti-pattern
   - Makes code more readable than constructors with many parameters
   - Example: Object with 10+ fields where most are optional

2. **Construction process must allow different representations**
   - Same building steps can create different product representations
   - Example: Building houses with different styles using same steps

3. **You want to construct composite objects or complex hierarchies**
   - Step-by-step construction of complex structures
   - Example: Building HTML/XML documents, SQL queries

4. **You need immutable objects with many fields**
   - Build object completely before use
   - No setters needed, ensuring immutability

### Real-world scenarios:

- **Document builders**: HTML, XML, JSON document construction
- **Query builders**: SQL, MongoDB, Elasticsearch queries
- **Configuration objects**: Application settings, API clients
- **Test data builders**: Creating test fixtures with various configurations
- **UI component builders**: Complex UI layouts
- **Request/Response objects**: HTTP requests, API responses
- **DTO (Data Transfer Objects)**: Objects with many fields

## Implementation Details

### Key Components:

1. **Product**: The complex object being constructed
2. **Builder**: Interface/abstract class defining construction steps
3. **Concrete Builder**: Implements building steps and maintains product instance
4. **Director** (optional): Defines order of building steps for creating specific configurations
5. **Client**: Uses builder to construct objects

### Code Example:
```java
// Product
public class House {
    private String foundation;
    private String structure;
    private String roof;
    // ... more fields

    private House() {}  // Private constructor

    public static class Builder {
        private House house = new House();

        public Builder foundation(String foundation) {
            house.foundation = foundation;
            return this;
        }

        public Builder structure(String structure) {
            house.structure = structure;
            return this;
        }

        public House build() {
            // Validation
            return house;
        }
    }
}

// Usage
House house = new House.Builder()
    .foundation("Concrete")
    .structure("Wood")
    .roof("Tile")
    .build();
```

### Common Variations:

1. **Fluent Interface Builder** (most common in Java)
```java
public Builder setField(String value) {
    this.field = value;
    return this;  // Return this for chaining
}
```

2. **Separate Builder Class**
```java
public class ProductBuilder {
    public Product build() { return new Product(this); }
}
```

3. **With Director**
```java
public class Director {
    public Product constructStandardProduct(Builder builder) {
        return builder.partA().partB().build();
    }
}
```

## Caveats and Considerations

### Disadvantages:

1. **Increased Complexity**
   - More code than simple constructors
   - Additional classes to maintain
   - May be overkill for simple objects

2. **Mutable Builder State**
   - Builders are typically mutable
   - Not thread-safe by default
   - Same builder instance shouldn't be reused without reset

3. **Incomplete Objects**
   - Without proper validation, might create invalid objects
   - Need to ensure all required fields are set in `build()` method

4. **Performance Overhead**
   - Creates additional objects (builder instances)
   - Slight performance cost compared to direct construction
   - Usually negligible but consider for high-performance scenarios

### Common Pitfalls:

1. **Forgetting to Call build()**
   - Working with builder instead of actual product
   ```java
   House.Builder builder = new House.Builder().foundation("Concrete");
   // Forgot to call .build()! builder is not a House
   ```

2. **Reusing Builder Instances**
   - Builders usually shouldn't be reused
   ```java
   Builder builder = new Builder();
   Product p1 = builder.setX(1).build();
   Product p2 = builder.setY(2).build();  // p2 also has X=1!
   ```

3. **Missing Validation**
   - Always validate in build() method
   ```java
   public Product build() {
       if (requiredField == null) {
           throw new IllegalStateException("Required field missing");
       }
       return product;
   }
   ```

4. **Breaking Immutability**
   - If product should be immutable, don't expose setters
   - Use private constructor and final fields

5. **Too Many Required Parameters**
   - If most fields are required, constructor might be better
   - Builder shines with many optional parameters

### Best Practices:

1. **Use for Complex Objects**
   - Objects with 4+ fields
   - Objects with many optional parameters
   - Objects that need validation

2. **Make Product Immutable**
   ```java
   public class Product {
       private final String field;  // final fields

       private Product(Builder builder) {  // private constructor
           this.field = builder.field;
       }
   }
   ```

3. **Validate in build()**
   ```java
   public Product build() {
       validateRequiredFields();
       validateBusinessRules();
       return new Product(this);
   }
   ```

4. **Use Descriptive Method Names**
   ```java
   // Good
   builder.withGarage(true).withGarden(true)

   // Better
   builder.addGarage().addGarden()
   ```

5. **Consider Static Factory Method**
   ```java
   public static Builder builder() {
       return new Builder();
   }

   // Usage
   Product.builder().field1(x).build();
   ```

6. **Document Required Fields**
   ```java
   /**
    * @throws IllegalStateException if required fields not set
    */
   public Product build() { ... }
   ```

7. **Thread Safety**
   - Builders are not thread-safe by default
   - Don't share builder instances across threads
   - Each thread should have its own builder

8. **Lombok Alternative**
   - For simple cases, consider using Lombok `@Builder` annotation
   ```java
   @Builder
   public class Product {
       private String field1;
       private String field2;
   }
   ```

## Comparison with Other Patterns

| Pattern | Key Difference |
|---------|---------------|
| **Abstract Factory** | Creates families of objects vs. step-by-step single object |
| **Factory Method** | One-step creation vs. multi-step construction |
| **Prototype** | Clones existing objects vs. builds new ones |
| **Composite** | Often built using Builder for complex hierarchies |
| **Fluent Interface** | Builder implements fluent interface pattern |

## Relationship with Other Patterns

- **Can be implemented as Singleton** (Director)
- **Often combined with Composite** to build complex trees
- **Can use Abstract Factory** to specify which builder to use
- **Prototype can be alternative** to Builder for creating complex objects

## Real-world Examples in Java

1. **StringBuilder/StringBuffer**
   ```java
   String result = new StringBuilder()
       .append("Hello")
       .append(" ")
       .append("World")
       .toString();
   ```

2. **Stream API**
   ```java
   List<String> result = stream
       .filter(x -> x > 0)
       .map(String::valueOf)
       .collect(Collectors.toList());
   ```

3. **Calendar**
   ```java
   Calendar cal = new Calendar.Builder()
       .setDate(2024, 1, 1)
       .setTimeOfDay(12, 0, 0)
       .build();
   ```

4. **MockMvc** (Spring Test)
   ```java
   mockMvc.perform(get("/api/users")
       .param("page", "1")
       .accept(MediaType.APPLICATION_JSON))
       .andExpect(status().isOk());
   ```

## Running the Example

```bash
cd java/creational/builder
javac BuilderDemo.java
java creational.builder.BuilderDemo
```

## Expected Output

The demo will show:
1. Custom house construction with specific parameters
2. Predefined house types using Director
3. Car construction with different configurations
4. SQL query builder for dynamic query generation
