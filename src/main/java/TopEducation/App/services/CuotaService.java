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

        /*En caso contrario se actualiza cuota*/
        CuotaExistente.setEstado("Pagado");
        CuotaExistente.setFecha_pago(LocalDate.now());
        return cuotaRepository.save(CuotaExistente);
    }

    public ArrayList<CuotaEntity> GenerarCuotasDeEstudiante(String Rut, Integer Cantidad) {
        /* Se busca usuario para generar cuotas */
        EstudiantesEntity estudiante = estudiantesRepository.findByRut(Rut);

        /* Lista para almacenar las cuotas generadas */
        ArrayList<CuotaEntity> cuotasGeneradas = new ArrayList<>();

        /* Se establece modelo de cuotas */


        /* Se ingresan cuotas a la lista */
        for (int i = 0; i < Cantidad; i++) {
            CuotaEntity ModeloCuota = new CuotaEntity();
            ModeloCuota.setId_estudiante(estudiante.getId_estudiante());
            ModeloCuota.setMonto_primario((float) (1500000 / Cantidad));
            ModeloCuota.setTipo_pag("Cuotas");
            ModeloCuota.setEstado("Pendiente");
            ModeloCuota.setMonto_pagado((float) 0);
            ModeloCuota.setFecha_crea(LocalDate.now());
            ModeloCuota.setMeses_atra(0);
            cuotaRepository.save(ModeloCuota); // Guarda la cuota en la base de datos
            cuotasGeneradas.add(ModeloCuota); // Agrega la cuota a la lista de cuotas generadas
        }

        /* Se retorna la lista de cuotas generadas */
        return cuotasGeneradas;
    }
}
