package com.grammar.model;

public class NonTerminal extends Symbol {
    public NonTerminal(String value) {
        super(value);
        if (!value.matches("[A-Z]")) { // Simple check: Non-terminals are uppercase letters
            throw new IllegalArgumentException("NonTerminal value must be an uppercase letter: " + value);
        }
    }

    @Override
    public boolean isTerminal() {
        return false;
    }
} 