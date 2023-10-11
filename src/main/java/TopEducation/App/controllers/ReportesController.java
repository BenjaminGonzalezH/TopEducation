package TopEducation.App.controllers;

import TopEducation.App.services.ReportesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class ReportesController {

    @Autowired
    ReportesService reportesService;

    @GetMapping({"/PlanillaAranceles"})
    public ResponseEntity<byte[]> GenerarPlanilla() {
        ResponseEntity<byte[]> response = reportesService.ArchivoPlannillaAranceles();

        // Verificar si el servicio generó el archivo correctamente
        if (response.getStatusCode() == HttpStatus.OK) {
            return response;
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping({"/ReportePagos"})
    public ResponseEntity<byte[]> ResumenEstadoPagos() {
        ResponseEntity<byte[]> response = reportesService.ResumenEstadoDePagos();

        // Verificar si el servicio generó el archivo correctamente
        if (response.getStatusCode() == HttpStatus.OK) {
            return response;
        } else {
            // Manejo de errores aquí, por ejemplo, redireccionar a una página de error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
