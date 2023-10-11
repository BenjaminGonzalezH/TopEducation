package TopEducation.App.services;

import TopEducation.App.entities.CuotaEntity;
import TopEducation.App.entities.EstudiantesEntity;
import TopEducation.App.entities.PruebaEntity;
import com.opencsv.CSVWriter;
import lombok.Generated;
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

    @Generated
    public ResponseEntity<byte[]> ArchivoPlannillaAranceles()
    {
        /*Variables internas*/
        ArrayList<EstudiantesEntity> Estudiantes;       //Lista con todos los estudiantes.
        String RutEstudiante;                           //Rut de un estudiante;
        String TipoPago;                                //Tipo de pago de las cuotas del estudiante.
        String DescuentoPorTipoColegio;                 //Descuento por tipo de colegio.
        String DescuentoPorAniosEgreso;                 //Descuento por años de egreso.
        String DescuentoPorPruebas;                     //Descuento por promedio de pruebas.
        String Total;                                   //Total a pagar.
        Integer Promedio;                               //Promedio de pruebas del estudiante.
        Float descuentoPuntaje;                         //Descuento por notas aplicada a las cuotas.
        ArrayList<CuotaEntity> AuxCuotasEstudiantes;    //Lista de cuotas de un estudiante.
        ArrayList<PruebaEntity> AuxPruebasEstudiantes;  //Lista de cuotas de un estudiante.
        ArrayList<String[]> data = new ArrayList<>();   //Estructura que guarda todos los datos.
        int i = 0;                                      //Contador para recorrer listas.
        int j;                                          //Contador para recorrer listas.

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

            /*Reinicio de valores de descuento*/
            DescuentoPorTipoColegio = "0%";
            DescuentoPorAniosEgreso = "0%";
            DescuentoPorPruebas = "0%";
            descuentoPuntaje = 0.0F;

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
                        descuentoPuntaje = (float) 0.10;
                    } else if (Promedio >= 900) {
                        DescuentoPorPruebas = "5%";
                        descuentoPuntaje = (float) 0.05;
                    } else if (Promedio >= 850) {
                        DescuentoPorPruebas = "2%";
                        descuentoPuntaje = (float) 0.02;
                    }

                    //Actualización de precio de cuotas.
                    j = 0;
                    while (j < AuxCuotasEstudiantes.size()){
                        descuentoPuntaje = AuxCuotasEstudiantes.get(j).getMonto_primario()*descuentoPuntaje;
                        AuxCuotasEstudiantes.get(j).setMonto_pagado(
                                AuxCuotasEstudiantes.get(j).getMonto_pagado() - descuentoPuntaje);
                        j++;
                    }
                    cuotaService.ActualizarCuotas(AuxCuotasEstudiantes);

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
        }

        // Configura las cabeceras de respuesta para la descarga
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "PlanillaAranceles.csv");

        return ResponseEntity.ok().headers(headers).contentLength(outputStream.size()).body(outputStream.toByteArray());
    }

    @Generated
    public ResponseEntity<byte[]> ResumenEstadoDePagos(){
        /*Variables internas*/
        ArrayList<EstudiantesEntity> Estudiantes;       //Lista con todos los estudiantes.
        ArrayList<CuotaEntity> AuxCuotasEstudiantes;    //Lista de cuotas de un estudiante.
        ArrayList<PruebaEntity> AuxPruebasEstudiantes;  //Lista de cuotas de un estudiante.
        String RutEstudiante;                           //Rut de un estudiante;
        String NombreEstudiante;
        String NroExamenesRendidos;
        String NroTotalCuotas;
        String Promedio;
        String Monto;
        String TipoPago;
        String TotalPagadas;
        String MontoPagado;
        String CuotasAtrasadas;
        String FechaUltimoPago;
        String MontoPorPagar;
        Integer CuotasPagadas;
        ArrayList<String[]> data = new ArrayList<>();   //Estructura que guarda todos los datos.
        int i = 0;                                      //Contador para recorrer listas.

        /*Columnas que se van a considerar*/
        data.add(new String[]{"Rut", "Nombre de estudiante", "Nro Examenes rendidos", "Promedio puntaje exámenes",
                "Monto total arancel a pagar", "Tipo Pago (Contado/Cuotas)",
                "Nro. total de cuotas pactadas", "Nro. cuotas pagadas", "Monto total pagado",
                "Fecha último pago", "Saldo por pagar", "Nro. Cuotas con retraso"});

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        /*Se buscan a todos los estudiantes*/
        Estudiantes = estudiantesService.BuscarTodosEstudiantes();

        while(i < Estudiantes.size()){
            //Se saca Rut de estudiante en posición i.
            RutEstudiante = Estudiantes.get(i).getRut();

            /*Reinicio de valores*/
            Monto = "";
            TipoPago = "";
            TotalPagadas = "";
            MontoPagado = "";
            CuotasAtrasadas = "";
            FechaUltimoPago = "";
            MontoPorPagar = "";

            /*Cuotas y pruebas del estudiante*/
            AuxCuotasEstudiantes = cuotaService.ObtenerCuotasPorRutEstudiante(RutEstudiante);
            AuxPruebasEstudiantes = pruebaService.ObtenerPruebasPorRutEstudiante(RutEstudiante);

            /*Nombre de estudiante*/
            NombreEstudiante = Estudiantes.get(i).getApellidos() + " " +Estudiantes.get(i).getNombres();

            /*Numero total de pruebas y cuotas*/
            NroExamenesRendidos = String.valueOf(AuxPruebasEstudiantes.size());
            NroTotalCuotas = String.valueOf(AuxCuotasEstudiantes.size());

            /*Promedio notas estudiante*/
            Promedio = String.valueOf(pruebaService.PromediosPruebasEstudiante(AuxPruebasEstudiantes));

            /*Tipo Pago y monto total a pagar*/
            if(AuxCuotasEstudiantes.size() > 1) {
                //Se establece número tipo de pago.
                TipoPago = AuxCuotasEstudiantes.get(1).getTipo_pag();
                Monto = String.valueOf((AuxCuotasEstudiantes.get(1).getMonto_pagado())
                        * (AuxCuotasEstudiantes.size()-1));

                /*Conteo de cuotas Pagadas*/
                CuotasPagadas = cuotaService.ContarCuotasPagadas(AuxCuotasEstudiantes);
                TotalPagadas = String.valueOf(CuotasPagadas-1);

                /*Monto total pagado*/
                MontoPagado = String.valueOf((AuxCuotasEstudiantes.get(1).getMonto_pagado())*CuotasPagadas);

                /*Monto por pagar*/
                MontoPorPagar = String.valueOf((AuxCuotasEstudiantes.get(1).getMonto_pagado())
                        *((AuxCuotasEstudiantes.size())-CuotasPagadas));

                /*Numero de cuotas con retraso*/
                CuotasAtrasadas = String.valueOf(cuotaService.ContarCuotasAtrasadas(AuxCuotasEstudiantes));

                /*Fecha ultimo pago*/
                FechaUltimoPago = cuotaService.FechaUltimaCuotaPagada(AuxCuotasEstudiantes);
            }

            data.add(new String[]{RutEstudiante, NombreEstudiante, NroExamenesRendidos, Promedio,
                    Monto, TipoPago, NroTotalCuotas, TotalPagadas, MontoPagado,
                    FechaUltimoPago, MontoPorPagar, CuotasAtrasadas});

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
        headers.setContentDispositionFormData("attachment", "Reporte.csv");

        return ResponseEntity.ok().headers(headers).contentLength(outputStream.size()).body(outputStream.toByteArray());

    }
}