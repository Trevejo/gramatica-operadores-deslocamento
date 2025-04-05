package com.grammar.logic;

import com.grammar.model.*;

import java.util.*;

/**
 * Computes the FIRST and FOLLOW sets for a given context-free grammar.
 *
 * FIRST(A) for a non-terminal A is the set of terminals that can begin a string derived from A.
 * If A can derive the empty string (epsilon), then epsilon is also in FIRST(A).
 *
 * FOLLOW(A) for a non-terminal A is the set of terminals that can appear immediately after A
 * in some sentential form derived from the start symbol. If A can be the last symbol in a
 * derivation, then the end-of-input marker (EOF) is in FOLLOW(A).
 *
 * This implementation uses iterative algorithms to compute the sets until convergence.
 */
public class FirstFollowCalculator {

    // The grammar for which the sets are computed.
    private final Grammar grammar;
    // Stores the computed FIRST sets for each non-terminal. Includes epsilon if derivable.
    private final Map<NonTerminal, Set<Terminal>> firstSets;
    // Stores the computed FOLLOW sets for each non-terminal. Includes EOF if applicable.
    private final Map<NonTerminal, Set<Terminal>> followSets;
    // Caches the computed FIRST sets for sequences of symbols to avoid redundant calculations.
    private final Map<List<Symbol>, Set<Terminal>> firstSetsForSequences; // Cache for sequence FIRST sets

    /**
     * Constructor for the FirstFollowCalculator.
     * Initializes the sets and triggers the computation of FIRST and FOLLOW sets.
     *
     * @param grammar The input context-free grammar.
     */
    public FirstFollowCalculator(Grammar grammar) {
        this.grammar = grammar;
        this.firstSets = new HashMap<>();
        this.followSets = new HashMap<>();
        this.firstSetsForSequences = new HashMap<>();
        initializeSets();       // Prepare the maps for calculation.
        computeFirstSets();     // Calculate all FIRST sets.
        computeFollowSets();    // Calculate all FOLLOW sets based on FIRST sets.
    }

    /**
     * Initializes the FIRST and FOLLOW set maps.
     * Creates empty sets for each non-terminal.
     * Adds the End-Of-File (EOF) symbol to the FOLLOW set of the start symbol,
     * as the start symbol can be followed by the end of the input.
     */
    private void initializeSets() {
        for (NonTerminal nt : grammar.getNonTerminals()) {
            firstSets.put(nt, new HashSet<>());
            followSets.put(nt, new HashSet<>());
        }
        // The start symbol S can be followed by the end of input.
        followSets.get(grammar.getStartSymbol()).add(Symbol.EOF);
    }

    // --- FIRST Set Computation ---

    /**
     * Computes the FIRST sets for all non-terminals in the grammar using an iterative approach.
     * The algorithm repeatedly applies the FIRST set rules to each production until no
     * new terminals can be added to any FIRST set, indicating convergence.
     */
    private void computeFirstSets() {
        boolean changed;
        // Iterate until no new terminals can be added to any FIRST set (convergence)
        do {
            changed = false;
            // Process each production in the grammar (e.g., A -> X Y Z)
            for (Production p : grammar.getProductions()) {
                NonTerminal lhs = p.getLeftHandSide(); // The non-terminal on the left side (A)
                Set<Terminal> currentFirstSet = firstSets.get(lhs); // Get the current FIRST(A)
                int originalSize = currentFirstSet.size(); // Keep track of size to detect changes

                // Compute the FIRST set for the right-hand side sequence of the production (FIRST(XYZ))
                Set<Terminal> firstOfRhs = computeFirstForSequence(p.getRightHandSide());
                // Add all terminals from FIRST(XYZ) to FIRST(A)
                // Rule: FIRST(A) = FIRST(A) U FIRST(XYZ)
                currentFirstSet.addAll(firstOfRhs);

                // If the size of FIRST(A) increased, it means we added new terminals
                if (currentFirstSet.size() > originalSize) {
                    changed = true; // Mark that a change occurred in this pass, need another iteration
                }
            }
        } while (changed); // Continue iterating as long as changes are being made
    }

