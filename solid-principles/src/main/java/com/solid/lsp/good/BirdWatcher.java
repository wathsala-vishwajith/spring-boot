package com.solid.lsp.good;

import java.util.List;

/**
 * Now we can work with birds at the appropriate level of abstraction
 * No runtime exceptions or unexpected behavior
 */
public class BirdWatcher {

    public void makeBirdsEat(List<Bird> birds) {
        // This works for ALL birds - LSP satisfied
        for (Bird bird : birds) {
            bird.eat();
        }
    }

    public void makeFlyingBirdsFly(List<FlyingBird> flyingBirds) {
        // This only accepts birds that can fly
        for (FlyingBird bird : flyingBirds) {
            bird.fly();
        }
    }

    public void makeSwimmingBirdsSwim(List<SwimmingBird> swimmingBirds) {
        // This only accepts birds that can swim
        for (SwimmingBird bird : swimmingBirds) {
            bird.swim();
        }
    }
}
