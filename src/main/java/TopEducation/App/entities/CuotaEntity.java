package TopEducation.App.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "cuotas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuotaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id_cuota;

    private Long id_estudiante;
    private Float monto_primario;
    private String tipo_pag;
    private String estado;
    private Float monto_pagado;
    private LocalDate fecha_crea;
    private LocalDate fecha_pago;
    private Integer meses_atra;
}
