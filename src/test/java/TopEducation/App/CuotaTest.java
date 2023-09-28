package TopEducation.App;

import TopEducation.App.entities.CuotaEntity;
import TopEducation.App.entities.EstudiantesEntity;
import TopEducation.App.repositories.EstudiantesRepository;
import TopEducation.App.services.CuotaService;
import TopEducation.App.services.EstudiantesService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;

@SpringBootTest
public class CuotaTest {

    @Autowired
    private CuotaService cuotaService;

    @Autowired
    private EstudiantesService estudiantesService;

    @Test
    void ListarCuotas_RutNoExiste() {
        //Elementos Internos.
        ArrayList<CuotaEntity> cuotas;

        /*Se establece un rut falso para la busqueda de cuotas*/
        /*Se seleccionó ese String porque se cree que será dificil
        * que una persona de Chile tenga un Rut así.*/
        cuotas = cuotaService.ObtenerCuotasPorRutEstudiante(".........");

        /*Se establece modelo de cuota cuando usuario no existe*/
        assertNull(cuotas.get(0).getId_cuota());
        assertNull(cuotas.get(0).getId_estudiante());
        assertNull(cuotas.get(0).getMonto_primario());
        assertNull(cuotas.get(0).getTipo_pag());
        assertNull(cuotas.get(0).getEstado());
        assertNull(cuotas.get(0).getMonto_pagado());
        assertNull(cuotas.get(0).getFecha_crea());
        assertNull(cuotas.get(0).getFecha_pago());
        assertEquals(cuotas.get(0).getMeses_atra(),-1,0);
    }

    @Test
    void ListarCuotas_CuotasNoExisten() {
        //Elementos Internos.
        ArrayList<CuotaEntity> cuotas;
        EstudiantesEntity estudiante = new EstudiantesEntity();   //Estudiante de prueba.

        /*Se genera estudiante de prueba Dummy (esto para evitar errores)*/
        estudiante.setRut("prueba");
        estudiante.setApellidos("Ramirez Baeza");
        estudiante.setNombres("Elvio Camba");
        estudiante.setFecha_nac(new Date());
        estudiante.setTipo_cole("Privado");
        estudiante.setNom_cole("Weston Academy");
        estudiante.setAnio_egre(4);

        /*Se guarda estudiante*/
        estudiantesService.guardarEstudiantes(estudiante);

        /*Se establece un rut existente para la busqueda de cuotas*/
        cuotas = cuotaService.ObtenerCuotasPorRutEstudiante("prueba");

        /*Se verifica lista vacia*/
        assertEquals(cuotas.isEmpty(),true);
    }
    
}
