# Sistema de Gestão de Clínica - API RESTful

API RESTful desenvolvida com **Java** e **Spring Boot** para gerenciamento de uma clínica médica: usuários, médicos, pacientes, especialidades, consultas, prontuários e receitas, com autenticação **JWT**, controle de acesso por perfil, paginação, filtros, tratamento de erros padronizado, logs, testes automatizados e Docker.

> 💡 **Para estudar:** todo o código Java tem um comentário `//` acima de cada linha explicando o que ela faz. Uma boa ordem de leitura é: `entity` → `repository` → `service` → `controller` → `security` → `exception`.

---

## Tecnologias

- Java 21
- Spring Boot 4.1 (Web MVC, Data JPA, Security, Validation)
- Hibernate 7
- JWT (biblioteca `java-jwt` da Auth0)
- MySQL 8 (produção) e H2 (testes)
- springdoc-openapi 3 (Swagger)
- Lombok
- JUnit 5, Mockito, AssertJ e MockMvc
- Docker e Docker Compose

---

## Arquitetura

```
Requisição HTTP
   ↓
RequisicaoLogFilter      → registra método, rota, status e tempo de cada requisição
   ↓
JwtAuthenticationFilter  → lê o token e identifica o usuário
   ↓
SecurityConfig           → verifica se o perfil pode acessar a rota
   ↓
Controller               → recebe e valida o JSON (DTO), devolve a resposta HTTP
   ↓
Service                  → regras de negócio e transações
   ↓
Repository               → acesso ao banco (Spring Data JPA)
   ↓
Banco de dados
```

Erros lançados em qualquer camada são convertidos em JSON pelo `GlobalExceptionHandler`.

### Estrutura de pacotes

| Pacote | Responsabilidade |
| --- | --- |
| `config` | CORS, Swagger, administrador inicial e filtro de logs |
| `controller` | Rotas REST |
| `converter` | Conversão do tipo sanguíneo para o banco (`A+`, `O-`...) |
| `dto.request` / `dto.response` | Dados que entram e saem da API |
| `entity` | Tabelas do banco |
| `enums` | Valores fixos (perfis, status, sexo, tipo sanguíneo) |
| `exception` | Exceções e tratamento global de erros |
| `mapper` | Conversão entre entidade e DTO |
| `repository` | Consultas ao banco |
| `security` | JWT, filtro de autenticação e regras de acesso |
| `service` | Regras de negócio |

---

## Como executar

### Opção 1: Docker (mais simples)

Pré-requisito: Docker Desktop.

```bash
git clone https://github.com/Dev-RP7/gestao-de-clinica.git
cd gestao-de-clinica
cp .env.example .env        # opcional: troque as senhas e a chave JWT
docker compose up --build
```

Isso sobe o MySQL (porta `3307` do seu computador) e a API (porta `8080`). As tabelas são criadas automaticamente.

Para parar: `docker compose down` (os dados ficam guardados no volume `mysql-data`).

### Opção 2: Localmente (IntelliJ ou terminal)

Pré-requisitos: Java 21+ e MySQL rodando.

1. Copie `src/main/resources/application-local.properties.example` para `application-local.properties` na mesma pasta e preencha usuário e senha do MySQL. Esse arquivo está no `.gitignore`.
2. Rode a aplicação:

```bash
./mvnw spring-boot:run      # Linux/macOS
mvnw.cmd spring-boot:run    # Windows
```

O banco `clinica` e as tabelas são criados automaticamente (`createDatabaseIfNotExist` + `ddl-auto=update`).

### Variáveis de ambiente

| Variável | Padrão | Descrição |
| --- | --- | --- |
| `DB_URL` | `jdbc:mysql://localhost:3306/clinica?...` | URL do banco |
| `DB_USERNAME` / `DB_PASSWORD` | `root` / vazio | Credenciais do banco |
| `JWT_SECRET` | chave de desenvolvimento | **Troque em produção** |
| `JWT_EXPIRACAO` | `7200` | Validade do token (segundos) |
| `ADMIN_EMAIL` / `ADMIN_SENHA` | `admin@clinica.com` / `admin123` | Administrador criado no primeiro start |
| `CORS_ORIGENS` | `http://localhost:4200` | Origens liberadas (separadas por vírgula) |
| `LOG_FILE` | `logs/gestao-clinica.log` | Arquivo de log |

