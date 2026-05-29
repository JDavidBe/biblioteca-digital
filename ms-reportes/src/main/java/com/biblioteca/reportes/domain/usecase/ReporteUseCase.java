package com.biblioteca.reportes.domain.usecase;

import com.biblioteca.reportes.domain.model.RegistroActividad;
import com.biblioteca.reportes.domain.model.ResumenGeneral;
import com.biblioteca.reportes.domain.model.gateway.ActividadGateway;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class ReporteUseCase {

    private final ActividadGateway actividadGateway;

    public RegistroActividad registrarEvento(RegistroActividad actividad) {
        if (actividad.getTipoEvento() == null || actividad.getTipoEvento().isBlank())
            throw new RuntimeException("El tipo de evento es obligatorio");
        actividad.setOcurridoEn(LocalDateTime.now());
        return actividadGateway.registrar(actividad);
    }

    public List<RegistroActividad> actividadPorTipo(String tipoEvento) {
        return actividadGateway.listarPorTipo(tipoEvento);
    }

    public List<RegistroActividad> actividadPorUsuario(Long usuarioId) {
        return actividadGateway.listarPorUsuario(usuarioId);
    }

    public List<RegistroActividad> actividadEnPeriodo(LocalDateTime desde, LocalDateTime hasta) {
        if (desde == null) desde = LocalDateTime.now().minusMonths(1);
        if (hasta == null) hasta = LocalDateTime.now();
        return actividadGateway.listarEntreFechas(desde, hasta);
    }

    public List<RegistroActividad> actividadReciente(Integer limite) {
        return actividadGateway.listarRecientes(limite != null ? limite : 50);
    }

    public ResumenGeneral resumenGeneral() {
        return actividadGateway.obtenerResumen();
    }
}
