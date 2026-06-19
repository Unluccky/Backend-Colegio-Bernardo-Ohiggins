package com.colegio.servicio_comunicaciones.config;

import com.colegio.servicio_comunicaciones.model.Mensaje;
import com.colegio.servicio_comunicaciones.model.Notificacion;
import com.colegio.servicio_comunicaciones.repository.MensajeRepository;
import com.colegio.servicio_comunicaciones.repository.NotificacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DataSeeder - Pruebas Unitarias")
class DataSeederTest {

    @Mock
    private MensajeRepository mensajeRepo;

    @Mock
    private NotificacionRepository notificacionRepo;

    private DataSeeder seeder;

    @BeforeEach
    void setUp() {
        seeder = new DataSeeder(mensajeRepo, notificacionRepo);
    }

    @Test
    @DisplayName("run - cuando la BD está vacía, siembra datos")
    void run_conBdVacia_deberiaSembrarDatos() {
        when(mensajeRepo.count()).thenReturn(0L);
        when(mensajeRepo.save(any(Mensaje.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(notificacionRepo.save(any(Notificacion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        seeder.run();

        verify(mensajeRepo, times(4)).save(any(Mensaje.class));
        verify(notificacionRepo, times(5)).save(any(Notificacion.class));
    }

    @Test
    @DisplayName("run - cuando ya hay datos, omite la siembra")
    void run_conDatosExistentes_deberiaOmitir() {
        when(mensajeRepo.count()).thenReturn(1L);

        seeder.run();

        verify(mensajeRepo, never()).save(any(Mensaje.class));
        verify(notificacionRepo, never()).save(any(Notificacion.class));
    }
}
