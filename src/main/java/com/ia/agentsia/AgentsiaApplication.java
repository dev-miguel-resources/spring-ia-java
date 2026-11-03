package com.ia.agentsia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // Esto permite habilitar funcionalidades automáticas en bdd como fechas, entre
					// otros...
public class AgentsiaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgentsiaApplication.class, args);
	}

}
