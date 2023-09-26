package TopEducation.App.controllers;

import TopEducation.App.entities.CuotaEntity;
import TopEducation.App.services.CuotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

@Controller
@RequestMapping
public class CuotaController {
    @Autowired
    CuotaService cuotaService;

    @GetMapping("/PedirRutEstudiante")
    public String PedirRutParaCuotas() {
        return "CuotasFormularioRut";
    }

    @GetMapping("/BuscarCuotasPorRut/{Rut}")
    public String ListaCuotasPorRutAlumno(@RequestParam("rut") String rut, Model model) {
        /*Obtención de cuotas*/
        ArrayList<CuotaEntity> cuotas = cuotaService.ObtenerCuotasPorRutEstudiante(rut);

        /*Verificación de salida*/
        if(cuotas.isEmpty()){
            /*Mensaje de usuario inexistente*/
            model.addAttribute("mensaje","No existen cuotas asociadas a ese Rut.");
            return "CuotasFormularioRut";
        }
        else if(cuotas.get(0).getMeses_atra() == -1){
            /*Mensaje de usuario inexistente*/
            model.addAttribute("mensaje","No existe estudiante asociado a ese Rut.");
            return "CuotasFormularioRut";
        }

        model.addAttribute("cuotas", cuotas);
        model.addAttribute("rutIngresado", rut);
        return "ListaCuotasEstudiante";
    }

    @GetMapping("/DetalleCuota/{idCuota}")
    public String detalleCuota(@PathVariable("idCuota") Long idCuota, Model model) {
        /*Búsqueda de Cuota especifica*/
        CuotaEntity cuota = cuotaService.BuscarPorID(idCuota);

        // Agrega la cuota al modelo para que se pueda mostrar en la vista
        model.addAttribute("cuota", cuota);

        return "DetalleCuotaIndividual";
    }

    @GetMapping("/RegistrarPagoCuota/{idCuota}")
    public String ActualizarCuotaFormulario(@PathVariable("idCuota") Long idCuota, Model model) {
        /*Búsqueda de Cuota especificada*/
        CuotaEntity cuota = cuotaService.RegistrarEstadoDePagoCuota(idCuota);

        /*Agrega la cuota al modelo para que se pueda mostrar en la vista*/
        model.addAttribute("cuota", cuota);

        return "DetalleCuotaIndividual";
    }
}
