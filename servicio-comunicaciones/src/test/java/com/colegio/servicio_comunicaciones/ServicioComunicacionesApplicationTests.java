package com.colegio.servicio_comunicaciones;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Requiere MongoDB y RabbitMQ en el entorno. Los tests unitarios no necesitan el contexto de Spring.")
@SpringBootTest
class ServicioComunicacionesApplicationTests {

	@Test
	void contextLoads() {
		// Deshabilitado porque las pruebas reales son unitarias con Mockito y no necesitan Spring Context.
		// Para ejecutar este test se necesita MongoDB y RabbitMQ disponibles.
	}

}
