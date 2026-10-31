package com.clinica.gestao_clinica.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ApiIntegrationTest {

    private static final String DATA_CONSULTA = LocalDate.now().plusDays(3).atTime(14, 0).toString();

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginDoAdministradorInicialDeveDevolverToken() throws Exception {
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "admin@teste.com", "senha": "admin123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tipoUsuario").value("ADMINISTRADOR"));
    }

    @Test
    void loginComSenhaErradaDeveDevolver401() throws Exception {
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "admin@teste.com", "senha": "errada"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensagem").value("E-mail ou senha inválidos."));
    }

    @Test
    void rotaProtegidaSemTokenDeveDevolver401() throws Exception {
        mockMvc.perform(get("/consultas"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.caminho").value("/consultas"));
    }

    @Test
    void tokenInvalidoDeveDevolver401() throws Exception {
        mockMvc.perform(get("/consultas").header("Authorization", "Bearer token-falso"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cadastroDePacienteInvalidoDeveDevolver400ComCampos() throws Exception {
        mockMvc.perform(post("/pacientes").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "Maria", "email": "nao-e-email", "cpf": "123"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos[*].campo").value(hasItem("email")))
                .andExpect(jsonPath("$.campos[*].campo").value(hasItem("cpf")));
    }

    @Test
    void buscarMedicoInexistenteDeveDevolver404() throws Exception {
        String tokenAdmin = login("admin@teste.com", "admin123");
        mockMvc.perform(comToken(get("/medicos/9999"), tokenAdmin))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Médico com id 9999 não encontrado(a)"));
    }

    @Test
    void pacienteNaoPodeListarUsuarios() throws Exception {
        cadastrarPaciente("joana@teste.com", "11111111111", "RG-J");
        String tokenPaciente = login("joana@teste.com", "senha123");
        mockMvc.perform(comToken(get("/usuarios"), tokenPaciente))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void fluxoCompletoDeAtendimento() throws Exception {
        String tokenAdmin = login("admin@teste.com", "admin123");

        String especialidade = mockMvc.perform(comToken(post("/especialidades"), tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"nome": "Cardiologia"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith("http://localhost/especialidades/")))
                .andReturn().getResponse().getContentAsString();
        Integer especialidadeId = JsonPath.read(especialidade, "$.id");

        String medico = mockMvc.perform(comToken(post("/medicos"), tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"nome": "Dra. Ana", "email": "ana@teste.com", "senha": "senha123",
                                 "telefone": "11999990000", "crm": "CRM-123", "especialidadeId": %d}
                                """.formatted(especialidadeId)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Integer medicoId = JsonPath.read(medico, "$.id");

        Integer pacienteId = cadastrarPaciente("carlos@teste.com", "22222222222", "RG-C");
        String tokenPaciente = login("carlos@teste.com", "senha123");

        String corpoConsulta = """
                {"dataConsulta": "%s", "motivo": "Dor no peito", "medicoId": %d, "pacienteId": %d}
                """.formatted(DATA_CONSULTA, medicoId, pacienteId);
        String consulta = mockMvc.perform(comToken(post("/consultas"), tokenPaciente)
                        .contentType(MediaType.APPLICATION_JSON).content(corpoConsulta))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusConsulta").value("AGENDADA"))
                .andReturn().getResponse().getContentAsString();
        Integer consultaId = JsonPath.read(consulta, "$.id");

        mockMvc.perform(comToken(post("/consultas"), tokenPaciente)
                        .contentType(MediaType.APPLICATION_JSON).content(corpoConsulta))
                .andExpect(status().isConflict());

        mockMvc.perform(comToken(get("/consultas")
                        .param("medicoId", medicoId.toString())
                        .param("status", "AGENDADA")
                        .param("page", "0").param("size", "5"), tokenPaciente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.page.size").value(5));

        mockMvc.perform(comToken(patch("/consultas/" + consultaId + "/concluir"), tokenPaciente))
                .andExpect(status().isForbidden());

        String tokenMedico = login("ana@teste.com", "senha123");
        mockMvc.perform(comToken(patch("/consultas/" + consultaId + "/concluir"), tokenMedico))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusConsulta").value("REALIZADA"));

        mockMvc.perform(comToken(patch("/consultas/" + consultaId + "/cancelar"), tokenMedico))
                .andExpect(status().isUnprocessableContent());

        String prontuario = mockMvc.perform(comToken(post("/prontuarios"), tokenMedico)
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"consultaId": %d, "peso": 80.5, "altura": 1.78, "pressao": 12.8, "temperatura": 36.5,
                                 "diagnostico": "Ansiedade", "tratamento": "Repouso", "observacao": "Retorno em 30 dias"}
                                """.formatted(consultaId)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Integer prontuarioId = JsonPath.read(prontuario, "$.id");

        for (String remedio : new String[]{"Dipirona", "Omeprazol"}) {
            mockMvc.perform(comToken(post("/receitas"), tokenMedico)
                            .contentType(MediaType.APPLICATION_JSON).content("""
                                    {"prontuarioId": %d, "medicamento": "%s", "dosagem": "500mg",
                                     "frequencia": "8/8h", "duracao": "5 dias", "observacao": "Após refeições"}
                                    """.formatted(prontuarioId, remedio)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.prontuarioId").value(prontuarioId));
        }

        mockMvc.perform(comToken(get("/receitas").param("prontuarioId", prontuarioId.toString()), tokenMedico))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(2));

        mockMvc.perform(comToken(get("/relatorios/consultas"), tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.realizadas").value(1));
    }

    private String login(String email, String senha) throws Exception {
        String resposta = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "%s", "senha": "%s"}
                                """.formatted(email, senha)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(resposta, "$.token");
    }

    private Integer cadastrarPaciente(String email, String cpf, String rg) throws Exception {
        String resposta = mockMvc.perform(post("/pacientes").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "Paciente Teste", "email": "%s", "senha": "senha123", "telefone": "11900000000",
                                 "cpf": "%s", "rg": "%s", "sexo": "OUTRO", "dataNascimento": "1990-05-20",
                                 "endereco": "Rua Teste, 10", "tipoSanguineo": "O_NEGATIVO"}
                                """.formatted(email, cpf, rg)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(resposta, "$.id");
    }

    private MockHttpServletRequestBuilder comToken(MockHttpServletRequestBuilder requisicao, String token) {
        return requisicao.header("Authorization", "Bearer " + token);
    }
}
