package TopEducation.App.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class WelcomeController {

    @GetMapping({"/welcome", "/"})
    public String PaginaWelcome(){
        return "Welcome";
    }
}