    /**
     * Computes the FIRST set for a given sequence of grammar symbols (terminals or non-terminals).
     * Example: For a sequence X Y Z, FIRST(XYZ) is calculated.
     * Uses memoization (caching) via `firstSetsForSequences` to optimize repeated calculations
     * for the same sequence.
     *
     * Rule:
     * 1. Add FIRST(X) - {epsilon} to FIRST(XYZ).
     * 2. If epsilon is in FIRST(X), add FIRST(Y) - {epsilon} to FIRST(XYZ).
     * 3. If epsilon is in FIRST(X) and FIRST(Y), add FIRST(Z) - {epsilon} to FIRST(XYZ).
     * 4. If epsilon is in FIRST(X), FIRST(Y), and FIRST(Z), add epsilon to FIRST(XYZ).
     *
     * @param sequence The list of symbols (e.g., [X, Y, Z]).
     * @return The set of terminals that can start a string derived from the sequence, including epsilon if applicable.
     */
    public Set<Terminal> computeFirstForSequence(List<Symbol> sequence) {
        // Check cache first
        if (firstSetsForSequences.containsKey(sequence)) {
            return firstSetsForSequences.get(sequence);
        }

        Set<Terminal> result = new HashSet<>();
        boolean derivesEpsilon = true; // Assume the sequence can derive epsilon initially

        // Iterate through each symbol in the sequence
        for (Symbol symbol : sequence) {
            // Get the FIRST set of the current symbol (X, then Y, then Z)
            Set<Terminal> firstOfSymbol = computeFirstForSymbol(symbol);

            // Add all terminals from FIRST(symbol) except epsilon to the result set
            for(Terminal t : firstOfSymbol) {
                if (!t.equals(Symbol.EPSILON)) {
                    result.add(t);
                }
            }

            // If FIRST(symbol) does not contain epsilon, then the sequence as a whole
            // cannot derive epsilon starting from this symbol. Stop processing the sequence.
            if (!firstOfSymbol.contains(Symbol.EPSILON)) {
                derivesEpsilon = false;
                break; // No need to look at subsequent symbols (Y, Z)
            }
            // If we are here, FIRST(symbol) contained epsilon.
            // We continue to the next symbol in the sequence to check its FIRST set.
        }

        // If after checking all symbols, `derivesEpsilon` is still true, it means
        // every symbol in the sequence could derive epsilon. Therefore, the entire
        // sequence can derive epsilon. Add epsilon to the result set.
        if (derivesEpsilon) {
            result.add(Symbol.EPSILON);
        }

        // Cache the computed result before returning
        firstSetsForSequences.put(sequence, result);
        return result;
    }

    /**
     * Computes the FIRST set for a single grammar symbol.
     *
     * Rule:
     * 1. If the symbol is a terminal 't', FIRST(t) = {t}.
     * 2. If the symbol is a non-terminal 'A', FIRST(A) is the set already computed
     *    (or partially computed during the iterative process) and stored in `firstSets`.
     *    This handles recursion correctly.
     *
     * @param symbol The grammar symbol (Terminal or NonTerminal).
     * @return The FIRST set for the given symbol.
     */
    private Set<Terminal> computeFirstForSymbol(Symbol symbol) {
        Set<Terminal> firstSet = new HashSet<>();
        if (symbol.isTerminal()) {
            // If it's a terminal, its FIRST set is just itself.
            firstSet.add((Terminal) symbol);
        } else { // NonTerminal
            // If it's a non-terminal, retrieve its pre-computed or partially computed FIRST set.
            // During the main `computeFirstSets` loop, this might return a set that's still growing,
            // which is essential for the iterative algorithm to work correctly, especially with recursion.
            firstSet.addAll(firstSets.get((NonTerminal) symbol));
        }
        return firstSet;
    }

    // --- FOLLOW Set Computation ---

