// Pacote dos testes de controller.
package com.clinica.gestao_clinica.controller;

// JsonPath: lê valores de um texto JSON, ex.: JsonPath.read(json, "$.token").
import com.jayway.jsonpath.JsonPath;
// JUnit.
import org.junit.jupiter.api.Test;
// Injeção de dependências.
import org.springframework.beans.factory.annotation.Autowired;
// Sobe a aplicação inteira.
import org.springframework.boot.test.context.SpringBootTest;
// Cria o MockMvc, que simula requisições HTTP sem abrir uma porta de rede.
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
// Tipo de conteúdo (application/json).
import org.springframework.http.MediaType;
// Perfil de teste.
import org.springframework.test.context.ActiveProfiles;
// Classes do MockMvc.
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
// Cada teste roda numa transação desfeita no final (o banco volta limpo).
import org.springframework.transaction.annotation.Transactional;

// Data.
import java.time.LocalDate;

// Métodos estáticos do MockMvc (get, post, status, jsonPath...).
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Sobe a aplicação completa (controllers, services, segurança, banco H2).
@SpringBootTest
// Disponibiliza o MockMvc para injeção.
@AutoConfigureMockMvc
// Usa o application-test.properties.
@ActiveProfiles("test")
// Desfaz as alterações no banco após cada teste.
@Transactional
// Testes de integração: verificam as camadas funcionando juntas, do HTTP ao banco.
class ApiIntegrationTest {

    // Data/hora futura usada nas consultas (formato ISO, ex.: "2026-10-09T14:00").
    private static final String DATA_CONSULTA = LocalDate.now().plusDays(3).atTime(14, 0).toString();

    // O Spring injeta o MockMvc.
    @Autowired
    // Simulador de requisições.
    private MockMvc mockMvc;

    // ===== Autenticação =====

