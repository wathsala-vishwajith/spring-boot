package com.solid.lsp.bad;

public class Sparrow extends Bird {

    public Sparrow() {
        super("Sparrow");
    }

    @Override
    public void fly() {
        System.out.println(name + " is flying high in the sky");
    }
}
