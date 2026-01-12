**Sistema de Gerenciamento de Portfólio de Projetos**
Visão Geral
Este projeto é uma API REST desenvolvida em Java com Spring Boot para gerenciar o portfólio de projetos de uma empresa.
O sistema permite acompanhar todo o ciclo de vida dos projetos, desde a análise inicial até o encerramento, incluindo controle de equipe, orçamento, risco e status.

A aplicação segue boas práticas de arquitetura, separação de camadas, uso de DTOs, regras de negócio bem definidas e testes unitários focados na camada de serviço.

**Tecnologias Utilizadas**
Java 17

Spring Boot

Spring Web

Spring Data JPA (Hibernate)

Spring Security (autenticação básica em memória)

PostgreSQL

Maven

Swagger / OpenAPI

JUnit 5

Mockito

**Arquitetura**
O projeto segue o padrão MVC, com clara separação de responsabilidades:

Controller: exposição dos endpoints REST

Service: regras de negócio

Repository: acesso a dados

DTOs: entrada e saída de dados

Entities: mapeamento JPA

Exception Handler Global: tratamento centralizado de erros

Security Config: configuração de segurança básica

**Funcionalidades**
Projetos
CRUD completo de projetos

Atualização parcial de dados

Avanço de status respeitando a ordem lógica

Cancelamento permitido a qualquer momento

Exclusão bloqueada para projetos iniciados, em andamento ou encerrados

Paginação e filtros na listagem

**Regras de Status
Ordem obrigatória de transição:

-Em análise

-Análise realizada

-Análise aprovada

-Iniciado

-Planejado

-Em andamento

-Encerrado

O status Cancelado pode ser aplicado a qualquer momento.

**Classificação de Risco**
A classificação de risco do projeto é calculada dinamicamente com base em orçamento e prazo:

-Baixo risco

Orçamento até R$ 100.000

Prazo de até 3 meses

-Médio risco

Orçamento entre R$ 100.001 e R$ 500.000

ou prazo entre 3 e 6 meses

-Alto risco

Orçamento acima de R$ 500.000

ou prazo superior a 6 meses

**Membros**
O cadastro de membros não é feito diretamente no sistema

Existe uma API REST externa simulada (mock) para criação e consulta de membros

Cada membro possui:

-Nome

-Cargo (GERENTE ou FUNCIONARIO)

**Regras de Alocação**
Apenas membros com cargo FUNCIONARIO podem ser alocados em projetos

Cada projeto deve ter entre 1 e 10 membros

Um membro não pode participar de mais de 3 projetos ativos (status diferente de encerrado ou cancelado)

O gerente do projeto deve possuir cargo GERENTE

Um gerente não pode gerenciar mais de 3 projetos ativos

**Relatórios**
O sistema disponibiliza um endpoint de relatório resumido contendo:

Quantidade de projetos por status

Total orçado por status

Média de duração dos projetos encerrados

Total de membros únicos alocados no portfólio

**Segurança**
A aplicação utiliza Spring Security com autenticação básica em memória.

-Usuário: admin

-Senha: 1234

Os endpoints do Swagger estão liberados sem autenticação.

**Documentação da API**
A documentação dos endpoints está disponível via Swagger/OpenAPI:

http://localhost:8080/swagger-ui.html

**Testes Unitários**
Os testes unitários estão concentrados principalmente na camada de serviço, onde estão as regras de negócio.

Utilização de JUnit 5 e Mockito

Repositórios são mockados

Cenários testados:

Criação de projeto com sucesso

Validações de gerente

Regras de datas

Busca de projetos

Tratamento de exceções

Cobertura focada nas regras de negócio, atendendo ao requisito mínimo de 70%

Para executar os testes:

mvn test

**Configuração do Banco de Dados**
O projeto utiliza PostgreSQL.

Pré-requisitos
PostgreSQL instalado

Um banco de dados criado (exemplo: portfolio_db)

application.properties (exemplo)
spring.datasource.url=jdbc:postgresql://localhost:5432/portfolio_db
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
As credenciais não devem ser versionadas com dados sensíveis reais.
Cada pessoa que for rodar o projeto deve configurar seu próprio banco local.

**Como Executar o Projeto**
Clonar o repositório

Configurar o banco de dados no application.properties

Garantir que o PostgreSQL esteja em execução

Executar o projeto:

mvn spring-boot:run
Acessar:

Swagger: http://localhost:8080/swagger-ui.html
