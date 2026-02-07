package com.solid.lsp.bad;

/**
 * Problem: Penguin violates LSP because it can't fly
 * It cannot be properly substituted for Bird in all contexts
 */
public class Penguin extends Bird {

    public Penguin() {
        super("Penguin");
    }

    @Override
    public void fly() {
        // This violates LSP - a penguin cannot fly!
        throw new UnsupportedOperationException("Penguins cannot fly!");
    }

    public void swim() {
        System.out.println(name + " is swimming gracefully");
    }
}
