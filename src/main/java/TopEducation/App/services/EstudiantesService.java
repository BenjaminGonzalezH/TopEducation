package TopEducation.App.services;

import TopEducation.App.entities.EstudiantesEntity;
import TopEducation.App.repositories.EstudiantesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class EstudiantesService {
    @Autowired
    EstudiantesRepository EstudiantesRepository;

    public EstudiantesEntity guardarEstudiantes(EstudiantesEntity estudiante){
        return EstudiantesRepository.save(estudiante);
    }

    public int VerificarExistenciaEstudiante(String Rut){
        //Se comprueba si no existe algun estudiante con el rut dado.
        if(EstudiantesRepository.findByRut(Rut) == null){
            //Se retorna 0 si no hay alumno con ese rut.
            return 0;
        }
        else{
            //En caso contrario se retorna 1.
            return 1;
        }
    }

    public ArrayList<EstudiantesEntity> BuscarTodosEstudiantes(){
        return (ArrayList<EstudiantesEntity>) EstudiantesRepository.findAll();
    }
}
