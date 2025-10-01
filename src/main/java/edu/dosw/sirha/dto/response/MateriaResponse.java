package edu.dosw.sirha.dto.response;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class MateriaResponse {
    String id;
    String mnemonico;
    String nombre;
    int creditos;
    int horasPresenciales;
    int horasIndependientes;
    int nivel;
    boolean laboratorio;
    String facultadId;
    List<String> prerequisitos;
    List<String> desbloquea;
    boolean activo;
    List<String> searchTerms;
}