---

## Documentação interativa (Swagger)

Com a aplicação rodando, acesse **http://localhost:8080/swagger-ui.html**.

1. Execute `POST /auth/login` com o administrador inicial.
2. Copie o `token` da resposta.
3. Clique em **Authorize** e cole o token. Todas as rotas passam a enviá-lo.

---

## Autenticação e perfis

Faça login em `POST /auth/login`:

```json
{ "email": "admin@clinica.com", "senha": "admin123" }
```

Resposta:

```json
{ "token": "eyJ...", "tipo": "Bearer", "expiraEmSegundos": 7200, "nome": "Administrador", "tipoUsuario": "ADMINISTRADOR" }
```

Envie o token nas demais requisições: `Authorization: Bearer eyJ...`

As senhas são guardadas criptografadas com **BCrypt**. Na primeira inicialização, se não existir nenhum administrador, um é criado com `ADMIN_EMAIL` / `ADMIN_SENHA`.

| Rota | Quem acessa |
| --- | --- |
| `POST /auth/login`, `POST /pacientes` (autocadastro), Swagger | Público |
| `/usuarios/**`, `/relatorios/**` | Administrador |
| `GET /medicos/**`, `GET /especialidades/**` | Qualquer usuário logado |
| Criar/alterar/excluir médicos e especialidades | Administrador |
| `GET /pacientes/**` | Administrador ou médico |
| Alterar/excluir pacientes | Administrador |
| `/prontuarios/**`, `/receitas/**`, `PATCH /consultas/{id}/concluir` | Administrador ou médico |
| Demais rotas de `/consultas` | Qualquer usuário logado |

---

## Endpoints

| Método | Rota | Descrição |
| --- | --- | --- |
| POST | `/auth/login` | Login (devolve o token JWT) |
| POST / GET | `/usuarios` | Cadastrar administrador / listar usuários |
| GET / PUT / DELETE | `/usuarios/{id}` | Buscar / atualizar / desativar |
| POST / GET | `/especialidades` | Cadastrar / listar |
| GET / PUT / DELETE | `/especialidades/{id}` | Buscar / renomear / excluir |
| POST / GET | `/medicos` | Cadastrar / listar |
| GET / PUT / DELETE | `/medicos/{id}` | Buscar / atualizar / desativar |
| POST / GET | `/pacientes` | Autocadastro / listar |
| GET / PUT / DELETE | `/pacientes/{id}` | Buscar / atualizar / desativar |
| POST / GET | `/consultas` | Agendar / listar |
| GET / PUT | `/consultas/{id}` | Buscar / remarcar |
| PATCH | `/consultas/{id}/confirmar` | AGENDADA → CONFIRMADA |
| PATCH | `/consultas/{id}/concluir` | → REALIZADA |
| PATCH ou DELETE | `/consultas/{id}/cancelar` ou `/consultas/{id}` | → CANCELADA |
| POST / GET | `/prontuarios` | Criar / listar |
| GET / PUT / DELETE | `/prontuarios/{id}` | Buscar / atualizar / excluir |
| POST / GET | `/receitas` | Criar / listar |
| GET / PUT / DELETE | `/receitas/{id}` | Buscar / atualizar / excluir |
| GET | `/relatorios/consultas` | Quantidade de consultas por status |

"Desativar" é uma exclusão lógica: o registro continua no banco com `ativo = false` e o usuário não consegue mais fazer login.

### Regras de negócio

- E-mail, CPF, RG, CRM e nome de especialidade não podem se repetir (**409**).
- Um médico não pode ter duas consultas abertas no mesmo horário (**409**).
- Consultas só podem ser marcadas para datas futuras (**400**).
- Consultas canceladas ou realizadas não podem ser remarcadas, concluídas nem canceladas (**422**).
- Cada consulta tem no máximo um prontuário; um prontuário pode ter várias receitas.
- Não é possível excluir uma especialidade em uso nem um prontuário com receitas (**409**).

