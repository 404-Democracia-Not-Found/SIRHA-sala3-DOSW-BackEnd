package edu.dosw.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "usuarios")
public class User {
    @Id
    private String id;
    
    private String nombre;
    private String email;
    
    @Field("fecha_nacimiento")
    private LocalDateTime fechaNacimiento;
    
    private String genero;
    
    @Field("pais_nacimiento")
    private String paisNacimiento;
    
    @Field("estado_civil")
    private String estadoCivil;
    
    @Field("password_hash")
    private String passwordHash;
    
    @Field("fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    private Boolean activo;
    private List<Rol> roles;
    
    @Field("search_terms")
    private List<String> searchTerms;

    public static class Rol {
        private String tipo; // ESTUDIANTE, PROFESOR, COORDINADOR, ADMIN, ADMINISTRATIVO
        private Boolean activo;
        
        @Field("fecha_asignacion")
        private LocalDateTime fechaAsignacion;
        
        @Field("datos_especificos")
        private Object datosEspecificos;
        
        public Rol() {}
        
        // Getters and Setters
        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }
        
        public Boolean getActivo() { return activo; }
        public void setActivo(Boolean activo) { this.activo = activo; }
        
        public LocalDateTime getFechaAsignacion() { return fechaAsignacion; }
        public void setFechaAsignacion(LocalDateTime fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }
        
        public Object getDatosEspecificos() { return datosEspecificos; }
        public void setDatosEspecificos(Object datosEspecificos) { this.datosEspecificos = datosEspecificos; }
    }

    public User() {
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDateTime fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getPaisNacimiento() { return paisNacimiento; }
    public void setPaisNacimiento(String paisNacimiento) { this.paisNacimiento = paisNacimiento; }

    public String getEstadoCivil() { return estadoCivil; }
    public void setEstadoCivil(String estadoCivil) { this.estadoCivil = estadoCivil; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public List<Rol> getRoles() { return roles; }
    public void setRoles(List<Rol> roles) { this.roles = roles; }

    public List<String> getSearchTerms() { return searchTerms; }
    public void setSearchTerms(List<String> searchTerms) { this.searchTerms = searchTerms; }
}