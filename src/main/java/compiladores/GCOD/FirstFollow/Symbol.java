package compiladores.GCOD.FirstFollow;

import java.util.HashSet;
import java.util.Set;

public class Symbol {
    private String name;
    private boolean isTerminal;
    private Set<Symbol> first;
    private Set<Symbol> follow;

    public Symbol(String name, boolean isTerminal) {
        this.name = name;
        this.isTerminal = isTerminal;
        this.first = new HashSet<>();
        this.follow = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public boolean isTerminal() {
        return isTerminal;
    }

    public Set<Symbol> getFirst() {
        return first;
    }

    public void setFirst(Set<Symbol> first) {
        this.first.clear();
        this.first.addAll(first);
    }

    public void addToFirst(Symbol symbol) {
        this.first.add(symbol);
    }

    public void addAllToFirst(Set<Symbol> symbols) {
        this.first.addAll(symbols);
    }

    public Set<Symbol> getFollow() {
        return follow;
    }

    public void setFollow(Set<Symbol> follow) {
        this.follow.clear();
        this.follow.addAll(follow);
    }

    public void addToFollow(Symbol symbol) {
        this.follow.add(symbol);
    }

    public void addAllToFollow(Set<Symbol> symbols) {
        this.follow.addAll(symbols);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Symbol symbol = (Symbol) obj;
        return name.equals(symbol.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
} 