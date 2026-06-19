package com.colegio.servicio_asistencia;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "academico.service.url=http://localhost:9999",
    "spring.cloud.openfeign.circuitbreaker.enabled=false",
    "spring.rabbitmq.host=localhost",
    "spring.rabbitmq.port=5672"
})
class ServicioAsistenciaApplicationTests {

	@Test
	void contextLoads() {
		// Verifica que el contexto de Spring se carga correctamente con H2 en memoria
	}

}
