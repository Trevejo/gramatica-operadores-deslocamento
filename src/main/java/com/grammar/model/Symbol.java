package com.grammar.model;

import java.util.Objects;

public abstract class Symbol {
    protected final String value;

    protected Symbol(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public abstract boolean isTerminal();

    public boolean isNonTerminal() {
        return !isTerminal();
    }

    // Special symbol for epsilon (empty string)
    public static final Terminal EPSILON = new Terminal("ε");
    // Special symbol for end of input
    public static final Terminal EOF = new Terminal("$");

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Symbol symbol = (Symbol) o;
        return Objects.equals(value, symbol.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
} 