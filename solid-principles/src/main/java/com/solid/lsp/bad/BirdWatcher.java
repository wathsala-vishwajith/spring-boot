package com.solid.lsp.bad;

import java.util.List;

/**
 * This class expects all birds to be able to fly
 * It will crash when it encounters a Penguin
 */
public class BirdWatcher {

    public void makeBirdsFly(List<Bird> birds) {
        for (Bird bird : birds) {
            // This will throw exception for Penguin!
            bird.fly();
        }
    }
}
