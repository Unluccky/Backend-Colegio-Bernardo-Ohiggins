package com.colegio.servicio_asistencia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ServicioAsistenciaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServicioAsistenciaApplication.class, args);
	}

}