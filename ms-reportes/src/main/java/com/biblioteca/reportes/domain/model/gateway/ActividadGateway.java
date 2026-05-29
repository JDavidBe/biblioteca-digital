package com.biblioteca.reportes.domain.model.gateway;

import com.biblioteca.reportes.domain.model.RegistroActividad;
import com.biblioteca.reportes.domain.model.ResumenGeneral;

import java.time.LocalDateTime;
import java.util.List;

public interface ActividadGateway {
    RegistroActividad registrar(RegistroActividad actividad);
    List<RegistroActividad> listarPorTipo(String tipoEvento);
    List<RegistroActividad> listarPorUsuario(Long usuarioId);
    List<RegistroActividad> listarEntreFechas(LocalDateTime desde, LocalDateTime hasta);
    List<RegistroActividad> listarRecientes(Integer limite);
    ResumenGeneral obtenerResumen();
}
