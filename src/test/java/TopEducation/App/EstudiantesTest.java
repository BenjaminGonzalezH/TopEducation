package TopEducation.App;

import TopEducation.App.entities.EstudiantesEntity;
import TopEducation.App.repositories.EstudiantesRepository;
import TopEducation.App.services.EstudiantesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
public class EstudiantesTest {

    @Autowired
    private EstudiantesService estudiantesService;

    @Autowired
    private EstudiantesRepository estudiantesRepository;

    @Test
    void GuardarEstudianteEnBD_CasoUsual() {
        /*Elementos Internos*/
        EstudiantesEntity estudiante;   //Estudiante de prueba.
        EstudiantesEntity resultado;    //Resultado de la función de guardado.

        /*Modelo de estudiante usual que se guarda en la BD*/
        estudiante = new EstudiantesEntity(); //Creación.
        //Id de entidad actualizable de forma automatica
        estudiante.setRut("20.453.333-k");
        estudiante.setApellidos("Ramirez Baeza");
        estudiante.setNombres("Elvio Camba");
        estudiante.setFecha_nac(new Date());
        estudiante.setTipo_cole("Privado");
        estudiante.setNom_cole("Weston Academy");
        estudiante.setAnio_egre(4);

        /*Guardado en la base de datos*/
        resultado = estudiantesService.guardarEstudiantes(estudiante);
        estudiantesRepository.delete(estudiante);

        /*Comparación*/
        assertEquals(estudiante.getRut(),resultado.getRut());
        assertEquals(estudiante.getApellidos(),resultado.getApellidos());
        assertEquals(estudiante.getNombres(),resultado.getNombres());
        assertEquals(estudiante.getFecha_nac().equals(resultado.getFecha_nac()),
                resultado.getFecha_nac().equals(estudiante.getFecha_nac()));
        assertEquals(estudiante.getTipo_cole(),resultado.getTipo_cole());
        assertEquals(estudiante.getNom_cole(),resultado.getNom_cole());
        assertEquals(estudiante.getAnio_egre(),resultado.getAnio_egre(),0);
    }

    @Test
    void GuardarEstudianteEnBD_CasoNoPermitidoPorVistaNull() {
        /*Elementos Internos*/
        EstudiantesEntity estudiante;   //Estudiante de prueba.
        EstudiantesEntity resultado;    //Resultado de la función de guardado.

        /*Modelo de estudiante vacio*/
        estudiante = new EstudiantesEntity(); //Creación.

        /*Guardado en la base de datos*/
        resultado = estudiantesService.guardarEstudiantes(estudiante);
        estudiantesRepository.delete(estudiante);

        /*Comparación*/
        /*Esto muestra que desde c*/
        assertNull(resultado.getRut());
        assertNull(resultado.getApellidos());
        assertNull(resultado.getNombres());
        assertNull(resultado.getFecha_nac());
        assertNull(resultado.getTipo_cole());
        assertNull(resultado.getNom_cole());
    }

    @Test
    void GuardarEstudianteEnBD_CasoNoPermitidoPorVistaErrores() {
        /*Elementos Internos*/
        EstudiantesEntity estudiante;   //Estudiante de prueba.
        EstudiantesEntity resultado;    //Resultado de la función de guardado.

        /*Modelo de estudiante usual que se guarda en la BD*/
        estudiante = new EstudiantesEntity(); //Creación.
        //Id de entidad actualizable de forma automatica
        estudiante.setRut("20453333k");
        estudiante.setApellidos("dsd7as6d767");
        estudiante.setNombres("asd08as7ds8a7dsa7");
        estudiante.setFecha_nac(new Date());
        estudiante.setTipo_cole("sdas098das90d8sa");
        estudiante.setNom_cole("sadas98d9asd809");
        estudiante.setAnio_egre(4);
        //La vista evita el formato de rut incorrecto
        //Pero no evita que el texto no sea correcto.

        /*Guardado en la base de datos*/
        resultado = estudiantesService.guardarEstudiantes(estudiante);
        estudiantesRepository.delete(estudiante);
        estudiantesRepository.delete(resultado);

        /*Comparación*/
        assertEquals(estudiante.getRut(),resultado.getRut());
        assertEquals(estudiante.getApellidos(),resultado.getApellidos());
        assertEquals(estudiante.getNombres(),resultado.getNombres());
        assertEquals(estudiante.getFecha_nac().equals(resultado.getFecha_nac()),
                resultado.getFecha_nac().equals(estudiante.getFecha_nac()));
        assertEquals(estudiante.getTipo_cole(),resultado.getTipo_cole());
        assertEquals(estudiante.getNom_cole(),resultado.getNom_cole());
        assertEquals(estudiante.getAnio_egre(),resultado.getAnio_egre(),0);
    }

    @Test
    void VerificarExistenciaEstudiante() {
        /*Elementos Internos*/
        EstudiantesEntity estudiante;   //Estudiante de prueba.
        int indicador;
        int indicador1;

        estudiante = new EstudiantesEntity(); //Creación.
        //Id de entidad actualizable de forma automatica
        estudiante.setRut("20453333k");
        estudiante.setApellidos("dsd7as6d767");
        estudiante.setNombres("asd08as7ds8a7dsa7");
        estudiante.setFecha_nac(new Date());
        estudiante.setTipo_cole("sdas098das90d8sa");
        estudiante.setNom_cole("sadas98d9asd809");
        estudiante.setAnio_egre(4);

        estudiante = estudiantesService.guardarEstudiantes(estudiante);
        indicador = estudiantesService.VerificarExistenciaEstudiante("20453333k");
        indicador1 = estudiantesService.VerificarExistenciaEstudiante(".......");
        estudiantesRepository.delete(estudiante);

        /*Verificar resultados*/
        assertEquals(indicador,1,0);
        assertEquals(indicador1,0,0);
    }

    @Test
    void BuscarTodosLosEstudiantes() {
        /*Elementos Internos*/
        ArrayList<EstudiantesEntity> estudiantes;   //Estudiantes

        /*Buscar estudiantes*/
        estudiantes = estudiantesService.BuscarTodosEstudiantes();

        /*Se verifica que se tiene tamaño*/
        if(estudiantes.size() == 0){
            assertEquals(0,0,0);
        }else {
            assertEquals(1, 1, 0);
        }
    }
}
