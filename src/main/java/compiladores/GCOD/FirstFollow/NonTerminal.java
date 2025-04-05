package compiladores.GCOD.FirstFollow;

import java.util.ArrayList;
import java.util.List;

public class NonTerminal extends Symbol {
    private List<Production> productions;

    public NonTerminal(String name) {
        super(name, false);
        this.productions = new ArrayList<>();
    }

    public void addProduction(Production production) {
        productions.add(production);
    }

    public List<Production> getProductions() {
        return productions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append(" -> ");
        
        for (int i = 0; i < productions.size(); i++) {
            sb.append(productions.get(i));
            if (i < productions.size() - 1) {
                sb.append(" | ");
            }
        }
        
        return sb.toString();
    }
} 