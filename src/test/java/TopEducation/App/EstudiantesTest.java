package TopEducation.App;

import TopEducation.App.entities.EstudiantesEntity;
import TopEducation.App.services.EstudiantesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EstudiantesTest {

    @Autowired
    private EstudiantesService estudiantesService;

    @Test
    void contextLoads() {

        EstudiantesEntity estudiante = new EstudiantesEntity();

        estudiante.setApellidos("Ramirez Baeza");
        estudiante.setNombres("Elvio Camba");
        estudiante.setRut("20.222.333-6");
        estudiante.setNom_cole("Algo");

        estudiantesService.guardarEstudiantes(estudiante);
    }
}
