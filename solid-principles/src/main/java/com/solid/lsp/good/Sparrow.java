package com.solid.lsp.good;

public class Sparrow extends FlyingBird {

    public Sparrow() {
        super("Sparrow");
    }

    @Override
    public void fly() {
        System.out.println(name + " is flying high in the sky");
    }

    @Override
    public void makeSound() {
        System.out.println(name + " chirps: Tweet tweet!");
    }
}
