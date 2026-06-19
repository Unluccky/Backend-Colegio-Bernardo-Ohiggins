package com.colegio.servicio_comunicaciones.service.impl;

import com.colegio.servicio_comunicaciones.model.Notificacion;
import com.colegio.servicio_comunicaciones.repository.NotificacionRepository;
import com.colegio.servicio_comunicaciones.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository repo;

    @Override
    public List<Notificacion> listarTodos() {
        return repo.findAll();
    }

    @Override
    public Notificacion buscarPorId(String id) {
        return repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Notificacion no encontrada: " + id));
    }

    @Override
    public List<Notificacion> buscarPorDestinatario(Long destinatarioId) {
        return repo.findByDestinatarioId(destinatarioId);
    }

    @Override
    public Notificacion guardar(Notificacion notificacion) {
        return repo.save(notificacion);
    }

    @Override
    public void eliminar(String id) {
        repo.deleteById(id);
    }
}