---

## Paginação e filtros

Todas as listagens são paginadas: `?page=0&size=10&sort=campo,asc` (tamanho máximo: 100).

| Rota | Filtros opcionais |
| --- | --- |
| `GET /consultas` | `medicoId`, `pacienteId`, `status`, `dataInicio`, `dataFim` (formato `aaaa-mm-dd`) |
| `GET /medicos` | `nome` (parcial), `especialidadeId` |
| `GET /pacientes` | `nome` (parcial), `cpf` |
| `GET /usuarios` | `nome` (parcial), `tipo` |
| `GET /especialidades` | `nome` (parcial) |
| `GET /prontuarios` | `pacienteId`, `medicoId` |
| `GET /receitas` | `prontuarioId` |

Exemplo: `GET /consultas?medicoId=1&status=AGENDADA&dataInicio=2026-12-01&dataFim=2026-12-31&page=0&size=5`

```json
{
  "content": [ { "id": 1, "dataConsulta": "2026-12-10T14:00:00", "statusConsulta": "AGENDADA", "...": "..." } ],
  "page": { "size": 5, "number": 0, "totalElements": 1, "totalPages": 1 }
}
```

---

## Formato dos erros

Todos os erros seguem o mesmo formato:

```json
{
  "timestamp": "2026-10-06T15:49:01.79",
  "status": 400,
  "erro": "Bad Request",
  "mensagem": "Dados inválidos. Verifique os campos.",
  "caminho": "/pacientes",
  "campos": [ { "campo": "cpf", "mensagem": "CPF deve conter 11 dígitos, sem pontos ou traço" } ]
}
```

| Status | Quando acontece |
| --- | --- |
| 400 | JSON malformado, campos inválidos, parâmetro com tipo errado |
| 401 | Sem token, token inválido/expirado ou login incorreto |
| 403 | Perfil sem permissão para a rota |
| 404 | Recurso ou rota inexistente |
| 409 | Dado duplicado ou registro em uso |
| 422 | Regra de negócio violada |
| 500 | Erro inesperado (o detalhe vai só para o log) |

---

## Logs

- Cada requisição gera uma linha como `GET /consultas -> 200 (35 ms)`.
- Todas as linhas de uma mesma requisição têm o mesmo id (`[req:86bb1527]`), o que facilita investigar problemas.
- Os services registram as operações importantes (cadastros, cancelamentos, logins).
- Os logs aparecem no console e também em `logs/gestao-clinica.log`, com rotação a cada 10 MB e histórico de 7 dias.

---

## Testes

```bash
./mvnw test      # ou mvnw.cmd test no Windows
```

Os testes usam o banco H2 em memória (perfil `test`), então não precisam de MySQL.

| Classe | Tipo | O que verifica |
| --- | --- | --- |
| `ConsultaRepositoryTest` | Repositório (`@DataJpaTest`) | Filtros, paginação e detecção de horário ocupado |
| `ConsultaServiceTest` | Unitário (Mockito) | Agendamento, conflitos, transições de status |
| `PacienteServiceTest` | Unitário (Mockito) | Criptografia de senha e CPF duplicado |
| `JwtServiceTest` | Unitário | Token válido, adulterado, de outra chave e expirado |
| `ApiIntegrationTest` | Integração (MockMvc) | Login, 401/403/404/400, fluxo completo de atendimento |
| `GestaoClinicaApplicationTests` | Fumaça | A aplicação sobe sem erros |

---

## Próximos passos

- Restringir cada paciente às próprias consultas (hoje qualquer usuário logado lista todas)
- Migrações de banco versionadas (Flyway)
- Endpoint para o usuário trocar a própria senha
- Pipeline de CI (GitHub Actions) rodando os testes a cada push
- Deploy em um serviço de hospedagem

---

## Autor

**Raphael Nogueira**

Projeto desenvolvido para fins de prática e experiência profissional.
