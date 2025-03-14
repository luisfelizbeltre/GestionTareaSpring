package com.gestion_tarea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
@EnableJpaRepositories("com.gestion_tarea.repository")
@SpringBootApplication

public class GestionTareas22Application {

	public static void main(String[] args) {
    SpringApplication.run(GestionTareas22Application.class, args);
	}

}
