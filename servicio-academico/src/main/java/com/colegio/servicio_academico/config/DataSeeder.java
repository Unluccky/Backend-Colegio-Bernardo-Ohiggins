package com.colegio.servicio_academico.config;

import com.colegio.servicio_academico.model.*;
import com.colegio.servicio_academico.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final ProfesorRepository profesorRepo;
    private final EstudianteRepository estudianteRepo;
    private final ApoderadoRepository apoderadoRepo;
    private final AsignaturaRepository asignaturaRepo;
    private final EvaluacionRepository evaluacionRepo;
    private final HorarioRepository horarioRepo;
    private final NotaRepository notaRepo;

    record ProfesorSeed(String nombre, String apellido, String rut, String especialidad, String email) {}
    record EstudianteSeed(String nombre, String apellido, String rut, String email, int curso) {}
    record ApoderadoSeed(String nombre, String apellido, String rut, String email, int estudianteIdx) {}

    @Override
    public void run(String... args) {
        if (profesorRepo.count() > 0) {
            log.info("Base de datos ya contiene datos — se omite el seed.");
            return;
        }

        log.info("=== Sembrando datos de prueba en Servicio Académico ===");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String pass = "profesor123";

        // ════════════════════════════════════════════════════════════
        // 1. PROFESORES  (7 profesores para distintas especialidades)
        // ════════════════════════════════════════════════════════════
        List<ProfesorSeed> datosProfesores = List.of(
                new ProfesorSeed("Carlos",    "Muñoz",     "11111111-1", "Matemáticas",       "carlos.munoz@colegio.cl"),
                new ProfesorSeed("Laura",     "González",  "22222222-2", "Lenguaje",          "laura.gonzalez@colegio.cl"),
                new ProfesorSeed("Patricia",  "Vega",      "66666666-6", "Inglés",            "patricia.vega@colegio.cl"),
                new ProfesorSeed("Roberto",   "Díaz",      "12121212-1", "Ciencias",          "roberto.diaz@colegio.cl"),
                new ProfesorSeed("Ana",       "Martínez",  "13131313-3", "Historia",          "ana.martinez@colegio.cl"),
                new ProfesorSeed("Fernando",  "Torres",    "14141414-4", "Educación Física",  "fernando.torres@colegio.cl"),
                new ProfesorSeed("Daniela",   "Rojas",     "15151515-5", "Artes",             "daniela.rojas@colegio.cl")
        );

        Map<String, Profesor> profesores = new LinkedHashMap<>();
        for (ProfesorSeed pd : datosProfesores) {
            Profesor p = profesorRepo.save(Profesor.builder()
                    .nombre(pd.nombre).apellido(pd.apellido).rut(pd.rut)
                    .especialidad(pd.especialidad).email(pd.email)
                    .contrasena(encoder.encode(pass)).build());
            profesores.put(pd.especialidad, p);
            log.info("  ✔ Profesor: {} {} ({}) — RUT: {}", pd.nombre, pd.apellido, pd.especialidad, pd.rut);
        }

        // ════════════════════════════════════════════════════════════
        // 2. ASIGNATURAS para cada curso (1° Básico → 4° Medio)
        // ════════════════════════════════════════════════════════════
        //  Cursos 1-6: Matemáticas, Lenguaje, Inglés, Ciencias, Historia, Ed.Física, Artes
        //  Cursos 7-12: mismos sin Artes
        Map<Integer, List<String>> materiasPorCurso = new LinkedHashMap<>();
        List<String> todas = List.of("Matemáticas", "Lenguaje", "Inglés", "Ciencias", "Historia", "Educación Física");
        for (int c = 1; c <= 12; c++) {
            List<String> materias = new ArrayList<>(todas);
            if (c <= 6) materias.add("Artes");
            materiasPorCurso.put(c, materias);
        }

        // nombreAsignatura → { curso → Asignatura }
        Map<String, Map<Integer, Asignatura>> asigPorNombre = new HashMap<>();
        int totalAsig = 0;
        for (Map.Entry<Integer, List<String>> entry : materiasPorCurso.entrySet()) {
            int curso = entry.getKey();
            for (String nombreMat : entry.getValue()) {
                Profesor profe = profesores.get(nombreMat);
                if (profe == null) {
                    log.warn("  ⚠ No hay profesor para '{}'", nombreMat);
                    continue;
                }
                Asignatura a = asignaturaRepo.save(Asignatura.builder()
                        .nombre(nombreMat).nivelCurso(curso).profesor(profe).build());
                asigPorNombre.computeIfAbsent(nombreMat, k -> new HashMap<>()).put(curso, a);
                totalAsig++;
            }
        }
        log.info("  ✔ Asignaturas: {} total creadas para cursos 1-12", totalAsig);

        // ════════════════════════════════════════════════════════════
        // 3. HORARIOS  (2-3 bloques por curso)
        // ════════════════════════════════════════════════════════════
        String[] horariosStr = {"08:00", "10:15", "12:30", "14:45"};
        String[] horariosFin = {"08:45", "11:00", "13:15", "15:30"};
        String[] salasPool = {"A-101", "A-102", "B-201", "B-202", "C-301", "C-302", "D-401", "GIM-1", "LAB-1"};

        Random rand = new Random(42); // seed fija para resultados reproducibles
        int totalHorarios = 0;
        int bloquesPorCurso = 7; // horario casi completo

        for (int curso = 1; curso <= 12; curso++) {
            List<String> materias = materiasPorCurso.get(curso);
            for (int b = 0; b < bloquesPorCurso; b++) {
                // Distribuir en días 1-5 variando bloques
                int dia = (b % 5) + 1;
                // Alternar horarios: 08:00, 10:15, 12:30, 09:30, 11:45, 14:45, 08:45
                int slot = b % horariosStr.length;
                String materia = materias.get(b % materias.size());
                String sala = salasPool[(curso + b) % salasPool.length];

                Map<Integer, Asignatura> asigMap = asigPorNombre.get(materia);
                if (asigMap == null) continue;
                Asignatura asig = asigMap.get(curso);
                if (asig == null) continue;

                horarioRepo.save(Horario.builder()
                        .asignatura(asig)
                        .profesor(asig.getProfesor())
                        .curso(curso).dia(dia)
                        .horaInicio(horariosStr[slot])
                        .horaFin(horariosFin[slot])
                        .sala(sala).build());
                totalHorarios++;
            }
        }
        log.info("  ✔ Horarios: {} bloques creados para cursos 1-12", totalHorarios);

        // ════════════════════════════════════════════════════════════
        // 4. ESTUDIANTES (2 por cada curso 1-4, total 8)
        // ════════════════════════════════════════════════════════════
        List<EstudianteSeed> datosEstudiantes = List.of(
                new EstudianteSeed("Ana",      "Soto",     "33333333-3", "ana.soto@colegio.cl",     1),
                new EstudianteSeed("Pedro",    "Ramírez",  "44444444-4", "pedro.ramirez@colegio.cl", 1),
                new EstudianteSeed("Camila",   "Torres",   "16161616-6", "camila.torres@colegio.cl", 2),
                new EstudianteSeed("Benjamín", "Díaz",     "17171717-7", "benjamin.diaz@colegio.cl", 2),
                new EstudianteSeed("Valentina","Muñoz",    "18181818-8", "valentina.munoz@colegio.cl", 3),
                new EstudianteSeed("Mateo",    "López",    "19191919-9", "mateo.lopez@colegio.cl",   3),
                new EstudianteSeed("Isidora",  "Martínez", "24242424-4", "isidora.martinez@colegio.cl", 4),
                new EstudianteSeed("Santiago", "Vargas",   "25252525-5", "santiago.vargas@colegio.cl", 4),
                // Cursos 5-12
                new EstudianteSeed("Florencia", "Álvarez",  "27272727-7", "florencia.alvarez@colegio.cl", 5),
                new EstudianteSeed("Ignacio",  "Cruz",     "28282828-8", "ignacio.cruz@colegio.cl",     5),
                new EstudianteSeed("Antonia",  "Flores",   "29292929-9", "antonia.flores@colegio.cl",   6),
                new EstudianteSeed("Felipe",   "Gutiérrez","30303030-0", "felipe.gutierrez@colegio.cl", 6),
                new EstudianteSeed("Josefina", "Herrera",  "31313131-1", "josefina.herrera@colegio.cl",  7),
                new EstudianteSeed("Nicolás",  "Iglesias", "32323232-2", "nicolas.iglesias@colegio.cl", 7),
                new EstudianteSeed("Amanda",   "Jara",     "34343434-4", "amanda.jara@colegio.cl",      8),
                new EstudianteSeed("Tomás",    "King",     "35353535-5", "tomas.king@colegio.cl",       8),
                new EstudianteSeed("Emilia",   "Lara",     "36363636-6", "emilia.lara@colegio.cl",      9),
                new EstudianteSeed("Joaquín",  "Molina",   "37373737-7", "joaquin.molina@colegio.cl",   9),
                new EstudianteSeed("Isabella", "Navarro",  "38383838-8", "isabella.navarro@colegio.cl", 10),
                new EstudianteSeed("Sebastián","Ortiz",    "39393939-9", "sebastian.ortiz@colegio.cl",  10),
                new EstudianteSeed("Martina",  "Pérez",    "40404040-0", "martina.perez@colegio.cl",    11),
                new EstudianteSeed("Benito",   "Quintana", "41414141-1", "benito.quintana@colegio.cl",  11),
                new EstudianteSeed("Catalina", "Reyes",    "42424242-2", "catalina.reyes@colegio.cl",   12),
                new EstudianteSeed("Diego",    "Soto",     "43434343-3", "diego.soto@colegio.cl",       12)
        );

        List<Estudiante> estudiantes = new ArrayList<>();
        for (EstudianteSeed ed : datosEstudiantes) {
            Estudiante e = estudianteRepo.save(Estudiante.builder()
                    .nombre(ed.nombre).apellido(ed.apellido).rut(ed.rut)
                    .email(ed.email).curso(ed.curso)
                    .password(encoder.encode("alumno123")).build());
            estudiantes.add(e);
        }
        log.info("  ✔ Estudiantes: {} creados en cursos 1-12", estudiantes.size());

        // ════════════════════════════════════════════════════════════
        // 5. APODERADOS
        // ════════════════════════════════════════════════════════════
        List<ApoderadoSeed> datosApoderados = List.of(
                new ApoderadoSeed("María",     "Soto",     "55555555-5",  "maria.soto@colegio.cl",     0),  // → Ana Soto (idx 0)
                new ApoderadoSeed("Andrés",    "Torres",   "20202020-0",  "andres.torres@colegio.cl",   2),  // → Camila Torres (idx 2)
                new ApoderadoSeed("Carolina",  "Díaz",     "21212121-1",  "carolina.diaz@colegio.cl",   3),  // → Benjamín Díaz (idx 3)
                new ApoderadoSeed("Francisco", "Muñoz",    "23232323-3",  "francisco.munoz@colegio.cl", 4),  // → Valentina Muñoz (idx 4)
                new ApoderadoSeed("Paula",     "López",    "26262626-6",  "paula.lopez@colegio.cl",     5),  // → Mateo López (idx 5)
                new ApoderadoSeed("Rosa",      "Álvarez",  "53535353-3",  "rosa.alvarez@colegio.cl",    8),  // → Florencia Álvarez (idx 8)
                new ApoderadoSeed("Pedro",     "Cruz",     "45454545-5",  "pedro.cruz@colegio.cl",      9),  // → Ignacio Cruz (idx 9)
                new ApoderadoSeed("Lorena",    "Flores",   "46464646-6",  "lorena.flores@colegio.cl",   10), // → Antonia Flores (idx 10)
                new ApoderadoSeed("Mónica",    "Herrera",  "47474747-7",  "monica.herrera@colegio.cl",  12), // → Josefina Herrera (idx 12)
                new ApoderadoSeed("Patricio",  "Jara",     "48484848-8",  "patricio.jara@colegio.cl",   14), // → Amanda Jara (idx 14)
                new ApoderadoSeed("Verónica",  "Lara",     "49494949-9",  "veronica.lara@colegio.cl",   16), // → Emilia Lara (idx 16)
                new ApoderadoSeed("Héctor",    "Navarro",  "50505050-0",  "hector.navarro@colegio.cl",  18), // → Isabella Navarro (idx 18)
                new ApoderadoSeed("Gloria",    "Pérez",    "51515151-1",  "gloria.perez@colegio.cl",    20), // → Martina Pérez (idx 20)
                new ApoderadoSeed("Álvaro",    "Reyes",    "52525252-2",  "alvaro.reyes@colegio.cl",    22)  // → Catalina Reyes (idx 22)
        );

        for (ApoderadoSeed ad : datosApoderados) {
            apoderadoRepo.save(Apoderado.builder()
                    .nombre(ad.nombre).apellido(ad.apellido).rut(ad.rut)
                    .email(ad.email)
                    .contrasena(encoder.encode("apoderado123"))
                    .estudiante(estudiantes.get(ad.estudianteIdx)).build());
        }
        log.info("  ✔ Apoderados: {} creados", datosApoderados.size());

        // ════════════════════════════════════════════════════════════
        // 6. EVALUACIONES (para cursos 1, 3, 5, 7, 9, 11)
        // ════════════════════════════════════════════════════════════
        int[] cursosEval = {1, 3, 5, 7, 9, 11};
        int totalEval = 0;
        Map<Integer, Map<String, Evaluacion>> evaluaciones = new HashMap<>();

        for (int ce : cursosEval) {
            Map<String, Evaluacion> evalsCurso = new HashMap<>();
            String[] nombresEval = {"Prueba N°1", "Trabajo N°1", "Prueba N°2"};
            String[] materiasEval = {"Matemáticas", "Lenguaje", "Ciencias"};
            TipoEvaluacion[] tipos = {TipoEvaluacion.PRUEBA, TipoEvaluacion.TRABAJO, TipoEvaluacion.PRUEBA};

            for (int i = 0; i < 3; i++) {
                Asignatura asig = asigPorNombre.get(materiasEval[i]).get(ce);
                if (asig == null) continue;
                Evaluacion e = evaluacionRepo.save(Evaluacion.builder()
                        .nombre(nombresEval[i] + " - " + asig.getNombre())
                        .fecha(LocalDate.now().minusDays(5 - i * 2))
                        .tipo(tipos[i])
                        .asignatura(asig).build());
                evalsCurso.put(materiasEval[i], e);
                totalEval++;
            }
            evaluaciones.put(ce, evalsCurso);
        }
        log.info("  ✔ Evaluaciones: {} creadas para múltiples cursos", totalEval);

        // ════════════════════════════════════════════════════════════
        // 7. NOTAS
        // ════════════════════════════════════════════════════════════
        Estudiante ana  = estudiantes.get(0);  // curso 1
        Estudiante pedro = estudiantes.get(1); // curso 1

        Map<String, Evaluacion> evalsCurso1 = evaluaciones.get(1);
        if (evalsCurso1 != null) {
            notaRepo.save(Nota.builder().estudiante(ana).evaluacion(evalsCurso1.get("Matemáticas")).valor(5.5).build());
            notaRepo.save(Nota.builder().estudiante(ana).evaluacion(evalsCurso1.get("Lenguaje")).valor(6.2).build());
            notaRepo.save(Nota.builder().estudiante(ana).evaluacion(evalsCurso1.get("Ciencias")).valor(5.8).build());
            notaRepo.save(Nota.builder().estudiante(pedro).evaluacion(evalsCurso1.get("Matemáticas")).valor(4.0).build());
            notaRepo.save(Nota.builder().estudiante(pedro).evaluacion(evalsCurso1.get("Lenguaje")).valor(5.0).build());
            notaRepo.save(Nota.builder().estudiante(pedro).evaluacion(evalsCurso1.get("Ciencias")).valor(4.5).build());
        }

        // Notas para estudiantes de otros cursos
        for (int ce : new int[]{3, 5, 7, 9, 11}) {
            Map<String, Evaluacion> evals = evaluaciones.get(ce);
            if (evals == null) continue;
            // Encontrar estudiantes de ese curso
            int curso = ce;
            List<Estudiante> estCurso = estudiantes.stream().filter(e -> e.getCurso() == curso).toList();
            for (Estudiante e : estCurso) {
                notaRepo.save(Nota.builder().estudiante(e).evaluacion(evals.get("Matemáticas")).valor(4.0 + Math.random() * 3).build());
                notaRepo.save(Nota.builder().estudiante(e).evaluacion(evals.get("Lenguaje")).valor(4.0 + Math.random() * 3).build());
            }
        }
        log.info("  ✔ Notas: registradas para múltiples cursos");

        log.info("✅ Seed de Servicio Académico completado.");
    }
}
