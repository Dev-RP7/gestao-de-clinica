// Pacote raiz dos testes (espelha o pacote do código principal).
package com.clinica.gestao_clinica;

// Anotação que marca um método como teste do JUnit 5.
import org.junit.jupiter.api.Test;
// Sobe a aplicação Spring inteira para o teste.
import org.springframework.boot.test.context.SpringBootTest;
// Escolhe o perfil de configuração (usa o application-test.properties).
import org.springframework.test.context.ActiveProfiles;

// Sobe o contexto completo do Spring.
@SpringBootTest
// Usa o banco H2 em memória em vez do MySQL.
@ActiveProfiles("test")
// Teste "fumaça": garante que a aplicação consegue iniciar.
class GestaoClinicaApplicationTests {

	// Marca o método como teste.
	@Test
	// Se o Spring não conseguir criar algum bean (ex.: consulta JPQL inválida), este teste falha.
	void contextLoads() {
	}

}
