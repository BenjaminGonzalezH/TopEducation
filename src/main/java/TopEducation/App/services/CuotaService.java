package TopEducation.App.services;

import TopEducation.App.entities.CuotaEntity;
import TopEducation.App.entities.EstudiantesEntity;
import TopEducation.App.repositories.CuotaRepository;
import TopEducation.App.repositories.EstudiantesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;

@Service
public class CuotaService {
    @Autowired
    CuotaRepository cuotaRepository;
    @Autowired
    EstudiantesRepository estudiantesRepository;

    public ArrayList<CuotaEntity> ObtenerCuotasPorRutEstudiante(String Rut) {
        /*Busqueda de ID de estudiante*/
        EstudiantesEntity estudiante = estudiantesRepository.findByRut(Rut);

        /*Se verifica que el estudiante exista*/
        if(estudiante == null){
            /*Se crea estructura con 1 elemento*/
            ArrayList<CuotaEntity> listafinal = new ArrayList<CuotaEntity>();
            CuotaEntity cuotaEntity = new CuotaEntity();
            cuotaEntity.setMeses_atra(-1);
            listafinal.add(cuotaEntity);

            return listafinal;
        }
        else {
            /*Busqueda de conjunto de cuotas por por id estudiante*/
            return cuotaRepository.findAllByEstudianteId(estudiante.getId_estudiante());
        }
    }

    public CuotaEntity BuscarPorID(Long idCuota){ return cuotaRepository.findByIdCuota(idCuota);}

    public CuotaEntity RegistrarEstadoDePagoCuota(Long idCuota){
        /*Se busca cuentas existentes*/
        CuotaEntity CuotaExistente = cuotaRepository.findByIdCuota(idCuota);

        /*Se verifica que no se modifique una cuota en estado pagado*/
        if("Pagado".equals(CuotaExistente.getEstado())) { /*Cuota ya pagada*/
            return CuotaExistente;
        }

        /*Se verifica estado de la cuota*/
        if("Atrasada".equals(CuotaExistente.getEstado())) { /*Cuota atrasada*/
            /*Actualización de estado*/
            CuotaExistente.setEstado("Pagado (con atraso)");
        }
        else {
            CuotaExistente.setEstado("Pagado");
        }

        /*Cambio de fechas*/
        CuotaExistente.setFecha_crea(CuotaExistente.getFecha_pago());
        CuotaExistente.setFecha_pago(LocalDate.now());

        /*Retorno*/
        return cuotaRepository.save(CuotaExistente);
    }

    public ArrayList<CuotaEntity> GenerarCuotasDeEstudiante(String Rut, Integer Cantidad, String Tipo) {
        /*Elementos Internos*/
        EstudiantesEntity estudiante;  //Estudiante a buscar.
        CuotaEntity errorCuota = new CuotaEntity(); //Modelo de cuotas para errores.
        CuotaEntity ModeloCuota;    //Entidad que sirve como modelo de cuota.
        ArrayList<CuotaEntity> cuotasGeneradas = new ArrayList<>(); //Arreglo de salida.
        CuotaEntity Matricula; //Registro de matricula como primer pago
        float ArancelReal;

        /* Se busca usuario para generar cuotas */
        estudiante = estudiantesRepository.findByRut(Rut);

        /*Control de entrada*/
        //Existencia previa de cuotas.
        if(!cuotaRepository.findAllByEstudianteId(estudiante.getId_estudiante()).isEmpty()){
            errorCuota.setMeses_atra(-2);
            cuotasGeneradas.add(errorCuota);
            return cuotasGeneradas;
        }
        else if(Cantidad > 1 && Tipo.equals("Contado")){ //Más de una cuota al contado.
            /*Se entrega un arreglo de 1 elemento que establece el error*/
            errorCuota.setMeses_atra(-1);
            cuotasGeneradas.add(errorCuota);
            return cuotasGeneradas;
        }
        //Bloque de condiciones por número máximo de cuotas.
        else if(Cantidad > 10 && estudiante.getTipo_cole().equals("Municipal")){
            errorCuota.setMeses_atra(-3);
            cuotasGeneradas.add(errorCuota);
            return cuotasGeneradas;
        }
        else if(Cantidad > 7 && estudiante.getTipo_cole().equals("Subvencionado")){
            errorCuota.setMeses_atra(-4);
            cuotasGeneradas.add(errorCuota);
            return cuotasGeneradas;
        }
        else if(Cantidad > 4 && estudiante.getTipo_cole().equals("Privado")){
            errorCuota.setMeses_atra(-5);
            cuotasGeneradas.add(errorCuota);
            return cuotasGeneradas;
        }

        /*Se establece primera cuota como pago de matricula*/
        Matricula = new CuotaEntity();
        Matricula.setId_estudiante(estudiante.getId_estudiante());
        Matricula.setMonto_primario((float) (70000));   //Valor de matricula.
        Matricula.setTipo_pag("Contado");
        Matricula.setEstado("Pagado");
        Matricula.setMonto_pagado((float) 70000);
        Matricula.setFecha_crea(LocalDate.now());
        Matricula.setFecha_pago(LocalDate.now());
        Matricula.setMeses_atra(0);
        cuotasGeneradas.add(Matricula);
        cuotaRepository.save(Matricula);

        /*Se establecen valores de descuentos*/
        ArancelReal = (float) 1500000;

        /*Descuento por procedencia*/
        switch(estudiante.getTipo_cole()){
            case "Municipal":
                ArancelReal = ArancelReal - ((float) (1500000*0.20));
                break;

            case "Subvencionado":
                ArancelReal = ArancelReal - ((float) (1500000*0.10));
                break;

            default:
                break;
        }

        /*Descuento por años de egreso en el colegio*/
        if(estudiante.getAnio_egre() == 0){
            ArancelReal = ArancelReal -  ((float) (1500000*0.15));
        }
        else if(estudiante.getAnio_egre() <= 2){
            ArancelReal = ArancelReal -  ((float) (1500000*0.08));
        }
        else if(estudiante.getAnio_egre() <= 4){
            ArancelReal = ArancelReal -  ((float) (1500000*0.04));
        }

        /* Se ingresan cuotas a la lista */
        for (int i = 0; i < Cantidad; i++) {
            /* Se establece modelo de cuotas */
            ModeloCuota = new CuotaEntity();
            ModeloCuota.setId_estudiante(estudiante.getId_estudiante());
            ModeloCuota.setMonto_primario((float) (1500000 / Cantidad));
            ModeloCuota.setTipo_pag("Cuotas");
            ModeloCuota.setEstado("Pendiente");
            ModeloCuota.setMonto_pagado(ArancelReal/Cantidad);
            ModeloCuota.setFecha_crea(LocalDate.now());
            ModeloCuota.setMeses_atra(0);

            cuotaRepository.save(ModeloCuota); // Guarda la cuota en la base de datos
            cuotasGeneradas.add(ModeloCuota); // Agrega la cuota a la lista de cuotas generadas
        }

        /* Se retorna la lista de cuotas generadas */
        return cuotasGeneradas;
    }
}
