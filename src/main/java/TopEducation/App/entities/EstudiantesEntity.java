package TopEducation.App.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "estudiantes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstudiantesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id_estudiante;

    private String rut;
    private String apellidos;
    private String nombres;
    private Date fecha_nac;
    private String tipo_cole;
    private String nom_cole;
    private int anio_egre;
}
