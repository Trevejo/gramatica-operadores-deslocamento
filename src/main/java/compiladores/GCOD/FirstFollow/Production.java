package compiladores.GCOD.FirstFollow;

import java.util.ArrayList;
import java.util.List;

public class Production {
    private List<Symbol> symbols;

    public Production() {
        this.symbols = new ArrayList<>();
    }

    public void addSymbol(Symbol symbol) {
        symbols.add(symbol);
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }

    public Symbol getFirstSymbol() {
        if (symbols.isEmpty()) {
            return null;
        }
        return symbols.get(0);
    }

    public Symbol getNextSymbol(Symbol currentSymbol) {
        int index = symbols.indexOf(currentSymbol);
        if (index == -1 || index == symbols.size() - 1) {
            return null;
        }
        return symbols.get(index + 1);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Symbol symbol : symbols) {
            sb.append(symbol.getName()).append(" ");
        }
        return sb.toString().trim();
    }
} 