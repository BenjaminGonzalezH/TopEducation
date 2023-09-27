package TopEducation.App;

import TopEducation.App.entities.EstudiantesEntity;
import TopEducation.App.services.EstudiantesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.util.Date;

@SpringBootTest
public class EstudiantesTest {

    @Autowired
    private EstudiantesService estudiantesService;

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
        resultado = new EstudiantesEntity();
        resultado = estudiantesService.guardarEstudiantes(estudiante);

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
        resultado = new EstudiantesEntity();
        resultado = estudiantesService.guardarEstudiantes(estudiante);

        /*Comparación*/
        /*Esto muestra que desde c*/
        assertEquals(resultado.getRut(),null);
        assertEquals(resultado.getApellidos(),null);
        assertEquals(resultado.getNombres(),null);
        assertEquals(resultado.getFecha_nac(),null);
        assertEquals(resultado.getTipo_cole(),null);
        assertEquals(resultado.getNom_cole(), null);
        assertNull(resultado.getFecha_nac());
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
        resultado = new EstudiantesEntity();
        resultado = estudiantesService.guardarEstudiantes(estudiante);

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
}
