package compiladores.GCOD.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ParserController {

    private final ParserService parserService;

    @Autowired
    public ParserController(ParserService parserService) {
        this.parserService = parserService;
    }

    @GetMapping("/parser")
    public String parserForm(Model model) {
        model.addAttribute("input", "");
        model.addAttribute("showResults", false);
        return "parser";
    }

    @PostMapping("/parser")
    public String parseInput(@RequestParam("input") String input, Model model) {
        ParserService.ParserResult result = parserService.parse(input);
        
        model.addAttribute("input", input);
        model.addAttribute("syntaxTree", result.getSyntaxTree());
        model.addAttribute("errors", result.getErrors());
        model.addAttribute("success", result.isSuccess());
        model.addAttribute("showResults", true);
        
        return "parser";
    }
} 