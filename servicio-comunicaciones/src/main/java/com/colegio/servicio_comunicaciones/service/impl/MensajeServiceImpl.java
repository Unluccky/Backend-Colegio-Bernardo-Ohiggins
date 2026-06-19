package com.colegio.servicio_comunicaciones.service.impl;

import com.colegio.servicio_comunicaciones.model.Mensaje;
import com.colegio.servicio_comunicaciones.repository.MensajeRepository;
import com.colegio.servicio_comunicaciones.service.MensajeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MensajeServiceImpl implements MensajeService {

    private final MensajeRepository repo;

    @Override
    public List<Mensaje> listarTodos() {
        return repo.findAll();
    }

    @Override
    public Mensaje buscarPorId(String id) {
        return repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Mensaje no encontrado: " + id));
    }

    @Override
    public List<Mensaje> buscarPorDestinatario(Long destinatarioId) {
        return repo.findByDestinatarioId(destinatarioId);
    }

    @Override
    public List<Mensaje> buscarPorUsuario(Long usuarioId, String usuarioTipo) {
        return repo.findByUsuarioIdAndTipo(usuarioId, usuarioTipo);
    }

    @Override
    public Mensaje guardar(Mensaje mensaje) {
        if (mensaje.getFechaEnvio() == null) {
            mensaje.setFechaEnvio(LocalDateTime.now());
        }
        return repo.save(mensaje);
    }

    @Override
    public Mensaje actualizar(String id, Mensaje mensaje) {
        Mensaje existente = buscarPorId(id);
        if (mensaje.getAsunto() != null) existente.setAsunto(mensaje.getAsunto());
        if (mensaje.getContenido() != null) existente.setContenido(mensaje.getContenido());
        if (mensaje.getLeido() != null) existente.setLeido(mensaje.getLeido());
        return repo.save(existente);
    }

    @Override
    public void eliminar(String id) {
        repo.deleteById(id);
    }
}