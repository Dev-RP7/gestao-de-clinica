// Pacote raiz: o Spring procura componentes (@Service, @Controller...) a partir daqui.
package com.clinica.gestao_clinica;

// Classe que sobe a aplicação Spring Boot.
import org.springframework.boot.SpringApplication;
// Anotação que liga a configuração automática do Spring Boot.
import org.springframework.boot.autoconfigure.SpringBootApplication;
// Anotação que procura classes @ConfigurationProperties (como a JwtProperties).
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

// Junta três coisas: @Configuration, @EnableAutoConfiguration e @ComponentScan.
@SpringBootApplication
// Faz o Spring ler as classes com @ConfigurationProperties e preencher com os valores do application.properties.
@ConfigurationPropertiesScan
// Classe principal do projeto.
public class GestaoClinicaApplication {

	// Método main: é o ponto de entrada de qualquer programa Java.
	public static void main(String[] args) {
		// Inicia o Spring: cria os objetos, conecta no banco e sobe o servidor web na porta 8080.
		SpringApplication.run(GestaoClinicaApplication.class, args);
	}

}
