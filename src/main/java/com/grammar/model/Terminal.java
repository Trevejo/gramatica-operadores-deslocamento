package com.grammar.model;

public class Terminal extends Symbol {
    public Terminal(String value) {
        super(value);
    }

    @Override
    public boolean isTerminal() {
        return true;
    }
} 