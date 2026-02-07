package com.solid.lsp.good;

/**
 * Separate abstraction for birds that can fly
 * Only flying birds extend this class
 */
public abstract class FlyingBird extends Bird {

    public FlyingBird(String name) {
        super(name);
    }

    public abstract void fly();
}
