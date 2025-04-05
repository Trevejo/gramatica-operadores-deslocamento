package compiladores.GCOD;

import compiladores.GCOD.FirstFollow.*;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class GrammarService {

    private Grammar grammar;
    private FirstFollow firstFollow;
    private List<String> grammarStrings;

    @PostConstruct
    public void init() {
        createGrammar();
        processFirstAndFollow();
    }

    private void createGrammar() {
        grammar = new Grammar();
        grammarStrings = new ArrayList<>();

        // Define terminals
        grammar.addTerminal("<<");
        grammar.addTerminal(">>");
        grammar.addTerminal("+");
        grammar.addTerminal("-");
        grammar.addTerminal("(");
        grammar.addTerminal(")");
        grammar.addTerminal("id");

        // Define non-terminals
        NonTerminal E = new NonTerminal("E");
        NonTerminal T = new NonTerminal("T");
        NonTerminal F = new NonTerminal("F");

        // Add non-terminals to grammar
        grammar.addNonTerminal(E);
        grammar.addNonTerminal(T);
        grammar.addNonTerminal(F);

        // Setup productions for E
        // E → E << T | E >> T | T
        Production eLeftShift = new Production();
        eLeftShift.addSymbol(E);
        eLeftShift.addSymbol(grammar.getTerminal("<<"));
        eLeftShift.addSymbol(T);
        E.addProduction(eLeftShift);
        grammarStrings.add("E → E << T");

        Production eRightShift = new Production();
        eRightShift.addSymbol(E);
        eRightShift.addSymbol(grammar.getTerminal(">>"));
        eRightShift.addSymbol(T);
        E.addProduction(eRightShift);
        grammarStrings.add("E → E >> T");

        Production eToT = new Production();
        eToT.addSymbol(T);
        E.addProduction(eToT);
        grammarStrings.add("E → T");

        // Setup productions for T
        // T → T + F | T - F | F
        Production tPlus = new Production();
        tPlus.addSymbol(T);
        tPlus.addSymbol(grammar.getTerminal("+"));
        tPlus.addSymbol(F);
        T.addProduction(tPlus);
        grammarStrings.add("T → T + F");

        Production tMinus = new Production();
        tMinus.addSymbol(T);
        tMinus.addSymbol(grammar.getTerminal("-"));
        tMinus.addSymbol(F);
        T.addProduction(tMinus);
        grammarStrings.add("T → T - F");

        Production tToF = new Production();
        tToF.addSymbol(F);
        T.addProduction(tToF);
        grammarStrings.add("T → F");

        // Setup productions for F
        // F → ( E ) | id
        Production fParen = new Production();
        fParen.addSymbol(grammar.getTerminal("("));
        fParen.addSymbol(E);
        fParen.addSymbol(grammar.getTerminal(")"));
        F.addProduction(fParen);
        grammarStrings.add("F → ( E )");

        Production fId = new Production();
        fId.addSymbol(grammar.getTerminal("id"));
        F.addProduction(fId);
        grammarStrings.add("F → id");

        // Set start symbol
        grammar.setStartSymbol(E);
    }

    private void processFirstAndFollow() {
        firstFollow = new FirstFollow(grammar);
        firstFollow.calculateFirstFollow();
        firstFollow.resolveFollowDependencies();
    }

    public List<String> getGrammarString() {
        return grammarStrings;
    }

    public String getFirstSets() {
        return firstFollow.printFirstSets();
    }

    public String getFollowSets() {
        return firstFollow.printFollowSets();
    }
} 