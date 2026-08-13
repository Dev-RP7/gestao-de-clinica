# Sistema de Gestão de Clínica - API RESTful

Uma API RESTful desenvolvida com **Java** e **Spring Boot** para gerenciamento de uma clínica médica. O sistema permite o cadastro e gerenciamento de usuários, médicos, pacientes, especialidades e consultas, utilizando autenticação baseada em **JWT** e seguindo boas práticas de desenvolvimento.

---

## Tecnologias

- Java 25
- Spring Boot 4.1.0
- Spring Data JPA
- Hibernate
- Spring Security
- JWT (JSON Web Token)
- MySQL
- Maven
- Lombok
- Bean Validation

---

## Arquitetura

O projeto foi desenvolvido seguindo a arquitetura em camadas, promovendo organização, reutilização de código e facilidade de manutenção.

Controller
↓
Service
↓
Repository
↓
Banco de Dados


### Recursos implementados

- API RESTful
- DTOs (Request e Response)
- Conversão entre Entity e DTO
- Arquitetura em camadas (Controller, Service e Repository)
- Bean Validation para validação de dados
- Tratamento global de exceções
- Autenticação e autorização com Spring Security + JWT

---

## Funcionalidades

- Cadastro de usuários
- Cadastro de médicos
- Cadastro de pacientes
- Cadastro de especialidades
- Agendamento de consultas
- Atualização de registros
- Exclusão de registros
- Login com autenticação JWT
- Controle de acesso por perfis de usuário

---

## Banco de Dados

Banco de dados utilizado:

- MySQL

### Entidades

- Usuário
- Médico
- Paciente
- Especialidade
- Consulta

---

## Segurança

O sistema utiliza **Spring Security** juntamente com **JWT (JSON Web Token)** para autenticação e autorização.

Recursos implementados:

- Login autenticado
- Geração de Token JWT
- Proteção dos endpoints
- Controle de acesso baseado em perfis

---

## Como executar o projeto

### 1. Copie a estrutura do banco de dados SQL abaixo

```sql
CREATE DATABASE clinica;
USE clinica;

CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    telefone VARCHAR(15),
    tipo_usuario ENUM('ADMINISTRADOR', 'MEDICO', 'PACIENTE') NOT NULL,
    ativo BOOLEAN DEFAULT TRUE,
    data_criacao DATETIME DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao DATETIME DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE especialidade (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE medico (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE,
    crm VARCHAR(20) NOT NULL UNIQUE,
    especialidade_id BIGINT NOT NULL,

    CONSTRAINT fk_medico_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuario(id),

    CONSTRAINT fk_medico_especialidade
        FOREIGN KEY (especialidade_id)
        REFERENCES especialidade(id)
);

CREATE TABLE paciente (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    rg VARCHAR(20),
    data_nascimento DATE,
    sexo ENUM('MASCULINO', 'FEMININO', 'OUTRO'),

    CONSTRAINT fk_paciente_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuario(id)
);

CREATE TABLE consulta (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    medico_id BIGINT NOT NULL,
    paciente_id BIGINT NOT NULL,
    data_consulta DATETIME NOT NULL,
    observacoes TEXT,
    status ENUM('AGENDADA', 'REALIZADA', 'CANCELADA') DEFAULT 'AGENDADA',

    CONSTRAINT fk_consulta_medico
        FOREIGN KEY (medico_id)
        REFERENCES medico(id),

    CONSTRAINT fk_consulta_paciente
        FOREIGN KEY (paciente_id)
        REFERENCES paciente(id)
);

CREATE TABLE prontuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    consulta_id BIGINT NOT NULL UNIQUE,
    peso DECIMAL(5,2),
    altura DECIMAL(4,2),
    pressao_arterial VARCHAR(20),
    temperatura DECIMAL(4,1),
    diagnostico TEXT,
    tratamento TEXT,
    observacoes TEXT,

    CONSTRAINT fk_prontuario_consulta
        FOREIGN KEY (consulta_id)
        REFERENCES consulta(id)
);

CREATE TABLE receita (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prontuario_id BIGINT NOT NULL,
    medicamento VARCHAR(150) NOT NULL,
    dosagem VARCHAR(100),
    frequencia VARCHAR(100),
    duracao VARCHAR(100),
    observacao TEXT,

    CONSTRAINT fk_receita_prontuario
        FOREIGN KEY (prontuario_id)
        REFERENCES prontuario(id)
);
```

### 2. Clone o repositório

```bash
git clone https://github.com/Dev-RP7/gestao-de-clinica.git
```

### 3. Configure o banco de dados

Edite o arquivo:

src/main/resources/application.properties


Configure:

- URL do banco
- Usuário
- Senha

### 4. Execute a aplicação

A API ficará disponível em:

http://localhost:8080


---

## Principais Endpoints

| Método | Endpoint        | Descrição               |
| ------ | --------------- | ------------------------ |
| POST   | /auth/login     | Realizar login           |
| POST   | /usuarios       | Cadastrar usuário        |
| POST   | /medicos        | Cadastrar médico         |
| POST   | /pacientes      | Cadastrar paciente       |
| POST   | /especialidades | Cadastrar especialidade  |
| POST   | /consultas      | Agendar consulta         |

---

## Roadmap

Próximas melhorias planejadas:

- Implementar documentação com Swagger/OpenAPI
- Criar testes unitários
- Criar testes de integração
- Adicionar Docker
- Publicar a API em um serviço de hospedagem (Deploy)
- Implementar paginação
- Adicionar filtros de pesquisa
- Melhorar tratamento de erros HTTP
- Implementar logs da aplicação

---

## Autor

**Raphael Nogueira**

Projeto desenvolvido para fins de prática e experiência profissional.
