package TopEducation.App.controllers;

import TopEducation.App.entities.CuotaEntity;
import TopEducation.App.services.CuotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String RegistrarPagoCuota(@PathVariable("idCuota") Long idCuota, Model model) {
        /*Búsqueda de Cuota especificada*/
        CuotaEntity cuota = cuotaService.RegistrarEstadoDePagoCuota(idCuota);

        /*Agrega la cuota al modelo para que se pueda mostrar en la vista*/
        model.addAttribute("cuota", cuota);

        return "DetalleCuotaIndividual";
    }

    @GetMapping("/GenerarCuotas")
    public String GenerarCuotasFormulario() {

        return "GenerarCuotasFormulario";
    }

    @PostMapping("/GuardarCuotas")
    public String GenerarCuotas(@RequestParam("rut") String rut,
                                @RequestParam("cant_cuotas") Integer cantCuotas,
                                @RequestParam("tipo_pago") String TipoPago,
                                Model model) {
        /*Cuota de error*/
        ArrayList<CuotaEntity> Error;

        /*Se guardan Cuotas*/
        Error = cuotaService.GenerarCuotasDeEstudiante(rut,cantCuotas,TipoPago);

        /*Mensajes de error*/
        if(Error.get(0).getMeses_atra() == -1){
            model.addAttribute("mensaje","Pago al contado es unico");
            return "welcome";
        }
        else if(Error.get(0).getMeses_atra() == -2){
            model.addAttribute("mensaje","Ya hay cuotas asociadas al rut");
            return "welcome";
        }
        else if(Error.get(0).getMeses_atra() == -3){
            model.addAttribute("mensaje","Un alumno de un colegio municipal solo " +
                    "opta a máximo 10 cuotas");
            return "welcome";
        }
        else if(Error.get(0).getMeses_atra() == -4){
            model.addAttribute("mensaje","Un alumno de un colegio subvencionado solo " +
                    "opta a máximo 7 cuotas");
            return "welcome";
        }
        else if(Error.get(0).getMeses_atra() == -5){
            model.addAttribute("mensaje","Un alumno de un colegio privado solo " +
                    "opta a máximo 4 cuotas");
            return "welcome";
        }

        /*Cuotas generadas satisfactoriamente*/
        model.addAttribute("mensaje","Cuotas generadas satisfactoriamente.");

        return "welcome";
    }
}
