package com.solid.lsp;

import com.solid.lsp.good.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Liskov Substitution Principle
 */
class LSPTest {

    @Test
    void testAllBirdsCanEat() {
        // All birds can eat - this is a common behavior
        Bird sparrow = new Sparrow();
        Bird eagle = new Eagle();
        Bird penguin = new Penguin();

        assertDoesNotThrow(sparrow::eat);
        assertDoesNotThrow(eagle::eat);
        assertDoesNotThrow(penguin::eat);
    }

    @Test
    void testAllBirdsCanMakeSound() {
        // All birds can make sound - this is a common behavior
        Bird sparrow = new Sparrow();
        Bird eagle = new Eagle();
        Bird penguin = new Penguin();

        assertDoesNotThrow(sparrow::makeSound);
        assertDoesNotThrow(eagle::makeSound);
        assertDoesNotThrow(penguin::makeSound);
    }

    @Test
    void testFlyingBirdsCanFly() {
        // Only flying birds have fly capability
        FlyingBird sparrow = new Sparrow();
        FlyingBird eagle = new Eagle();

        assertDoesNotThrow(sparrow::fly);
        assertDoesNotThrow(eagle::fly);
        assertEquals("Sparrow", sparrow.getName());
        assertEquals("Eagle", eagle.getName());
    }

    @Test
    void testSwimmingBirdsCanSwim() {
        // Only swimming birds have swim capability
        SwimmingBird penguin = new Penguin();

        assertDoesNotThrow(penguin::swim);
        assertEquals("Penguin", penguin.getName());
    }

    @Test
    void testBirdWatcherWithAllBirds() {
        // BirdWatcher can work with all birds at the Bird level
        List<Bird> allBirds = Arrays.asList(
            new Sparrow(),
            new Eagle(),
            new Penguin()
        );

        BirdWatcher watcher = new BirdWatcher();
        assertDoesNotThrow(() -> watcher.makeBirdsEat(allBirds));
    }

    @Test
    void testBirdWatcherWithFlyingBirds() {
        // BirdWatcher can work with only flying birds
        List<FlyingBird> flyingBirds = Arrays.asList(
            new Sparrow(),
            new Eagle()
        );

        BirdWatcher watcher = new BirdWatcher();
        assertDoesNotThrow(() -> watcher.makeFlyingBirdsFly(flyingBirds));
        // Note: Penguin cannot be added to this list - compile-time safety!
    }

    @Test
    void testBirdWatcherWithSwimmingBirds() {
        // BirdWatcher can work with only swimming birds
        List<SwimmingBird> swimmingBirds = Arrays.asList(
            new Penguin()
        );

        BirdWatcher watcher = new BirdWatcher();
        assertDoesNotThrow(() -> watcher.makeSwimmingBirdsSwim(swimmingBirds));
    }

    @Test
    void testSubstitutability() {
        // Sparrow can substitute for Bird or FlyingBird
        Bird bird1 = new Sparrow();
        FlyingBird bird2 = new Sparrow();

        assertNotNull(bird1);
        assertNotNull(bird2);

        // Penguin can substitute for Bird or SwimmingBird
        Bird bird3 = new Penguin();
        SwimmingBird bird4 = new Penguin();

        assertNotNull(bird3);
        assertNotNull(bird4);

        // This satisfies LSP - derived classes can be used wherever base class is expected
    }

    @Test
    void testBadExampleViolation() {
        // The bad example would throw exception
        com.solid.lsp.bad.Penguin badPenguin = new com.solid.lsp.bad.Penguin();

        // This throws UnsupportedOperationException - violates LSP
        assertThrows(UnsupportedOperationException.class, badPenguin::fly);
    }
}
