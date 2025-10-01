package edu.dosw.sirha.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import edu.dosw.sirha.model.enums.Genero;
import edu.dosw.sirha.model.enums.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "usuarios")
public class User {

    @Id
    private String id;

    @NotBlank
    private String nombre;

    @Email
    @NotBlank
    @Indexed(unique = true)
    private String email;

    @NotBlank
    private String passwordHash;

    @NotNull
    private Rol rol;

    private boolean activo;

    private String codigoEstudiante;

    private Integer semestre;

    private String facultadId;

    private Genero genero;

    private SemaforoAcademico semaforo;

    @CreatedDate
    @Field("creado_en")
    private Instant creadoEn;

    @LastModifiedDate
    @Field("actualizado_en")
    private Instant actualizadoEn;

    @Field("ultimo_acceso")
    private Instant ultimoAcceso;
}