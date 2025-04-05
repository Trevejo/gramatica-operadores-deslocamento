package com.grammar.controller;

import com.grammar.logic.FirstFollowCalculator;
import com.grammar.model.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class GrammarController {

    private final Grammar grammar;
    private final Map<NonTerminal, Set<Terminal>> firstSets;
    private final Map<NonTerminal, Set<Terminal>> followSets;
    private final Map<String, String> expectedFirst;
    private final Map<String, String> expectedFollow;

    public GrammarController() {
        // --- Define the Grammar ---
        NonTerminal E = new NonTerminal("E");
        NonTerminal T = new NonTerminal("T");
        NonTerminal F = new NonTerminal("F");

        Terminal plus = new Terminal("+");
        Terminal minus = new Terminal("-");
        Terminal shiftLeft = new Terminal("<<");
        Terminal shiftRight = new Terminal(">>");
        Terminal openParen = new Terminal("(");
        Terminal closeParen = new Terminal(")");
        Terminal id = new Terminal("id");

        Set<NonTerminal> nonTerminals = new HashSet<>(Arrays.asList(E, T, F));
        Set<Terminal> terminals = new HashSet<>(Arrays.asList(plus, minus, shiftLeft, shiftRight, openParen, closeParen, id));

        Production p1 = new Production(E, Arrays.asList(E, shiftLeft, T));
        Production p2 = new Production(E, Arrays.asList(E, shiftRight, T));
        Production p3 = new Production(E, List.of(T));
        Production p4 = new Production(T, Arrays.asList(T, plus, F));
        Production p5 = new Production(T, Arrays.asList(T, minus, F));
        Production p6 = new Production(T, List.of(F));
        Production p7 = new Production(F, Arrays.asList(openParen, E, closeParen));
        Production p8 = new Production(F, List.of(id));

        List<Production> productions = Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8);

        this.grammar = new Grammar(nonTerminals, terminals, productions, E);

        // --- Calculate FIRST and FOLLOW sets ---
        FirstFollowCalculator calculator = new FirstFollowCalculator(this.grammar);
        this.firstSets = calculator.getFirstSets();
        this.followSets = calculator.getFollowSets();

        // --- Prepare Expected Results ---
        this.expectedFirst = new LinkedHashMap<>();
        expectedFirst.put("FIRST(F)", "{ (, id }");
        expectedFirst.put("FIRST(T)", "{ (, id }");
        expectedFirst.put("FIRST(E)", "{ (, id }");

        this.expectedFollow = new LinkedHashMap<>();
        expectedFollow.put("FOLLOW(E)", "{ ), <<, >>, $ }");
        expectedFollow.put("FOLLOW(T)", "{ +, -, ), <<, >>, $ }");
        expectedFollow.put("FOLLOW(F)", "{ +, -, ), <<, >>, $ }");
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("pageTitle", "GRAMÁTICA COM OPERADORES DE DESLOCAMENTO");
        model.addAttribute("grammarTitle", "Grammar Productions:");
        // Optionally pass the raw productions if needed by the template
        // model.addAttribute("productions", grammar.getProductions());

        model.addAttribute("firstSetsTitle", "FIRST Sets (Calculated)");
        model.addAttribute("firstSets", formatSetsForView(firstSets));
        model.addAttribute("followSetsTitle", "FOLLOW Sets (Calculated)");
        model.addAttribute("followSets", formatSetsForView(followSets));

        model.addAttribute("expectedFirstTitle", "Expected FIRST Sets:");
        model.addAttribute("expectedFirstSets", expectedFirst);
        model.addAttribute("expectedFollowTitle", "Expected FOLLOW Sets:");
        model.addAttribute("expectedFollowSets", expectedFollow);

        return "index"; // Return the name of the Thymeleaf template (index.html)
    }

    // Helper to format sets for display in the view
    private Map<String, String> formatSetsForView(Map<NonTerminal, Set<Terminal>> sets) {
        Map<String, String> formattedSets = new LinkedHashMap<>();
        sets.entrySet().stream()
            .sorted(Map.Entry.comparingByKey(Comparator.comparing(Symbol::getValue)))
            .forEach(entry -> formattedSets.put(entry.getKey().getValue(), formatSet(entry.getValue())));
        return formattedSets;
    }

    // Helper to format a single set
    private String formatSet(Set<Terminal> set) {
        if (set == null || set.isEmpty()) {
            return "{ }";
        }
        return "{ " +
               set.stream()
                  .map(Symbol::getValue)
                  .sorted() // Sort for consistent output
                  .collect(Collectors.joining(", ")) +
               " }";
    }
} 