package com.solid.lsp.good;

/**
 * GOOD EXAMPLE: Follows Liskov Substitution Principle
 * Base class only contains behavior common to ALL birds
 */
public abstract class Bird {
    protected String name;

    public Bird(String name) {
        this.name = name;
    }

    public void eat() {
        System.out.println(name + " is eating");
    }

    public abstract void makeSound();

    public String getName() {
        return name;
    }
}