    // Marca como teste.
    @Test
    // O administrador inicial (criado ao subir a aplicação) deve conseguir logar.
    void loginDoAdministradorInicialDeveDevolverToken() throws Exception {
        // POST /auth/login com o JSON de login.
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        // Corpo da requisição (text block com aspas triplas).
                        .content("""
                                {"email": "admin@teste.com", "senha": "admin123"}
                                """))
                // Espera 200 OK.
                .andExpect(status().isOk())
                // O token deve existir.
                .andExpect(jsonPath("$.token").isNotEmpty())
                // O perfil deve ser ADMINISTRADOR.
                .andExpect(jsonPath("$.tipoUsuario").value("ADMINISTRADOR"));
    }

    // Marca como teste.
    @Test
    // Senha errada deve devolver 401 no formato padrão de erro.
    void loginComSenhaErradaDeveDevolver401() throws Exception {
        // POST /auth/login com senha errada.
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        // Corpo.
                        .content("""
                                {"email": "admin@teste.com", "senha": "errada"}
                                """))
                // Espera 401.
                .andExpect(status().isUnauthorized())
                // A mensagem deve ser a genérica.
                .andExpect(jsonPath("$.mensagem").value("E-mail ou senha inválidos."));
    }

    // Marca como teste.
    @Test
    // Rota protegida sem token deve devolver 401 em JSON (e não uma página HTML).
    void rotaProtegidaSemTokenDeveDevolver401() throws Exception {
        // GET /consultas sem cabeçalho Authorization.
        mockMvc.perform(get("/consultas"))
                // Espera 401.
                .andExpect(status().isUnauthorized())
                // O corpo segue o ErroResponseDTO.
                .andExpect(jsonPath("$.status").value(401))
                // O caminho aparece na resposta.
                .andExpect(jsonPath("$.caminho").value("/consultas"));
    }

    // Marca como teste.
    @Test
    // Token inválido deve ser tratado como "não autenticado".
    void tokenInvalidoDeveDevolver401() throws Exception {
        // GET /consultas com um token falso.
        mockMvc.perform(get("/consultas").header("Authorization", "Bearer token-falso"))
                // Espera 401.
                .andExpect(status().isUnauthorized());
    }

    // ===== Validação e erros =====

    // Marca como teste.
    @Test
    // Dados inválidos no cadastro devem devolver 400 com a lista de campos errados.
    void cadastroDePacienteInvalidoDeveDevolver400ComCampos() throws Exception {
        // POST /pacientes (rota pública) com e-mail e CPF inválidos e campos faltando.
        mockMvc.perform(post("/pacientes").contentType(MediaType.APPLICATION_JSON)
                        // Corpo incompleto.
                        .content("""
                                {"nome": "Maria", "email": "nao-e-email", "cpf": "123"}
                                """))
                // Espera 400.
                .andExpect(status().isBadRequest())
                // A lista de campos deve citar o e-mail...
                .andExpect(jsonPath("$.campos[*].campo").value(hasItem("email")))
                // ...e o CPF.
                .andExpect(jsonPath("$.campos[*].campo").value(hasItem("cpf")));
    }

    // Marca como teste.
    @Test
    // Recurso inexistente deve devolver 404 com mensagem clara.
    void buscarMedicoInexistenteDeveDevolver404() throws Exception {
        // Faz login como admin.
        String tokenAdmin = login("admin@teste.com", "admin123");
        // GET /medicos/9999 com o token.
        mockMvc.perform(comToken(get("/medicos/9999"), tokenAdmin))
                // Espera 404.
                .andExpect(status().isNotFound())
                // Mensagem com o id.
                .andExpect(jsonPath("$.mensagem").value("Médico com id 9999 não encontrado(a)"));
    }

    // ===== Autorização =====

    // Marca como teste.
    @Test
    // Paciente não pode acessar a gestão de usuários (só administrador).
    void pacienteNaoPodeListarUsuarios() throws Exception {
        // Cadastra um paciente.
        cadastrarPaciente("joana@teste.com", "11111111111", "RG-J");
        // Faz login como paciente.
        String tokenPaciente = login("joana@teste.com", "senha123");
        // GET /usuarios com o token do paciente.
        mockMvc.perform(comToken(get("/usuarios"), tokenPaciente))
                // Espera 403 Forbidden.
                .andExpect(status().isForbidden())
                // No formato padrão.
                .andExpect(jsonPath("$.status").value(403));
    }

    // ===== Fluxo completo =====

    // Marca como teste.
    @Test
    // Cenário de ponta a ponta: cadastros, agendamento, conflito, filtros, conclusão, prontuário, receita e relatório.
    void fluxoCompletoDeAtendimento() throws Exception {
        // 1) Login do administrador.
        String tokenAdmin = login("admin@teste.com", "admin123");

        // 2) Administrador cadastra uma especialidade e recebe 201 com o cabeçalho Location.
        String especialidade = mockMvc.perform(comToken(post("/especialidades"), tokenAdmin)
                        // Corpo.
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"nome": "Cardiologia"}
                                """))
                // Espera 201 Created.
                .andExpect(status().isCreated())
                // O Location aponta para o novo recurso.
                .andExpect(header().string("Location", startsWith("http://localhost/especialidades/")))
                // Pega o corpo da resposta como texto.
                .andReturn().getResponse().getContentAsString();
        // Lê o id da especialidade criada.
        Integer especialidadeId = JsonPath.read(especialidade, "$.id");

        // 3) Administrador cadastra um médico.
        String medico = mockMvc.perform(comToken(post("/medicos"), tokenAdmin)
                        // Corpo com os dados do médico.
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"nome": "Dra. Ana", "email": "ana@teste.com", "senha": "senha123",
                                 "telefone": "11999990000", "crm": "CRM-123", "especialidadeId": %d}
                                """.formatted(especialidadeId)))
                // Espera 201.
                .andExpect(status().isCreated())
                // Pega o corpo.
                .andReturn().getResponse().getContentAsString();
        // Lê o id do médico.
        Integer medicoId = JsonPath.read(medico, "$.id");

        // 4) Paciente faz o autocadastro (rota pública) e login.
        Integer pacienteId = cadastrarPaciente("carlos@teste.com", "22222222222", "RG-C");
        // Login do paciente.
        String tokenPaciente = login("carlos@teste.com", "senha123");

        // 5) Paciente agenda uma consulta.
        String corpoConsulta = """
                {"dataConsulta": "%s", "motivo": "Dor no peito", "medicoId": %d, "pacienteId": %d}
                """.formatted(DATA_CONSULTA, medicoId, pacienteId);
        // POST /consultas.
        String consulta = mockMvc.perform(comToken(post("/consultas"), tokenPaciente)
                        // Corpo.
                        .contentType(MediaType.APPLICATION_JSON).content(corpoConsulta))
                // Espera 201.
                .andExpect(status().isCreated())
                // Status inicial AGENDADA.
                .andExpect(jsonPath("$.statusConsulta").value("AGENDADA"))
                // Pega o corpo.
                .andReturn().getResponse().getContentAsString();
        // Lê o id da consulta.
        Integer consultaId = JsonPath.read(consulta, "$.id");

        // 6) Mesmo médico, mesmo horário: deve dar 409 Conflict.
        mockMvc.perform(comToken(post("/consultas"), tokenPaciente)
                        // Mesmo corpo.
                        .contentType(MediaType.APPLICATION_JSON).content(corpoConsulta))
                // Espera 409.
                .andExpect(status().isConflict());

        // 7) Listagem paginada com filtros.
        mockMvc.perform(comToken(get("/consultas")
                        // Filtro por médico.
                        .param("medicoId", medicoId.toString())
                        // Filtro por status.
                        .param("status", "AGENDADA")
                        // Página 0, 5 itens.
                        .param("page", "0").param("size", "5"), tokenPaciente))
                // Espera 200.
                .andExpect(status().isOk())
                // O conteúdo tem 1 consulta.
                .andExpect(jsonPath("$.content.length()").value(1))
                // Os metadados da página aparecem em "page".
                .andExpect(jsonPath("$.page.totalElements").value(1))
                // Tamanho de página pedido.
                .andExpect(jsonPath("$.page.size").value(5));

        // 8) Paciente NÃO pode concluir a consulta (só médico/admin).
        mockMvc.perform(comToken(patch("/consultas/" + consultaId + "/concluir"), tokenPaciente))
                // Espera 403.
                .andExpect(status().isForbidden());

        // 9) Médico faz login e conclui a consulta.
        String tokenMedico = login("ana@teste.com", "senha123");
        // PATCH /consultas/{id}/concluir.
        mockMvc.perform(comToken(patch("/consultas/" + consultaId + "/concluir"), tokenMedico))
                // Espera 200.
                .andExpect(status().isOk())
                // Status REALIZADA.
                .andExpect(jsonPath("$.statusConsulta").value("REALIZADA"));

        // 10) Consulta realizada não pode ser cancelada: 422.
        mockMvc.perform(comToken(patch("/consultas/" + consultaId + "/cancelar"), tokenMedico))
                // Espera 422 Unprocessable Content.
                .andExpect(status().isUnprocessableContent());

        // 11) Médico cria o prontuário.
        String prontuario = mockMvc.perform(comToken(post("/prontuarios"), tokenMedico)
                        // Corpo.
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"consultaId": %d, "peso": 80.5, "altura": 1.78, "pressao": 12.8, "temperatura": 36.5,
                                 "diagnostico": "Ansiedade", "tratamento": "Repouso", "observacao": "Retorno em 30 dias"}
                                """.formatted(consultaId)))
                // Espera 201.
                .andExpect(status().isCreated())
                // Pega o corpo.
                .andReturn().getResponse().getContentAsString();
        // Lê o id do prontuário.
        Integer prontuarioId = JsonPath.read(prontuario, "$.id");

        // 12) Médico cria DUAS receitas no mesmo prontuário (antes o @OneToOne impedia a segunda).
        for (String remedio : new String[]{"Dipirona", "Omeprazol"}) {
            // POST /receitas.
            mockMvc.perform(comToken(post("/receitas"), tokenMedico)
                            // Corpo.
                            .contentType(MediaType.APPLICATION_JSON).content("""
                                    {"prontuarioId": %d, "medicamento": "%s", "dosagem": "500mg",
                                     "frequencia": "8/8h", "duracao": "5 dias", "observacao": "Após refeições"}
                                    """.formatted(prontuarioId, remedio)))
                    // Espera 201 e o id do prontuário correto na resposta.
                    .andExpect(status().isCreated())
                    // O prontuário ficou ligado (antes ficava nulo e o banco recusava).
                    .andExpect(jsonPath("$.prontuarioId").value(prontuarioId));
        }

        // 13) Filtrar receitas pelo prontuário.
        mockMvc.perform(comToken(get("/receitas").param("prontuarioId", prontuarioId.toString()), tokenMedico))
                // Espera 200.
                .andExpect(status().isOk())
                // Duas receitas.
                .andExpect(jsonPath("$.page.totalElements").value(2));

        // 14) Relatório do administrador.
        mockMvc.perform(comToken(get("/relatorios/consultas"), tokenAdmin))
                // Espera 200.
                .andExpect(status().isOk())
                // Uma consulta realizada.
                .andExpect(jsonPath("$.realizadas").value(1));
    }

    // ===== Métodos auxiliares =====

    // Faz login e devolve o token.
    private String login(String email, String senha) throws Exception {
        // POST /auth/login.
        String resposta = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        // Corpo com e-mail e senha.
                        .content("""
                                {"email": "%s", "senha": "%s"}
                                """.formatted(email, senha)))
                // Espera 200.
                .andExpect(status().isOk())
                // Pega o corpo.
                .andReturn().getResponse().getContentAsString();
        // Extrai o token.
        return JsonPath.read(resposta, "$.token");
    }

    // Cadastra um paciente pela rota pública e devolve o id.
    private Integer cadastrarPaciente(String email, String cpf, String rg) throws Exception {
        // POST /pacientes.
        String resposta = mockMvc.perform(post("/pacientes").contentType(MediaType.APPLICATION_JSON)
                        // Corpo com todos os campos obrigatórios.
                        .content("""
                                {"nome": "Paciente Teste", "email": "%s", "senha": "senha123", "telefone": "11900000000",
                                 "cpf": "%s", "rg": "%s", "sexo": "OUTRO", "dataNascimento": "1990-05-20",
                                 "endereco": "Rua Teste, 10", "tipoSanguineo": "O_NEGATIVO"}
                                """.formatted(email, cpf, rg)))
                // Espera 201.
                .andExpect(status().isCreated())
                // Pega o corpo.
                .andReturn().getResponse().getContentAsString();
        // Extrai o id.
        return JsonPath.read(resposta, "$.id");
    }

    // Acrescenta o cabeçalho "Authorization: Bearer <token>" a uma requisição.
    private MockHttpServletRequestBuilder comToken(MockHttpServletRequestBuilder requisicao, String token) {
        // Devolve a mesma requisição com o cabeçalho.
        return requisicao.header("Authorization", "Bearer " + token);
    }
}
