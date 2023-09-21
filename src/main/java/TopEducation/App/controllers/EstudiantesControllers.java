package TopEducation.App.controllers;


import TopEducation.App.entities.EstudiantesEntity;
import TopEducation.App.services.EstudiantesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;

@Controller
@RequestMapping
public class EstudiantesControllers {
    @Autowired
    EstudiantesService estudiantesService;

    @GetMapping("/listar")
    public String listar(Model model) {
        ArrayList<EstudiantesEntity>estudiantes=estudiantesService.obtenerEstudiantes();
        model.addAttribute("estudiantes",estudiantes);
        return "index";
    }
}
