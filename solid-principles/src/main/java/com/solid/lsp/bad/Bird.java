package com.solid.lsp.bad;

/**
 * BAD EXAMPLE: Violates Liskov Substitution Principle
 * Base class assumes all birds can fly
 */
public abstract class Bird {
    protected String name;

    public Bird(String name) {
        this.name = name;
    }

    public abstract void fly();

    public void eat() {
        System.out.println(name + " is eating");
    }

    public String getName() {
        return name;
    }
}
