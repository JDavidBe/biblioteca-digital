package com.biblioteca.notificaciones.domain.usecase;

import com.biblioteca.notificaciones.domain.model.Notificacion;
import com.biblioteca.notificaciones.domain.model.gateway.EmailGateway;
import com.biblioteca.notificaciones.domain.model.gateway.NotificacionGateway;
import com.biblioteca.notificaciones.domain.model.gateway.SmsGateway;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class NotificacionUseCase {

    private final NotificacionGateway notificacionGateway;
    private final EmailGateway emailGateway;
    private final SmsGateway smsGateway;

    public Notificacion enviarNotificacion(Notificacion notificacion) {
        String canal = notificacion.getCanal() != null ? notificacion.getCanal().toUpperCase() : "EMAIL";
        notificacion.setCanal(canal);

        boolean necesitaEmail = canal.equals("EMAIL") || canal.equals("AMBOS");
        boolean necesitaSms   = canal.equals("SMS")   || canal.equals("AMBOS");

        if (necesitaEmail) {
            if (notificacion.getDestinatarioCorreo() == null || notificacion.getDestinatarioCorreo().isBlank())
                throw new RuntimeException("El correo del destinatario es obligatorio para canal EMAIL");
            if (notificacion.getAsunto() == null || notificacion.getAsunto().isBlank())
                throw new RuntimeException("El asunto es obligatorio para canal EMAIL");
        }
        if (necesitaSms) {
            if (notificacion.getDestinatarioTelefono() == null || notificacion.getDestinatarioTelefono().isBlank())
                throw new RuntimeException("El teléfono del destinatario es obligatorio para canal SMS");
        }
        if (notificacion.getMensaje() == null || notificacion.getMensaje().isBlank())
            throw new RuntimeException("El mensaje es obligatorio");
        if (notificacion.getTipo() == null || notificacion.getTipo().isBlank())
            notificacion.setTipo("GENERAL");

        notificacion.setEnviada(false);
        notificacion.setEnviadaSms(false);
        notificacion.setCreadaEn(LocalDateTime.now());
        Notificacion guardada = notificacionGateway.guardar(notificacion);

        if (necesitaEmail) {
            try {
                emailGateway.enviar(notificacion.getDestinatarioCorreo(),
                        notificacion.getAsunto(), notificacion.getMensaje());
                notificacionGateway.marcarComoEnviada(guardada.getId());
                guardada.setEnviada(true);
                guardada.setEnviadaEn(LocalDateTime.now());
            } catch (Exception e) {
                // guardada pero no enviada — reintentable
            }
        }

        if (necesitaSms) {
            try {
                smsGateway.enviar(notificacion.getDestinatarioTelefono(), notificacion.getMensaje());
                notificacionGateway.marcarSmsComotEnviado(guardada.getId());
                guardada.setEnviadaSms(true);
                if (guardada.getEnviadaEn() == null) guardada.setEnviadaEn(LocalDateTime.now());
            } catch (Exception e) {
                // guardada pero SMS no enviado — reintentable
            }
        }

        return guardada;
    }

    public Notificacion enviarBienvenida(String correo, String telefono, Long usuarioId, String nombre) {
        if ((correo == null || correo.isBlank()) && (telefono == null || telefono.isBlank()))
            throw new RuntimeException("Se requiere correo o teléfono para bienvenida");

        Notificacion n = new Notificacion();
        n.setDestinatarioCorreo(correo);
        n.setDestinatarioTelefono(telefono);
        n.setDestinatarioId(usuarioId);
        n.setTipo("BIENVENIDA");
        n.setCanal(resolverCanal(correo, telefono));
        n.setAsunto("¡Bienvenido a Biblioteca Digital!");
        n.setMensaje(String.format(
                "Hola %s, tu cuenta ha sido creada exitosamente. " +
                "Ya puedes acceder a todos los recursos educativos disponibles.", nombre));
        return enviarNotificacion(n);
    }

    public Notificacion notificarNuevoRecurso(String correo, String telefono, Long usuarioId, String tituloRecurso) {
        if (tituloRecurso == null || tituloRecurso.isBlank())
            throw new RuntimeException("El título del recurso es obligatorio");

        Notificacion n = new Notificacion();
        n.setDestinatarioCorreo(correo);
        n.setDestinatarioTelefono(telefono);
        n.setDestinatarioId(usuarioId);
        n.setTipo("NUEVO_RECURSO");
        n.setCanal(resolverCanal(correo, telefono));
        n.setAsunto("Nuevo recurso disponible: " + tituloRecurso);
        n.setMensaje(String.format(
                "Se ha publicado un nuevo recurso educativo: \"%s\". " +
                "Ya está disponible para descargar en la plataforma.", tituloRecurso));
        return enviarNotificacion(n);
    }

    public Notificacion notificarDescarga(String correo, String telefono, Long usuarioId, String tituloRecurso) {
        Notificacion n = new Notificacion();
        n.setDestinatarioCorreo(correo);
        n.setDestinatarioTelefono(telefono);
        n.setDestinatarioId(usuarioId);
        n.setTipo("NUEVA_DESCARGA");
        n.setCanal(resolverCanal(correo, telefono));
        n.setAsunto("Confirmación de descarga");
        n.setMensaje(String.format(
                "Tu descarga del recurso \"%s\" fue registrada exitosamente.", tituloRecurso));
        return enviarNotificacion(n);
    }

    public List<Notificacion> listarPorDestinatario(Long destinatarioId) {
        if (destinatarioId == null)
            throw new RuntimeException("El ID del destinatario es obligatorio");
        return notificacionGateway.listarPorDestinatario(destinatarioId);
    }

    public List<Notificacion> listarPendientes() {
        return notificacionGateway.listarPendientes();
    }

    public List<Notificacion> listarTodas() {
        return notificacionGateway.listarTodas();
    }

    public Notificacion obtenerPorId(Long id) {
        Notificacion n = notificacionGateway.buscarPorId(id);
        if (n == null)
            throw new RuntimeException("No existe notificación con id: " + id);
        return n;
    }

    private String resolverCanal(String correo, String telefono) {
        boolean tieneCorreo = correo != null && !correo.isBlank();
        boolean tieneTelefono = telefono != null && !telefono.isBlank();
        if (tieneCorreo && tieneTelefono) return "AMBOS";
        if (tieneTelefono) return "SMS";
        return "EMAIL";
    }
}
