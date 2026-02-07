package com.solid.lsp.good;

public class Eagle extends FlyingBird {

    public Eagle() {
        super("Eagle");
    }

    @Override
    public void fly() {
        System.out.println(name + " is soaring majestically");
    }

    @Override
    public void makeSound() {
        System.out.println(name + " screams: Screech!");
    }
}
