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

    public ArrayList<EstudiantesEntity> obtenerEstudiantes(){
        return (ArrayList<EstudiantesEntity>) EstudiantesRepository.findAll();
    }
}
