package TopEducation.App;


import TopEducation.App.entities.EstudiantesEntity;
import TopEducation.App.entities.PruebaEntity;
import TopEducation.App.repositories.EstudiantesRepository;
import TopEducation.App.services.EstudiantesService;
import TopEducation.App.services.PruebaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PruebaTest {
    @Autowired
    private EstudiantesRepository estudiantesRepository;

    @Autowired
    private EstudiantesService estudiantesService;

    @Autowired
    private PruebaService pruebaService;

    @Test
    void ObtenerPruebasEstudianteRutExiste() {
        /*Elementos Internos*/
        EstudiantesEntity estudiante;   //Estudiante de prueba.
        ArrayList<PruebaEntity> Pruebas;    //Pruebas

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
        estudiantesService.guardarEstudiantes(estudiante);
        Pruebas = pruebaService.ObtenerPruebasPorRutEstudiante(estudiante.getRut());
        estudiantesRepository.delete(estudiante);

        /*Verificar resultado*/
        assertEquals(Pruebas.size(),0,0);
    }

    @Test
    void ObtenerPruebasEstudianteRutNoExiste() {
        /*Elementos Internos*/
        ArrayList<PruebaEntity> Pruebas;    //Pruebas

        Pruebas = pruebaService.ObtenerPruebasPorRutEstudiante("..........");

        assertEquals(Pruebas.size(),1,0);
    }

    @Test
    void PromedioPruebas() {
        /*Elementos internos*/
        ArrayList<PruebaEntity> Pruebas = new ArrayList<>();    //Pruebas
        PruebaEntity ElementoAuxiliar = new PruebaEntity();
        Integer PromedioListaVacia;
        Integer PromedioLista1Elemento;

        //Se calcula promedio con lista vacia.
        PromedioListaVacia = pruebaService.PromediosPruebasEstudiante(Pruebas);

        //Se genera añade elemto dummy.
        ElementoAuxiliar.setPuntaje(100);
        Pruebas.add(ElementoAuxiliar);

        //Se calcula promedio con 1 elemento.
        PromedioLista1Elemento = pruebaService.PromediosPruebasEstudiante(Pruebas);

        /*Validar resultados*/
        assertEquals(PromedioListaVacia,0,0);
        assertEquals(PromedioLista1Elemento,100,0);
    }
}
