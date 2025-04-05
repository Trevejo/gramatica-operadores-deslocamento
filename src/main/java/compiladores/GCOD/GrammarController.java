package compiladores.GCOD;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GrammarController {

    private final GrammarService grammarService;

    @Autowired
    public GrammarController(GrammarService grammarService) {
        this.grammarService = grammarService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("grammar", grammarService.getGrammarString());
        model.addAttribute("firstSets", grammarService.getFirstSets());
        model.addAttribute("followSets", grammarService.getFollowSets());
        model.addAttribute("showParserLink", true);
        return "index";
    }
} 