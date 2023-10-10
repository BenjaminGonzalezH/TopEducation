package TopEducation.App.repositories;

import TopEducation.App.entities.PruebaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface PruebaRepository extends JpaRepository<PruebaEntity, Long> {

    /*Buscar pruebas por id de estudiante*/
    @Query("SELECT prueba FROM PruebaEntity prueba WHERE prueba.id_estudiante = :idEstudiante")
    ArrayList<PruebaEntity> findAllByEstudianteId(@Param("idEstudiante") Long idEstudiante);
}
