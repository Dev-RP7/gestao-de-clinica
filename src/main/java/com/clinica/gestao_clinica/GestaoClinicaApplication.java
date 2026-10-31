package com.clinica.gestao_clinica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class GestaoClinicaApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestaoClinicaApplication.class, args);
	}

}
