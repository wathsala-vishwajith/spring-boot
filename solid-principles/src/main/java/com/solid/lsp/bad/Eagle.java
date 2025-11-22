package com.solid.lsp.bad;

public class Eagle extends Bird {

    public Eagle() {
        super("Eagle");
    }

    @Override
    public void fly() {
        System.out.println(name + " is soaring majestically");
    }
}
