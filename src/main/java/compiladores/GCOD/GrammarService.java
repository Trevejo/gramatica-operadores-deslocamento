package compiladores.GCOD;

import compiladores.GCOD.FirstFollow.*;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Service handling grammar creation and analysis.
 * Responsible for creating a grammar and computing its First and Follow sets.
 */
@Service
public class GrammarService {

    private Grammar grammar;
    private FirstFollow firstFollow;
    private List<String> grammarStrings;
    private final Map<String, NonTerminal> nonTerminals = new HashMap<>();

    /**
     * Initializes the grammar and processes first/follow sets after bean creation.
     */
    @PostConstruct
    public void init() {
        createGrammar();
        processFirstAndFollow();
    }

    /**
     * Creates the grammar with its terminals, non-terminals, and productions.
     */
    private void createGrammar() {
        grammar = new Grammar();
        grammarStrings = new ArrayList<>();

        // Define terminals
        initializeTerminals();
        
        // Define non-terminals
        initializeNonTerminals();
        
        // Setup productions
        defineProductions();
        
        // Set start symbol
        grammar.setStartSymbol(nonTerminals.get("E"));
    }

    /**
     * Initializes all terminal symbols.
     */
    private void initializeTerminals() {
        List<String> terminals = List.of("<<", ">>", "+", "-", "(", ")", "id");
        for (String terminal : terminals) {
            grammar.addTerminal(terminal);
        }
    }

    /**
     * Initializes all non-terminal symbols.
     */
    private void initializeNonTerminals() {
        List<String> ntNames = List.of("E", "T", "F");
        for (String name : ntNames) {
            NonTerminal nt = new NonTerminal(name);
            nonTerminals.put(name, nt);
            grammar.addNonTerminal(nt);
        }
    }

    /**
     * Defines all production rules for the grammar.
     */
    private void defineProductions() {
        // E productions: E → E << T | E >> T | T
        addProduction("E", List.of("E", "<<", "T"), "E → E << T");
        addProduction("E", List.of("E", ">>", "T"), "E → E >> T");
        addProduction("E", List.of("T"), "E → T");

        // T productions: T → T + F | T - F | F
        addProduction("T", List.of("T", "+", "F"), "T → T + F");
        addProduction("T", List.of("T", "-", "F"), "T → T - F");
        addProduction("T", List.of("F"), "T → F");

        // F productions: F → ( E ) | id
        addProduction("F", List.of("(", "E", ")"), "F → ( E )");
        addProduction("F", List.of("id"), "F → id");
    }

    /**
     * Adds a production rule to the grammar.
     *
     * @param leftSide the left-hand side non-terminal
     * @param rightSide the list of symbols on the right-hand side
     * @param productionString the string representation of the production
     */
    private void addProduction(String leftSide, List<String> rightSide, String productionString) {
        NonTerminal nt = nonTerminals.get(leftSide);
        Production production = new Production();
        
        for (String symbol : rightSide) {
            if (nonTerminals.containsKey(symbol)) {
                production.addSymbol(nonTerminals.get(symbol));
            } else {
                production.addSymbol(grammar.getTerminal(symbol));
            }
        }
        
        nt.addProduction(production);
        grammarStrings.add(productionString);
    }

    /**
     * Processes first and follow sets for the grammar.
     */
    private void processFirstAndFollow() {
        firstFollow = new FirstFollow(grammar);
        firstFollow.calculateFirstFollow();
        firstFollow.resolveFollowDependencies();
    }

    /**
     * Returns the string representation of the grammar.
     *
     * @return list of production strings
     */
    public List<String> getGrammarString() {
        return grammarStrings;
    }

    /**
     * Returns the string representation of the First sets.
     *
     * @return formatted First sets string
     */
    public String getFirstSets() {
        return firstFollow.printFirstSets();
    }

    /**
     * Returns the string representation of the Follow sets.
     *
     * @return formatted Follow sets string
     */
    public String getFollowSets() {
        return firstFollow.printFollowSets();
    }
} 