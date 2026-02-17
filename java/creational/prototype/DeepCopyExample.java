package creational.prototype;

import java.util.ArrayList;
import java.util.List;

/**
 * Example demonstrating deep copy vs shallow copy
 */
public class DeepCopyExample {

    /**
     * Class demonstrating shallow copy issues
     */
    public static class ShallowCopyPerson implements Cloneable {
        private String name;
        private List<String> hobbies;

        public ShallowCopyPerson(String name) {
            this.name = name;
            this.hobbies = new ArrayList<>();
        }

        public void addHobby(String hobby) {
            hobbies.add(hobby);
        }

        @Override
        public ShallowCopyPerson clone() {
            try {
                return (ShallowCopyPerson) super.clone();  // Shallow copy!
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }

        @Override
        public String toString() {
            return name + "'s hobbies: " + hobbies;
        }
    }

    /**
     * Class demonstrating proper deep copy
     */
    public static class DeepCopyPerson implements Cloneable {
        private String name;
        private List<String> hobbies;

        public DeepCopyPerson(String name) {
            this.name = name;
            this.hobbies = new ArrayList<>();
        }

        public void addHobby(String hobby) {
            hobbies.add(hobby);
        }

        @Override
        public DeepCopyPerson clone() {
            try {
                DeepCopyPerson cloned = (DeepCopyPerson) super.clone();
                cloned.hobbies = new ArrayList<>(this.hobbies);  // Deep copy!
                return cloned;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }

        @Override
        public String toString() {
            return name + "'s hobbies: " + hobbies;
        }
    }

    public static void demonstrateShallowCopyProblem() {
        System.out.println("=== Shallow Copy Problem ===");
        ShallowCopyPerson person1 = new ShallowCopyPerson("Alice");
        person1.addHobby("Reading");
        person1.addHobby("Swimming");

        ShallowCopyPerson person2 = person1.clone();
        System.out.println("Original: " + person1);
        System.out.println("Clone: " + person2);

        // Modify clone's hobbies
        person2.addHobby("Coding");

        System.out.println("\nAfter modifying clone:");
        System.out.println("Original: " + person1);  // Original is also modified!
        System.out.println("Clone: " + person2);
    }

    public static void demonstrateDeepCopy() {
        System.out.println("\n=== Deep Copy Solution ===");
        DeepCopyPerson person1 = new DeepCopyPerson("Bob");
        person1.addHobby("Gaming");
        person1.addHobby("Cycling");

        DeepCopyPerson person2 = person1.clone();
        System.out.println("Original: " + person1);
        System.out.println("Clone: " + person2);

        // Modify clone's hobbies
        person2.addHobby("Photography");

        System.out.println("\nAfter modifying clone:");
        System.out.println("Original: " + person1);  // Original unchanged
        System.out.println("Clone: " + person2);
    }
}
