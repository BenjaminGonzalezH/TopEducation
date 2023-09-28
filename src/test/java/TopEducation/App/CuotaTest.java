package TopEducation.App;

import TopEducation.App.entities.CuotaEntity;
import TopEducation.App.entities.EstudiantesEntity;
import TopEducation.App.repositories.CuotaRepository;
import TopEducation.App.repositories.EstudiantesRepository;
import TopEducation.App.services.CuotaService;
import TopEducation.App.services.EstudiantesService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

@SpringBootTest
public class CuotaTest {

    @Autowired
    private CuotaService cuotaService;

    @Autowired
    private EstudiantesService estudiantesService;

    @Autowired
    private CuotaRepository cuotaRepository;

    @Autowired
    private EstudiantesRepository estudiantesRepository;

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
        estudiante.setRut("prueba2");
        estudiante.setApellidos("Ramirez Baeza");
        estudiante.setNombres("Elvio Camba");
        estudiante.setFecha_nac(new Date());
        estudiante.setTipo_cole("Privado");
        estudiante.setNom_cole("Weston Academy");
        estudiante.setAnio_egre(4);

        /*Se guarda estudiante*/
        estudiantesService.guardarEstudiantes(estudiante);

        /*Se establece un rut existente para la busqueda de cuotas*/
        cuotas = cuotaService.ObtenerCuotasPorRutEstudiante("prueba2");
        estudiantesRepository.delete(estudiante);

        /*Se verifica lista vacia*/
        assertEquals(cuotas.isEmpty(),true);
    }

    @Test
    void RegistrarCuotas_CambioAPagada() {
        //Elementos Internos.
        CuotaEntity cuota = new CuotaEntity();
        CuotaEntity resultados;

        /*Se crea una nueva entidad cuota*/
        cuota.setMonto_primario((float) 100000);
        cuota.setTipo_pag("Cuota");
        cuota.setEstado("Pendiente");
        cuota.setMonto_pagado((float) 100000);
        cuota.setFecha_crea(LocalDate.now());
        cuota.setFecha_pago(LocalDate.now());
        cuota.setMeses_atra(0);

        /*Se guarda en la base de datos*/
        cuota = cuotaRepository.save(cuota); //Sobreescritura.

        /*Se cambia su estado*/
        resultados = cuotaService.RegistrarEstadoDePagoCuota(cuota.getId_cuota());
        cuotaRepository.delete(cuota);

        /*se verifica el cambio de cuota*/
        assertEquals(resultados.getEstado(),"Pagado");
    }

    @Test
    void RegistrarCuota_CuotaYaPagada() {
        //Elementos Internos.
        CuotaEntity cuota = new CuotaEntity();
        CuotaEntity resultados;

        /*Se crea una nueva entidad cuota*/
        cuota.setMonto_primario((float) 100000);
        cuota.setTipo_pag("Cuota");
        cuota.setEstado("Pagado");
        cuota.setMonto_pagado((float) 100000);
        cuota.setFecha_crea(LocalDate.now());
        cuota.setFecha_pago(LocalDate.now());
        cuota.setMeses_atra(0);

        /*Se guarda en la base de datos*/
        cuota = cuotaRepository.save(cuota); //Sobreescritura.

        /*Se cambia su estado*/
        resultados = cuotaService.RegistrarEstadoDePagoCuota(cuota.getId_cuota());
        cuotaRepository.delete(cuota);

        /*se verifica el cambio de cuota*/
        assertEquals(resultados.getEstado(),cuota.getEstado());
    }

    @Test
    void RegistroCuota_EstadoAtrasado() {
        //Elementos Internos.
        CuotaEntity cuota = new CuotaEntity();
        CuotaEntity resultados;

        /*Se crea una nueva entidad cuota*/
        cuota.setMonto_primario((float) 100000);
        cuota.setTipo_pag("Cuota");
        cuota.setEstado("Atrasada");
        cuota.setMonto_pagado((float) 100000);
        cuota.setFecha_crea(LocalDate.now());
        cuota.setFecha_pago(LocalDate.of(2023,9,26));
        cuota.setMeses_atra(0);

        /*Se guarda en la base de datos*/
        cuota = cuotaRepository.save(cuota); //Sobreescritura.

        /*Se cambia su estado*/
        resultados = cuotaService.RegistrarEstadoDePagoCuota(cuota.getId_cuota());
        cuotaRepository.delete(cuota);

        /*Verificar estado, además de cambio de fecha*/
        assertEquals(resultados.getEstado(),"Pagado (con atraso)");
        resultados.getFecha_crea().equals(cuota.getFecha_pago());
        resultados.getFecha_pago().equals(LocalDate.now());
    }

    @Test
    void GenerarCuotas_UsoDeCeroPagoCuotas() {

    }
}
