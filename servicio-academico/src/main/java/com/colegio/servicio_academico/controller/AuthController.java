package com.colegio.servicio_academico.controller;

import com.colegio.servicio_academico.model.Apoderado;
import com.colegio.servicio_academico.model.Estudiante;
import com.colegio.servicio_academico.model.Profesor;
import com.colegio.servicio_academico.repository.ApoderadoRepository;
import com.colegio.servicio_academico.repository.EstudianteRepository;
import com.colegio.servicio_academico.repository.ProfesorRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación Académico", description = "Validación de credenciales contra la base de datos")
public class AuthController {

    private final ProfesorRepository profesorRepo;
    private final EstudianteRepository estudianteRepo;
    private final ApoderadoRepository apoderadoRepo;

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/validar")
    @Operation(summary = "Validar credenciales de un usuario",
               description = "Busca el RUT en Profesores, Estudiantes y Apoderados, y verifica la contraseña con BCrypt")
    public ResponseEntity<Map<String, Object>> validar(@RequestBody Map<String, String> request) {
        String rut = request.get("rut");
        String password = request.get("password");

        if (rut == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "valido", false,
                "error", "RUT y contraseña son requeridos"
            ));
        }

        // 1. Buscar en Profesores
        Optional<Profesor> profesor = profesorRepo.findByRut(rut);
        if (profesor.isPresent()) {
            if (passwordEncoder.matches(password, profesor.get().getContrasena())) {
                return ResponseEntity.ok(Map.of(
                    "valido", true,
                    "role", "PROFESOR",
                    "nombre", profesor.get().getNombre() + " " + profesor.get().getApellido()
                ));
            }
            return ResponseEntity.ok(Map.of("valido", false, "role", "", "error", "Contraseña incorrecta"));
        }

        // 2. Buscar en Estudiantes
        Optional<Estudiante> estudiante = estudianteRepo.findByRut(rut);
        if (estudiante.isPresent()) {
            if (passwordEncoder.matches(password, estudiante.get().getPassword())) {
                return ResponseEntity.ok(Map.of(
                    "valido", true,
                    "role", "ALUMNO",
                    "nombre", estudiante.get().getNombre() + " " + estudiante.get().getApellido()
                ));
            }
            return ResponseEntity.ok(Map.of("valido", false, "role", "", "error", "Contraseña incorrecta"));
        }

        // 3. Buscar en Apoderados
        Optional<Apoderado> apoderado = apoderadoRepo.findByRut(rut);
        if (apoderado.isPresent()) {
            if (passwordEncoder.matches(password, apoderado.get().getContrasena())) {
                return ResponseEntity.ok(Map.of(
                    "valido", true,
                    "role", "APODERADO",
                    "nombre", apoderado.get().getNombre() + " " + apoderado.get().getApellido()
                ));
            }
            return ResponseEntity.ok(Map.of("valido", false, "role", "", "error", "Contraseña incorrecta"));
        }

        // 4. No encontrado en ninguna tabla
        return ResponseEntity.ok(Map.of(
            "valido", false,
            "role", "",
            "error", "RUT no encontrado en el sistema"
        ));
    }

    @PostMapping("/cambiar-contrasena")
    @Operation(summary = "Cambiar contraseña de un usuario",
               description = "Verifica la contraseña actual y la actualiza por una nueva. Aplica a Profesores, Estudiantes y Apoderados.")
    public ResponseEntity<Map<String, Object>> cambiarContrasena(@RequestBody Map<String, String> request) {
        String rut = request.get("rut");
        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");

        if (rut == null || currentPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "exito", false,
                "error", "RUT, contraseña actual y nueva contraseña son requeridos"
            ));
        }

        if (newPassword.length() < 4) {
            return ResponseEntity.badRequest().body(Map.of(
                "exito", false,
                "error", "La nueva contraseña debe tener al menos 4 caracteres"
            ));
        }

        // 1. Buscar en Profesores
        Optional<Profesor> profesor = profesorRepo.findByRut(rut);
        if (profesor.isPresent()) {
            if (!passwordEncoder.matches(currentPassword, profesor.get().getContrasena())) {
                return ResponseEntity.ok(Map.of("exito", false, "error", "La contraseña actual no es correcta"));
            }
            profesor.get().setContrasena(passwordEncoder.encode(newPassword));
            profesorRepo.save(profesor.get());
            return ResponseEntity.ok(Map.of("exito", true, "mensaje", "Contraseña actualizada correctamente"));
        }

        // 2. Buscar en Estudiantes
        Optional<Estudiante> estudiante = estudianteRepo.findByRut(rut);
        if (estudiante.isPresent()) {
            if (!passwordEncoder.matches(currentPassword, estudiante.get().getPassword())) {
                return ResponseEntity.ok(Map.of("exito", false, "error", "La contraseña actual no es correcta"));
            }
            estudiante.get().setPassword(passwordEncoder.encode(newPassword));
            estudianteRepo.save(estudiante.get());
            return ResponseEntity.ok(Map.of("exito", true, "mensaje", "Contraseña actualizada correctamente"));
        }

        // 3. Buscar en Apoderados
        Optional<Apoderado> apoderado = apoderadoRepo.findByRut(rut);
        if (apoderado.isPresent()) {
            if (!passwordEncoder.matches(currentPassword, apoderado.get().getContrasena())) {
                return ResponseEntity.ok(Map.of("exito", false, "error", "La contraseña actual no es correcta"));
            }
            apoderado.get().setContrasena(passwordEncoder.encode(newPassword));
            apoderadoRepo.save(apoderado.get());
            return ResponseEntity.ok(Map.of("exito", true, "mensaje", "Contraseña actualizada correctamente"));
        }

        // 4. No encontrado
        return ResponseEntity.ok(Map.of(
            "exito", false,
            "error", "RUT no encontrado en el sistema"
        ));
    }

    @PostMapping("/resetear-contrasena")
    @Operation(summary = "Resetear contraseña de un usuario (solo UTP)",
               description = "Permite a un administrador UTP resetear la contraseña de cualquier usuario sin verificar la actual.")
    public ResponseEntity<Map<String, Object>> resetearContrasena(@RequestBody Map<String, String> request) {
        String rut = request.get("rut");
        String newPassword = request.get("newPassword");

        if (rut == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "exito", false,
                "error", "RUT y nueva contraseña son requeridos"
            ));
        }

        if (newPassword.length() < 4) {
            return ResponseEntity.badRequest().body(Map.of(
                "exito", false,
                "error", "La nueva contraseña debe tener al menos 4 caracteres"
            ));
        }

        // 1. Buscar en Profesores
        Optional<Profesor> profesor = profesorRepo.findByRut(rut);
        if (profesor.isPresent()) {
            profesor.get().setContrasena(passwordEncoder.encode(newPassword));
            profesorRepo.save(profesor.get());
            return ResponseEntity.ok(Map.of("exito", true, "mensaje", "Contraseña reseteada correctamente"));
        }

        // 2. Buscar en Estudiantes
        Optional<Estudiante> estudiante = estudianteRepo.findByRut(rut);
        if (estudiante.isPresent()) {
            estudiante.get().setPassword(passwordEncoder.encode(newPassword));
            estudianteRepo.save(estudiante.get());
            return ResponseEntity.ok(Map.of("exito", true, "mensaje", "Contraseña reseteada correctamente"));
        }

        // 3. Buscar en Apoderados
        Optional<Apoderado> apoderado = apoderadoRepo.findByRut(rut);
        if (apoderado.isPresent()) {
            apoderado.get().setContrasena(passwordEncoder.encode(newPassword));
            apoderadoRepo.save(apoderado.get());
            return ResponseEntity.ok(Map.of("exito", true, "mensaje", "Contraseña reseteada correctamente"));
        }

        // 4. No encontrado
        return ResponseEntity.ok(Map.of(
            "exito", false,
            "error", "RUT no encontrado en el sistema"
        ));
    }
}