    /**
     * Computes the FOLLOW sets for all non-terminals in the grammar using an iterative approach.
     * Requires the FIRST sets to be computed beforehand.
     * The algorithm repeatedly applies the FOLLOW set rules to each production until no
     * new terminals can be added to any FOLLOW set, indicating convergence.
     */
    private void computeFollowSets() {
        boolean changed;
        // Iterate until no new terminals can be added to any FOLLOW set (convergence)
        do {
            changed = false;
            // Process each production in the grammar (e.g., A -> alpha B beta)
            for (Production p : grammar.getProductions()) {
                NonTerminal A = p.getLeftHandSide(); // The non-terminal on the LHS (A)
                List<Symbol> alpha = p.getRightHandSide(); // The sequence on the RHS (alpha B beta)

                // Iterate through the symbols on the right-hand side
                for (int i = 0; i < alpha.size(); i++) {
                    Symbol B_symbol = alpha.get(i); // Current symbol being examined (potentially B)

                    // We only care about non-terminals for FOLLOW sets
                    if (B_symbol.isNonTerminal()) {
                        NonTerminal B = (NonTerminal) B_symbol; // The non-terminal B
                        // beta is the sequence of symbols immediately following B in the production
                        // If B is the last symbol, beta is empty.
                        List<Symbol> beta = (i + 1 < alpha.size()) ? alpha.subList(i + 1, alpha.size()) : Collections.emptyList();

                        // Compute FIRST(beta) - the set of terminals that can start strings derived from beta.
                        Set<Terminal> firstOfBeta = computeFirstForSequence(beta);

                        // --- Apply FOLLOW Set Rules ---
                        Set<Terminal> followB = followSets.get(B); // Get the current FOLLOW(B) set
                        int originalSize = followB.size(); // Track size for change detection

                        // Rule 2: For a production A -> alpha B beta, add FIRST(beta) - {epsilon} to FOLLOW(B).
                        // If beta can derive a terminal 't', then 't' can follow B.
                        for (Terminal t : firstOfBeta) {
                            if (!t.equals(Symbol.EPSILON)) {
                                followB.add(t);
                            }
                        }
                        // Check if Rule 2 added any new terminals
                        if (followB.size() > originalSize) changed = true;


                        // Rule 3: For a production A -> alpha B beta where epsilon is in FIRST(beta)
                        // (meaning beta can derive the empty string), OR if beta is empty (A -> alpha B),
                        // then everything in FOLLOW(A) must also be in FOLLOW(B).
                        // If whatever follows A can also follow B because beta can vanish.
                        if (firstOfBeta.contains(Symbol.EPSILON) || beta.isEmpty()) {
                            Set<Terminal> followA = followSets.get(A); // Get FOLLOW(A)
                            originalSize = followB.size(); // Update original size before adding FOLLOW(A)
                            followB.addAll(followA); // Add all terminals from FOLLOW(A) to FOLLOW(B)
                            // Check if Rule 3 added any new terminals
                            if (followB.size() > originalSize) changed = true;
                        }
                    }
                }
            }
        } while (changed); // Continue iterating as long as changes are being made
    }

    // --- Getters ---

    /**
     * Gets the computed FIRST sets for all non-terminals.
     * This version returns a *copy* of the sets with the epsilon symbol removed.
     * This is typically used for displaying FIRST sets, as epsilon is often handled separately.
     *
     * @return A map where keys are NonTerminals and values are their computed FIRST sets (without epsilon).
     */
    public Map<NonTerminal, Set<Terminal>> getFirstSets() {
        // Create a new map for the display version
        Map<NonTerminal, Set<Terminal>> displayFirstSets = new HashMap<>();
        // Iterate through the internally stored FIRST sets (which may include epsilon)
        for (Map.Entry<NonTerminal, Set<Terminal>> entry : firstSets.entrySet()) {
            // Create a copy of the set for the current non-terminal
            Set<Terminal> terminals = new HashSet<>(entry.getValue());
            // Remove epsilon, if present
            terminals.remove(Symbol.EPSILON);
            // Put the modified set into the display map
            displayFirstSets.put(entry.getKey(), terminals);
        }
        // Return the map containing sets without epsilon
        return displayFirstSets;
    }

     /**
      * Gets the computed FIRST sets for all non-terminals, including epsilon if derivable.
      * This returns an unmodifiable view of the internal map used during calculations.
      * Useful for algorithms that need the complete FIRST set information (like FOLLOW set computation).
      *
      * @return An unmodifiable map where keys are NonTerminals and values are their complete computed FIRST sets.
      */
     public Map<NonTerminal, Set<Terminal>> getInternalFirstSets() {
        // Return an unmodifiable view to prevent external modification
        return Collections.unmodifiableMap(firstSets);
    }

    /**
     * Gets the computed FOLLOW sets for all non-terminals.
     * Returns an unmodifiable view of the internal FOLLOW set map.
     * The sets include the EOF symbol where appropriate.
     *
     * @return An unmodifiable map where keys are NonTerminals and values are their computed FOLLOW sets.
     */
    public Map<NonTerminal, Set<Terminal>> getFollowSets() {
        // Return an unmodifiable view to prevent external modification
        return Collections.unmodifiableMap(followSets);
    }
} 