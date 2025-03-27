package compiladores.GCOD;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
public class GcodApplication {

  public static void main(String[] args) {
    SpringApplication.run(GcodApplication.class, args);
  }

  @GetMapping("/")
  public String index() {
    return "index";
  }
}
