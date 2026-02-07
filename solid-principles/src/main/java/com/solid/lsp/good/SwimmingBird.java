package com.solid.lsp.good;

/**
 * Separate abstraction for birds that can swim
 */
public abstract class SwimmingBird extends Bird {

    public SwimmingBird(String name) {
        super(name);
    }

    public abstract void swim();
}
