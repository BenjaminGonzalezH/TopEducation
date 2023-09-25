package TopEducation.App.controllers;


import TopEducation.App.entities.EstudiantesEntity;
import TopEducation.App.services.EstudiantesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping("/registrar")
    public String FormularioRegistro(Model model)
    {
        model.addAttribute("estudiantes",new EstudiantesEntity());
        return "Registro";
    }

    @PostMapping("/Procesar-registro")
    public String procesarFormularioRegistro(@ModelAttribute EstudiantesEntity estudiante, Model model) {
        //Se verifica que el rut no existe previamente.
        if(estudiantesService.VerificarExistenciaEstudiante(estudiante.getRut()) == 1){
            //Si existe estudiante se va a una vista con el mensaje.
            model.addAttribute("mensaje","Ya existe un alumno con el rut escrito.");
        }
        else{
            //En caso contrario se sigue con flujo a mensaje de retroalimentación.
            estudiantesService.guardarEstudiantes(estudiante);
            model.addAttribute("mensaje", "El registro fue exitoso");
        }

        return "Welcome";
    }
}
