package com.grammar.model;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Production {
    private final NonTerminal leftHandSide;
    private final List<Symbol> rightHandSide;

    public Production(NonTerminal leftHandSide, List<Symbol> rightHandSide) {
        this.leftHandSide = Objects.requireNonNull(leftHandSide, "Left hand side cannot be null");
        this.rightHandSide = Objects.requireNonNull(rightHandSide, "Right hand side cannot be null");
        // Allow empty right hand side for epsilon productions
    }

    public NonTerminal getLeftHandSide() {
        return leftHandSide;
    }

    public List<Symbol> getRightHandSide() {
        return rightHandSide;
    }

    public boolean isEpsilonProduction() {
        return rightHandSide.size() == 1 && rightHandSide.get(0).equals(Symbol.EPSILON);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Production that = (Production) o;
        return Objects.equals(leftHandSide, that.leftHandSide) &&
               Objects.equals(rightHandSide, that.rightHandSide);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftHandSide, rightHandSide);
    }

    @Override
    public String toString() {
        return leftHandSide + " → " +
               (rightHandSide.isEmpty() ? Symbol.EPSILON.toString() :
                rightHandSide.stream().map(Symbol::toString).collect(Collectors.joining(" ")));
    }
} 