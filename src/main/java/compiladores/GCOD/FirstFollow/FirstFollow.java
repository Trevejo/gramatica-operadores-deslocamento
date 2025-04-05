package compiladores.GCOD.FirstFollow;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Responsible for calculating First and Follow sets for a context-free grammar.
 * This class implements algorithms for computing these sets based on grammar rules.
 */
public class FirstFollow {
    private final Grammar grammar;
    private final Symbol END_MARKER = new Symbol("$", true);
    private boolean followSetsResolved = false;

    /**
     * Creates a new FirstFollow instance for the given grammar.
     *
     * @param grammar the grammar to analyze
     */
    public FirstFollow(Grammar grammar) {
        this.grammar = grammar;
    }

    /**
     * Calculates FIRST and FOLLOW sets for all non-terminals in the grammar.
     * This is the main entry point for the analysis process.
     */
    public void calculateFirstFollow() {
        calculateAllFirstSets();
        calculateAllFollowSets();
    }

    /**
     * Calculates FIRST sets for all non-terminals in the grammar.
     */
    private void calculateAllFirstSets() {
        for (NonTerminal nonTerminal : grammar.getNonTerminals()) {
            calculateFirstSet(nonTerminal);
        }
    }

    /**
     * Calculates FOLLOW sets for all non-terminals in the grammar.
     */
    private void calculateAllFollowSets() {
        // Add end marker to the start symbol's FOLLOW set
        addEndMarkerToStartSymbol();
        
        // Calculate initial FOLLOW sets
        for (NonTerminal nonTerminal : grammar.getNonTerminals()) {
            calculateFollowSet(nonTerminal);
        }
    }

    /**
     * Adds the end marker to the start symbol's FOLLOW set.
     */
    private void addEndMarkerToStartSymbol() {
        grammar.getStartSymbol().addToFollow(END_MARKER);
    }

