package TopEducation.App.services;

import TopEducation.App.entities.EstudiantesEntity;
import TopEducation.App.entities.PruebaEntity;
import TopEducation.App.repositories.EstudiantesRepository;
import TopEducation.App.repositories.PruebaRepository;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    @Generated
    public ArrayList<PruebaEntity> ObtenerDatosPruebas(){
        return (ArrayList<PruebaEntity>) pruebaRepository.findAll();
    }

    @Generated
    public String VerificarArchivo(String nombreArchivo) {
        /*Verificar existencia*/
        try {
            File archivo = new File(nombreArchivo);
            if (!archivo.exists()) {
                return "El archivo no existe";
            }

            // Abre el archivo para lectura
            FileReader fileReader = new FileReader(archivo);
            CSVParser parser = new CSVParserBuilder()
                    .withSeparator(';') // Especifica el punto y coma como delimitador
                    .build();
            CSVReader csvReader = new CSVReaderBuilder(fileReader)
                    .withCSVParser(parser)
                    .build();

            // Lee las líneas del archivo CSV
            String[] nextLine;
            csvReader.readNext(); //Omitir primera linea.
            while ((nextLine = csvReader.readNext()) != null) {
                // Verificar si cada línea tiene tres columnas
                if (nextLine.length != 3) {
                    return "El archivo debe poseer 3 columnas: Rut, puntaje, fecha";
                }

                // Realizar validación específica para cada columna (por ejemplo, validar el formato del rut o la fecha)
                String puntaje = nextLine[1];
                String fecha = nextLine[2];

                /*Se verifica que puntaje sea un número*/
                if (!puntaje.matches("^-?[0-9]+$")) {
                    return "Puntaje debe ser un número";
                }

                /*Se verifica que fecha siga el formato de fecha*/
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    dateFormat.parse(fecha);
                } catch (ParseException e) {
                    return "El campo 'fecha' no tiene un formato de fecha válido (dd-MM-yyyy).";
                }
            }

            // Cierra el archivo después de leerlo
            fileReader.close();

            /* Si cumple el formato se entrega una cadena vacía */
            return "";
        } catch (IOException | CsvValidationException e) {
            return "Error al intentar procesar archivo";
        }
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
    public void LeerArchivoCsv(String nombreArchivo){
        try {
            File archivo = new File(nombreArchivo);

            // Abre el archivo para lectura
            FileReader fileReader = new FileReader(archivo);
            CSVParser parser = new CSVParserBuilder()
                    .withSeparator(';') // Especifica el punto y coma como delimitador
                    .build();
            CSVReader csvReader = new CSVReaderBuilder(fileReader)
                    .withCSVParser(parser)
                    .build();

            String[] nextLine;
            csvReader.readNext(); //Omitir primera linea.
            while ((nextLine = csvReader.readNext()) != null) {
                String rut = nextLine[0];
                String puntaje = nextLine[1];
                String fecha = nextLine[2];

                GuardarPruebaEnBD(rut,puntaje,fecha);
            }

            // Cierra el archivo después de leerlo
            fileReader.close();

        }catch (IOException | CsvValidationException e) {
            return;
        }

    }

    @Generated
    public void GuardarPruebaEnBD(String Rut_Estudiante, String Puntaje, String Fecha_Realizacion) {
        /*Entidad a Guardar*/
        PruebaEntity Prueba = new PruebaEntity();
        EstudiantesEntity Estudiante;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

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

    @Generated
    public void EliminarPruebas(ArrayList<PruebaEntity> Pruebas){
        pruebaRepository.deleteAll(Pruebas);
    }


    public ArrayList<PruebaEntity> ObtenerPruebasPorRutEstudiante(String Rut) {
        /*Busqueda de ID de estudiante*/
        EstudiantesEntity estudiante = estudiantesRepository.findByRut(Rut);

        /*Se verifica que el estudiante exista*/
        if(estudiante == null){
            /*Se crea estructura con 1 elemento*/
            ArrayList<PruebaEntity> listafinal = new ArrayList<PruebaEntity>();
            PruebaEntity Prueba = new PruebaEntity();
            Prueba.setPuntaje(-1);
            listafinal.add(Prueba);

            return listafinal;
        }
        else {
            /*Busqueda de conjunto de pruebas por por id estudiante*/
            return pruebaRepository.findAllByEstudianteId(estudiante.getId_estudiante());
        }
    }


    public Integer PromediosPruebasEstudiante(ArrayList<PruebaEntity> Pruebas){
        /*Variables internas*/
        int i = 0;              //Contador de recorrido.
        Integer Suma = 0;       //Suma de puntajes.

        if (Pruebas.size() > 0) {
            while (i < Pruebas.size()) {
                Suma = Suma + Pruebas.get(i).getPuntaje();
                i++;
            }
            return (Suma / Pruebas.size());
        }
        else{
            return 0;
        }
    }
}
