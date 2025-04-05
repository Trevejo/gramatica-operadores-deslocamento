package compiladores.GCOD.FirstFollow;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Grammar {
    private Set<Symbol> terminals;
    private Map<String, NonTerminal> nonTerminals;
    private NonTerminal startSymbol;

    public Grammar() {
        terminals = new HashSet<>();
        nonTerminals = new HashMap<>();
    }

    public void addTerminal(String name) {
        terminals.add(new Symbol(name, true));
    }

    public Symbol getTerminal(String name) {
        for (Symbol terminal : terminals) {
            if (terminal.getName().equals(name)) {
                return terminal;
            }
        }
        return null;
    }

    public void addNonTerminal(NonTerminal nonTerminal) {
        nonTerminals.put(nonTerminal.getName(), nonTerminal);
        if (startSymbol == null) {
            startSymbol = nonTerminal;
        }
    }

    public NonTerminal getNonTerminal(String name) {
        return nonTerminals.get(name);
    }

    public Set<Symbol> getTerminals() {
        return terminals;
    }

    public Set<NonTerminal> getNonTerminals() {
        return new HashSet<>(nonTerminals.values());
    }

    public NonTerminal getStartSymbol() {
        return startSymbol;
    }

    public void setStartSymbol(NonTerminal startSymbol) {
        this.startSymbol = startSymbol;
    }
} 