    /**
     * Calculates the FIRST set for a non-terminal.
     * 
     * @param nonTerminal the non-terminal to calculate FIRST set for
     * @return the calculated FIRST set
     */
    private Set<Symbol> calculateFirstSet(NonTerminal nonTerminal) {
        // If FIRST set is already calculated, return it
        if (!nonTerminal.getFirst().isEmpty()) {
            return Collections.unmodifiableSet(nonTerminal.getFirst());
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
                    Set<Symbol> first = calculateFirstSet(firstNonTerminal);
                    firstSet.addAll(first);
                }
            }
        }

        nonTerminal.setFirst(firstSet);
        return Collections.unmodifiableSet(firstSet);
    }

    /**
     * Calculates the FOLLOW set for a non-terminal.
     * 
     * @param targetNonTerminal the non-terminal to calculate FOLLOW set for
     */
    private void calculateFollowSet(NonTerminal targetNonTerminal) {
        for (NonTerminal originNonTerminal : grammar.getNonTerminals()) {
            processProductionsForFollowSet(originNonTerminal, targetNonTerminal);
        }
    }

    /**
     * Processes all productions from an origin non-terminal to compute the FOLLOW set
     * of a target non-terminal.
     * 
     * @param originNonTerminal the non-terminal whose productions are being examined
     * @param targetNonTerminal the non-terminal whose FOLLOW set is being calculated
     */
    private void processProductionsForFollowSet(NonTerminal originNonTerminal, NonTerminal targetNonTerminal) {
        for (Production production : originNonTerminal.getProductions()) {
            List<Symbol> symbols = production.getSymbols();
            
            for (int i = 0; i < symbols.size(); i++) {
                Symbol symbol = symbols.get(i);
                
                if (symbol.equals(targetNonTerminal)) {
                    if (isLastSymbolInProduction(i, symbols)) {
                        handleLastSymbolCase(targetNonTerminal, originNonTerminal);
                    } else {
                        handleSymbolFollowedByNextSymbol(targetNonTerminal, symbols, i, originNonTerminal);
                    }
                }
            }
        }
    }

    /**
     * Checks if the symbol at the given index is the last symbol in the production.
     * 
     * @param index the index of the symbol
     * @param symbols the list of symbols in the production
     * @return true if it's the last symbol, false otherwise
     */
    private boolean isLastSymbolInProduction(int index, List<Symbol> symbols) {
        return index == symbols.size() - 1;
    }

    /**
     * Handles the case when the target non-terminal is the last symbol in a production.
     * 
     * @param targetNonTerminal the non-terminal whose FOLLOW set is being calculated
     * @param originNonTerminal the non-terminal that has this production
     */
    private void handleLastSymbolCase(NonTerminal targetNonTerminal, NonTerminal originNonTerminal) {
        // If A -> αB is a production, FOLLOW(B) includes FOLLOW(A)
        if (!originNonTerminal.equals(targetNonTerminal)) {
            // To avoid infinite recursion, we'll add this dependency for later resolution
            addFollowDependency(targetNonTerminal, originNonTerminal);
        }
    }

    /**
     * Handles the case when the target non-terminal is followed by another symbol.
     * 
     * @param targetNonTerminal the non-terminal whose FOLLOW set is being calculated
     * @param symbols the symbols in the production
     * @param index the index of the target non-terminal
     * @param originNonTerminal the non-terminal that has this production
     */
    private void handleSymbolFollowedByNextSymbol(
            NonTerminal targetNonTerminal, 
            List<Symbol> symbols, 
            int index, 
            NonTerminal originNonTerminal) {
        
        Symbol nextSymbol = symbols.get(index + 1);
        
        if (nextSymbol.isTerminal()) {
            // If the next symbol is a terminal, add it to FOLLOW
            targetNonTerminal.addToFollow(nextSymbol);
        } else {
            // If the next symbol is a non-terminal, add its FIRST set to FOLLOW
            NonTerminal nextNonTerminal = (NonTerminal) nextSymbol;
            targetNonTerminal.addAllToFollow(nextNonTerminal.getFirst());
            
            // If this is the second-to-last symbol, handle follow dependency
            if (isSecondToLastSymbol(index, symbols)) {
                if (!originNonTerminal.equals(targetNonTerminal)) {
                    addFollowDependency(targetNonTerminal, originNonTerminal);
                }
            }
        }
    }

    /**
     * Checks if the symbol at the given index is the second-to-last symbol in the production.
     * 
     * @param index the index of the symbol
     * @param symbols the list of symbols in the production
     * @return true if it's the second-to-last symbol, false otherwise
     */
    private boolean isSecondToLastSymbol(int index, List<Symbol> symbols) {
        return index == symbols.size() - 2;
    }

    /**
     * Adds a dependency that the FOLLOW set of the dependent non-terminal 
     * includes the FOLLOW set of the source non-terminal.
     * 
     * @param dependent the non-terminal that depends on the source
     * @param source the source non-terminal
     */
    private void addFollowDependency(NonTerminal dependent, NonTerminal source) {
        dependent.addAllToFollow(source.getFollow());
    }

    /**
     * Resolves FOLLOW dependencies after initial calculation.
     * This is needed because of recursive dependencies between FOLLOW sets.
     */
    public void resolveFollowDependencies() {
        if (followSetsResolved) {
            return;
        }
        
        // Repeat until no more changes occur
        boolean changed;
        do {
            changed = resolveOneCycleOfFollowDependencies();
        } while (changed);
        
        followSetsResolved = true;
    }

    /**
     * Executes one cycle of resolving FOLLOW dependencies.
     * 
     * @return true if any FOLLOW sets changed during this cycle, false otherwise
     */
    private boolean resolveOneCycleOfFollowDependencies() {
        boolean changed = false;
        
        for (NonTerminal nonTerminal : grammar.getNonTerminals()) {
            int originalSize = nonTerminal.getFollow().size();
            
            changed |= propagateFollowSetsFromProductions(nonTerminal);
            
            if (nonTerminal.getFollow().size() > originalSize) {
                changed = true;
            }
        }
        
        return changed;
    }

    /**
     * Propagates FOLLOW sets from a non-terminal to the last non-terminal in each of its productions.
     * 
     * @param nonTerminal the non-terminal whose FOLLOW set is being propagated
     * @return true if any FOLLOW sets changed, false otherwise
     */
    private boolean propagateFollowSetsFromProductions(NonTerminal nonTerminal) {
        boolean changed = false;
        
        for (Production production : nonTerminal.getProductions()) {
            List<Symbol> symbols = production.getSymbols();
            
            if (!symbols.isEmpty()) {
                Symbol lastSymbol = symbols.get(symbols.size() - 1);
                
                if (!lastSymbol.isTerminal()) {
                    NonTerminal lastNt = (NonTerminal) lastSymbol;
                    int beforeSize = lastNt.getFollow().size();
                    
                    lastNt.addAllToFollow(nonTerminal.getFollow());
                    
                    if (lastNt.getFollow().size() > beforeSize) {
                        changed = true;
                    }
                }
            }
        }
        
        return changed;
    }

    /**
     * Returns the grammar being analyzed.
     * 
     * @return the grammar
     */
    public Grammar getGrammar() {
        return grammar;
    }

    /**
     * Creates a string representation of all FIRST sets.
     * 
     * @return a formatted string containing all FIRST sets
     */
    public String printFirstSets() {
        StringBuilder sb = new StringBuilder();
        sb.append("FIRST Sets:\n");
        
        for (NonTerminal nonTerminal : grammar.getNonTerminals()) {
            appendSet(sb, "FIRST", nonTerminal.getName(), nonTerminal.getFirst());
        }
        
        return sb.toString();
    }

    /**
     * Creates a string representation of all FOLLOW sets.
     * 
     * @return a formatted string containing all FOLLOW sets
     */
    public String printFollowSets() {
        // Make sure FOLLOW sets are fully resolved
        resolveFollowDependencies();
        
        StringBuilder sb = new StringBuilder();
        sb.append("FOLLOW Sets:\n");
        
        for (NonTerminal nonTerminal : grammar.getNonTerminals()) {
            appendSet(sb, "FOLLOW", nonTerminal.getName(), nonTerminal.getFollow());
        }
        
        return sb.toString();
    }

    /**
     * Helper method to append a formatted set to a StringBuilder.
     * 
     * @param sb the StringBuilder to append to
     * @param setName the name of the set (e.g., "FIRST" or "FOLLOW")
     * @param symbolName the name of the symbol
     * @param symbols the set of symbols
     */
    private void appendSet(StringBuilder sb, String setName, String symbolName, Set<Symbol> symbols) {
        sb.append(setName).append("(").append(symbolName).append(") = { ");
        
        boolean first = true;
        for (Symbol symbol : symbols) {
            if (!first) {
                sb.append(" ");
            }
            sb.append(symbol.getName());
            first = false;
        }
        
        sb.append(" }\n");
    }
} 