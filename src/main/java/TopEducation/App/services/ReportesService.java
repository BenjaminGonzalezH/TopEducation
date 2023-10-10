package TopEducation.App.services;

import TopEducation.App.entities.CuotaEntity;
import TopEducation.App.entities.EstudiantesEntity;
import TopEducation.App.entities.PruebaEntity;
import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


@Service
public class ReportesService {
    @Autowired
    CuotaService cuotaService;

    @Autowired
    EstudiantesService estudiantesService;

    @Autowired
    PruebaService pruebaService;

    public ResponseEntity<byte[]> ArchivoPlannillaAranceles()
    {
        /*Variables internas*/
        ArrayList<EstudiantesEntity> Estudiantes;       //Lista con todos los estudiantes.
        String RutEstudiante;                           //Rut de un estudiante;
        String TipoPago;                                //Tipo de pago de las cuotas del estudiante.
        String DescuentoPorTipoColegio = "0%";          //Descuento por tipo de colegio.
        String DescuentoPorAniosEgreso = "0%";          //Descuento por años de egreso.
        String DescuentoPorPruebas = "0%";              //Descuento por promedio de pruebas.
        String Total;
        Integer Promedio;                               //Promedio de pruebas del estudiante.
        ArrayList<CuotaEntity> AuxCuotasEstudiantes;    //Lista de cuotas de un estudiante.
        ArrayList<PruebaEntity> AuxPruebasEstudiantes;  //Lista de cuotas de un estudiante.
        ArrayList<String[]> data = new ArrayList<>();   //Estructura que guarda todos los datos.
        int i = 0;                                      //Contador para recorrer listas.

        /*Columnas que se van a considerar*/
        data.add(new String[]{"Rut", "Valor Nominal", "Tipo de pago", "Descuento Colegio",
                "Descuento egreso", "Descuento pruebas", "Descuento Tipo Pago", "Total a pagar"});

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        /*Se buscan a todos los estudiantes*/
        Estudiantes = estudiantesService.BuscarTodosEstudiantes();

        /*Se realiza un proceso de añadido de datos*/
        while (i < Estudiantes.size()) {
            //Se saca Rut de estudiante en posición i.
            RutEstudiante = Estudiantes.get(i).getRut();

            //Obtención de listas de cuotas y pruebas por rut de estudiante.
            AuxCuotasEstudiantes = cuotaService.ObtenerCuotasPorRutEstudiante(RutEstudiante);
            AuxPruebasEstudiantes = pruebaService.ObtenerPruebasPorRutEstudiante(RutEstudiante);

            if(AuxCuotasEstudiantes.size() > 1) {
                //Se establece número tipo de pago.
                TipoPago = AuxCuotasEstudiantes.get(1).getTipo_pag();

                //Descuentos por tipo de pago
                if (TipoPago.equals("Contado")) {
                    data.add(new String[]{RutEstudiante, "1500000", TipoPago, "0%",
                            "0%", "0%", "50%", "750000"});
                } else {
                    //Descuento por tipo de colegio.
                    switch (Estudiantes.get(i).getTipo_cole()) {
                        case "Municipal" -> DescuentoPorTipoColegio = "20%";
                        case "Subvencionado" -> DescuentoPorTipoColegio = "10%";
                        default -> {
                        }
                    }

                    //Descuento por años de egreso.
                    if (Estudiantes.get(i).getAnio_egre() == 0) {
                        DescuentoPorAniosEgreso = "15%";
                    } else if (Estudiantes.get(i).getAnio_egre() <= 2) {
                        DescuentoPorAniosEgreso = "8%";
                    } else if (Estudiantes.get(i).getAnio_egre() <= 4) {
                        DescuentoPorAniosEgreso = "4%";
                    }

                    //Descuento por promedio de pruebas.
                    Promedio = pruebaService.PromediosPruebasEstudiante(AuxPruebasEstudiantes);
                    if (Promedio >= 950) {
                        DescuentoPorPruebas = "10%";
                    } else if (Promedio >= 900) {
                        DescuentoPorPruebas = "5%";
                    } else if (Promedio >= 850) {
                        DescuentoPorPruebas = "2%";
                    }

                    //Total a pagar.
                    Total = String.valueOf((AuxCuotasEstudiantes.get(1).getMonto_pagado())
                            * (AuxCuotasEstudiantes.size()-1));
                    //Se añaden datos al string.
                    data.add(new String[]{RutEstudiante, "1500000", TipoPago, DescuentoPorTipoColegio,
                            DescuentoPorAniosEgreso, DescuentoPorPruebas, "0%", Total});
                }
            }

            //Incremento.
            i++;
        }

        /*Después de obtención de datos se hace escritura de un CSV*/
        // Escribe los datos en un archivo CSV
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(outputStream))) {
            writer.writeAll(data);
        } catch (IOException e) {
            e.printStackTrace();
            // Manejo de errores aquí
        }

        // Configura las cabeceras de respuesta para la descarga
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "PlanillaAranceles.csv");

        return ResponseEntity.ok().headers(headers).contentLength(outputStream.size()).body(outputStream.toByteArray());
    }

    public void ResumenEstadoDePagos(){
        /*Variables internas*/
        ArrayList<EstudiantesEntity> Estudiantes;       //Lista con todos los estudiantes.
        
    }
}