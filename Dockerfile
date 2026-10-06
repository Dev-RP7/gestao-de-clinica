# =====================================================================
# Dockerfile "multi-stage" (em duas etapas):
#   1) "build": usa uma imagem com Maven + JDK para compilar o projeto e gerar o .jar.
#   2) "runtime": usa uma imagem só com o JRE (menor e mais segura) para rodar o .jar.
# Assim a imagem final não carrega Maven, código-fonte nem dependências de compilação.
# =====================================================================

# ----- Etapa 1: build -----
# Imagem oficial com Maven 3.9 e Java 21. "AS build" dá um nome à etapa.
FROM maven:3.9-eclipse-temurin-21 AS build
# Pasta de trabalho dentro do container.
WORKDIR /app
# Copia primeiro só o pom.xml: o Docker guarda esta etapa em cache e só baixa as dependências de novo se o pom mudar.
COPY pom.xml .
# Baixa as dependências (-B = modo não interativo, -q = menos mensagens).
RUN mvn -B -q dependency:go-offline
# Agora copia o código-fonte.
COPY src ./src
# Compila e empacota o .jar. Os testes rodam fora do Docker (mvnw test), por isso -DskipTests.
RUN mvn -B -q -DskipTests package

# ----- Etapa 2: runtime -----
# Imagem só com o Java Runtime 21 (sem compilador).
FROM eclipse-temurin:21-jre
# Pasta de trabalho.
WORKDIR /app
# Cria um usuário sem privilégios: rodar como root dentro do container é má prática de segurança.
RUN groupadd --system app && useradd --system --gid app app
# Copia o .jar gerado na etapa "build".
COPY --from=build /app/target/gestao-clinica-*.jar app.jar
# Cria a pasta de logs e dá permissão ao usuário "app".
RUN mkdir logs && chown app:app logs
# A partir daqui, tudo roda como o usuário "app".
USER app
# Documenta que a aplicação escuta na porta 8080.
EXPOSE 8080
# Comando que inicia a aplicação quando o container sobe.
ENTRYPOINT ["java", "-jar", "app.jar"]
