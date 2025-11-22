package com.solid.lsp.good;

/**
 * Penguin extends SwimmingBird, not FlyingBird
 * This satisfies LSP - it can be substituted for Bird or SwimmingBird
 */
public class Penguin extends SwimmingBird {

    public Penguin() {
        super("Penguin");
    }

    @Override
    public void swim() {
        System.out.println(name + " is swimming gracefully");
    }

    @Override
    public void makeSound() {
        System.out.println(name + " calls: Honk honk!");
    }
}
