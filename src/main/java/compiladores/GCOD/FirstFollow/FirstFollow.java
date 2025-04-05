package compiladores.GCOD.FirstFollow;

import java.util.HashSet;
import java.util.Set;

public class FirstFollow {
    private Grammar grammar;
    private final Symbol END_MARKER = new Symbol("$", true);

    public FirstFollow(Grammar grammar) {
        this.grammar = grammar;
    }

    public void calculateFirstFollow() {
        // Calculate FIRST sets
        for (NonTerminal nonTerminal : grammar.getNonTerminals()) {
            calculateFirst(nonTerminal);
        }

        // Calculate FOLLOW sets
        for (NonTerminal nonTerminal : grammar.getNonTerminals()) {
            calculateFollow(nonTerminal);
        }
    }

    private Set<Symbol> calculateFirst(NonTerminal nonTerminal) {
        // If FIRST set is already calculated, return it
        if (!nonTerminal.getFirst().isEmpty()) {
            return nonTerminal.getFirst();
        }

        Set<Symbol> firstSet = new HashSet<>();

        for (Production production : nonTerminal.getProductions()) {
            Symbol firstSymbol = production.getFirstSymbol();
            if (firstSymbol == null) {
                continue;
            }

            if (firstSymbol.isTerminal()) {
                firstSet.add(firstSymbol);
            } else {
                NonTerminal firstNonTerminal = (NonTerminal) firstSymbol;
                // Avoid infinite recursion for left-recursive productions
                if (!firstNonTerminal.equals(nonTerminal)) {
                    Set<Symbol> first = calculateFirst(firstNonTerminal);
                    firstSet.addAll(first);
                }
            }
        }

        nonTerminal.setFirst(firstSet);
        return firstSet;
    }

    private void calculateFollow(NonTerminal targetNonTerminal) {
        // Add end marker to the start symbol
        if (targetNonTerminal.equals(grammar.getStartSymbol())) {
            targetNonTerminal.addToFollow(END_MARKER);
        }

        for (NonTerminal originNonTerminal : grammar.getNonTerminals()) {
            for (Production production : originNonTerminal.getProductions()) {
                for (int i = 0; i < production.getSymbols().size(); i++) {
                    Symbol symbol = production.getSymbols().get(i);
                    
                    if (symbol.equals(targetNonTerminal)) {
                        // If this is the last symbol in the production
                        if (i == production.getSymbols().size() - 1) {
                            // If A -> αB is a production, add FOLLOW(A) to FOLLOW(B)
                            if (!originNonTerminal.equals(targetNonTerminal)) {
                                // To avoid infinite recursion, we'll add this in a post-processing step
                                addFollowDependency(targetNonTerminal, originNonTerminal);
                            }
                        } else {
                            // Get the next symbol
                            Symbol nextSymbol = production.getSymbols().get(i + 1);
                            
                            if (nextSymbol.isTerminal()) {
                                // If the next symbol is a terminal, add it to FOLLOW
                                targetNonTerminal.addToFollow(nextSymbol);
                            } else {
                                // If the next symbol is a non-terminal, add its FIRST set to FOLLOW
                                NonTerminal nextNonTerminal = (NonTerminal) nextSymbol;
                                targetNonTerminal.addAllToFollow(nextNonTerminal.getFirst());
                                
                                // If this is the last symbol or if all remaining symbols can derive empty,
                                // add FOLLOW(originNonTerminal) to FOLLOW(targetNonTerminal)
                                if (i == production.getSymbols().size() - 2) {
                                    if (!originNonTerminal.equals(targetNonTerminal)) {
                                        addFollowDependency(targetNonTerminal, originNonTerminal);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void addFollowDependency(NonTerminal dependent, NonTerminal source) {
        // Add the FOLLOW set of source to dependent
        dependent.addAllToFollow(source.getFollow());
    }

    // Method to resolve FOLLOW dependencies after initial calculation
    public void resolveFollowDependencies() {
        // Repeat until no more changes occur
        boolean changed;
        do {
            changed = false;
            
            for (NonTerminal nonTerminal : grammar.getNonTerminals()) {
                int originalSize = nonTerminal.getFollow().size();
                
                for (Production production : nonTerminal.getProductions()) {
                    // For each production A -> α B
                    // where B is the last symbol or followed by symbols that can derive empty
                    for (int i = 0; i < production.getSymbols().size(); i++) {
                        Symbol symbol = production.getSymbols().get(i);
                        
                        if (!symbol.isTerminal() && i == production.getSymbols().size() - 1) {
                            NonTerminal nt = (NonTerminal) symbol;
                            int beforeSize = nt.getFollow().size();
                            nt.addAllToFollow(nonTerminal.getFollow());
                            if (nt.getFollow().size() > beforeSize) {
                                changed = true;
                            }
                        }
                    }
                }
                
                if (nonTerminal.getFollow().size() > originalSize) {
                    changed = true;
                }
            }
        } while (changed);
    }

    public Grammar getGrammar() {
        return grammar;
    }

    public String printFirstSets() {
        StringBuilder sb = new StringBuilder();
        sb.append("FIRST Sets:\n");
        
        for (NonTerminal nonTerminal : grammar.getNonTerminals()) {
            sb.append("FIRST(").append(nonTerminal.getName()).append(") = { ");
            for (Symbol symbol : nonTerminal.getFirst()) {
                sb.append(symbol.getName()).append(" ");
            }
            sb.append("}\n");
        }
        
        return sb.toString();
    }

    public String printFollowSets() {
        StringBuilder sb = new StringBuilder();
        sb.append("FOLLOW Sets:\n");
        
        for (NonTerminal nonTerminal : grammar.getNonTerminals()) {
            sb.append("FOLLOW(").append(nonTerminal.getName()).append(") = { ");
            for (Symbol symbol : nonTerminal.getFollow()) {
                sb.append(symbol.getName()).append(" ");
            }
            sb.append("}\n");
        }
        
        return sb.toString();
    }
} 