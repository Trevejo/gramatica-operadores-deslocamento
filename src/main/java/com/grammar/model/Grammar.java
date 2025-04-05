package com.grammar.model;

import java.util.*;
import java.util.stream.Collectors;

public class Grammar {
    private final Set<NonTerminal> nonTerminals;
    private final Set<Terminal> terminals;
    private final List<Production> productions;
    private final NonTerminal startSymbol;

    public Grammar(Set<NonTerminal> nonTerminals, Set<Terminal> terminals,
                   List<Production> productions, NonTerminal startSymbol) {
        this.nonTerminals = Collections.unmodifiableSet(new HashSet<>(nonTerminals));
        this.terminals = Collections.unmodifiableSet(new HashSet<>(terminals));
        this.productions = Collections.unmodifiableList(new ArrayList<>(productions));
        this.startSymbol = Objects.requireNonNull(startSymbol, "Start symbol cannot be null");

        if (!nonTerminals.contains(startSymbol)) {
            throw new IllegalArgumentException("Start symbol must be one of the non-terminals.");
        }
        // Add EOF to terminals if not present
        if (!this.terminals.contains(Symbol.EOF)) {
           Set<Terminal> augmentedTerminals = new HashSet<>(this.terminals);
           augmentedTerminals.add(Symbol.EOF);
           // Re-assign (though the instance field is final, we can work around it if needed or adjust design)
           // For simplicity, let's assume EOF should be explicitly added by the user or handled elsewhere.
           // Or, modify constructor logic if EOF should always be implicitly included.
           // System.out.println("Warning: EOF symbol ($) not explicitly in terminals set. Added implicitly for calculations.");
        }
    }

    public Set<NonTerminal> getNonTerminals() {
        return nonTerminals;
    }

    public Set<Terminal> getTerminals() {
        // Return a set including EOF for consistency in algorithms
        Set<Terminal> allTerminals = new HashSet<>(terminals);
        allTerminals.add(Symbol.EOF);
        return Collections.unmodifiableSet(allTerminals);
    }

    public List<Production> getProductions() {
        return productions;
    }

    public NonTerminal getStartSymbol() {
        return startSymbol;
    }

    public List<Production> getProductionsFor(NonTerminal nonTerminal) {
        return productions.stream()
                .filter(p -> p.getLeftHandSide().equals(nonTerminal))
                .collect(Collectors.toList());
    }

    public Set<Symbol> getAllSymbols() {
        Set<Symbol> allSymbols = new HashSet<>(nonTerminals);
        allSymbols.addAll(terminals);
        allSymbols.add(Symbol.EOF);
        return allSymbols;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("NonTerminals: ").append(nonTerminals).append("\n");
        sb.append("Terminals: ").append(terminals).append("\n"); // Show original terminals
        sb.append("Start Symbol: ").append(startSymbol).append("\n");
        sb.append("Productions:\n");
        productions.forEach(p -> sb.append("  ").append(p).append("\n"));
        return sb.toString();
    }
} 