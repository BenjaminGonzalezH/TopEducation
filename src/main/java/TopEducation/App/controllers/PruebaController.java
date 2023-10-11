package TopEducation.App.controllers;

import TopEducation.App.entities.PruebaEntity;
import TopEducation.App.services.PruebaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

@Controller
@RequestMapping
public class PruebaController {

    @Autowired
    PruebaService pruebaService;

    @GetMapping("/SubirDatosPruebas")
    public String VistaSubirDatos() {return "SubirPruebas";}

    @PostMapping("/SubirDatosPruebas")
    public String upload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

        String mensaje = pruebaService.VerificarArchivo(file);
        if(!mensaje.equals("")) {
            redirectAttributes.addFlashAttribute("mensaje", mensaje);
            return "redirect:/SubirDatosPruebas";
        }
        else {
            pruebaService.GuardarNombreArchivo(file);
            redirectAttributes.addFlashAttribute("mensaje", "¡Archivo cargado correctamente!");
            pruebaService.LeerArchivoCsv("Pruebas.csv");
            return "redirect:/SubirDatosPruebas";
        }
    }

    @GetMapping("/VerPruebas")
    public String ListarPruebas(Model model) {
        ArrayList<PruebaEntity> Pruebas = pruebaService.ObtenerDatosPruebas();
        model.addAttribute("Pruebas", Pruebas);
        return "ListarPruebas";
    }
}
