package TopEducation.App.services;

import TopEducation.App.entities.EstudiantesEntity;
import TopEducation.App.entities.PruebaEntity;
import TopEducation.App.repositories.EstudiantesRepository;
import TopEducation.App.repositories.PruebaRepository;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Service
public class PruebaService {

    @Autowired
    private PruebaRepository pruebaRepository;

    @Autowired
    private EstudiantesRepository estudiantesRepository;

    private final Logger logg = LoggerFactory.getLogger(PruebaService.class);

    public ArrayList<PruebaEntity> ObtenerDatosPruebas(){
        return (ArrayList<PruebaEntity>) pruebaRepository.findAll();
    }

    @Generated
    public String GuardarNombreArchivo(MultipartFile file){
        String filename = file.getOriginalFilename();
        if(filename != null){
            if(!file.isEmpty()){
                try{
                    byte [] bytes = file.getBytes();
                    Path path  = Paths.get(file.getOriginalFilename());
                    Files.write(path, bytes);
                    logg.info("Archivo guardado");
                }
                catch (IOException e){
                    logg.error("ERROR", e);
                }
            }
            return "Archivo guardado con exito!";
        }
        else{
            return "No se pudo guardar el archivo";
        }
    }

    @Generated
    public void LeerArchivoCsv(String direccion){
        String texto = "";
        BufferedReader bf = null;
        pruebaRepository.deleteAll();
        try{
            bf = new BufferedReader(new FileReader(direccion));
            String temp = "";
            String bfRead;
            int count = 1;
            while((bfRead = bf.readLine()) != null){
                if (count == 1){
                    count = 0;
                }
                else{
                    GuardarPruebaEnBD(bfRead.split(";")[0], bfRead.split(";")[1], bfRead.split(";")[2]);
                    temp = temp + "\n" + bfRead;
                }
            }
            texto = temp;
            System.out.println("Archivo leido exitosamente");
        }catch(Exception e){
            System.err.println("No se encontro el archivo");
        }finally{
            if(bf != null){
                try{
                    bf.close();
                }catch(IOException e){
                    logg.error("ERROR", e);
                }
            }
        }
    }

    public void GuardarPruebaEnBD(String Rut_Estudiante, String Puntaje, String Fecha_Realizacion) {
        /*Entidad a Guardar*/
        PruebaEntity Prueba = new PruebaEntity();
        EstudiantesEntity Estudiante;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        /*Se busca estudiante*/
        Estudiante = estudiantesRepository.findByRut(Rut_Estudiante);

        /*Caso de que estudiante no exista se detiene proceso*/
        if (Estudiante == null) {
            return;
        }

        /*Inicialización*/
        Prueba.setId_estudiante(Estudiante.getId_estudiante());
        /*Caso en que no haya registro de puntaje*/
        if(Puntaje == ""){
            Prueba.setPuntaje(150);
        }
        /*Caso de puntaje que sobrepasa los valores de la PAES*/
        else if(Integer.parseInt(Puntaje) < 150 || Integer.parseInt(Puntaje) > 1000){
            Prueba.setPuntaje(150);
        }
        else {
            Prueba.setPuntaje(Integer.parseInt(Puntaje));
        }
        Prueba.setFecha_reali(LocalDate.parse(Fecha_Realizacion,formatter));
        Prueba.setFecha_resul(LocalDate.now());

        /*Se guarda Entidad de Prueba*/
        pruebaRepository.save(Prueba);
    }

    public void EliminarPruebas(ArrayList<PruebaEntity> Pruebas){
        pruebaRepository.deleteAll(Pruebas);
    }
}
