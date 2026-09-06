# Arquitetura de Microservices com Spring Boot e Apache Kafka

Este repositório contém meus estudos e implementações práticas de **Arquitetura de Microservices utilizando Java, Spring Boot e Apache Kafka**.

Ao longo do curso, será desenvolvido um sistema distribuído completo, aplicando conceitos e tecnologias utilizados em arquiteturas modernas de backend e ambientes corporativos.

---

**[🚀 Quick Start (PT-BR)](#-quick-start)** · **[🚀 Quick Start (EN)](#-quick-start-1)**

---

## 🎯 Objetivos de Aprendizado

O principal objetivo deste projeto é compreender como projetar, desenvolver, integrar e operar serviços independentes utilizando princípios de **Microservices Architecture** e **Event-Driven Architecture**.

### Arquitetura de Microservices

- Compreender os fundamentos da arquitetura de microservices.
- Explorar independência e escalabilidade dos serviços.
- Entender vantagens e desafios em comparação com arquiteturas monolíticas.
- Estudar comunicação, integração e monitoramento em sistemas distribuídos.
- Aplicar princípios para desenvolver serviços independentes e de fácil manutenção.

### Spring Boot

- Desenvolver microservices independentes utilizando Spring Boot.
- Criar aplicações leves, escaláveis e preparadas para ambientes de produção.
- Explorar o ecossistema Spring para desenvolvimento de aplicações backend distribuídas.
- Aplicar padrões e boas práticas do desenvolvimento moderno de aplicações Java.

### Apache Kafka

- Compreender comunicação orientada a eventos entre microservices.
- Publicar e consumir eventos utilizando Apache Kafka.
- Trabalhar com tópicos e partições.
- Compreender distribuição de mensagens e balanceamento de carga.
- Implementar comunicação assíncrona entre serviços distribuídos.

### Docker

O Docker será utilizado para executar a infraestrutura e os serviços externos necessários para a aplicação.

A utilização de containers permitirá reproduzir uma abordagem semelhante às práticas utilizadas em ambientes profissionais e de produção.

### Bancos de Dados Independentes

Cada microservice possuirá seu próprio banco de dados, seguindo o princípio de **database-per-service**.

Essa abordagem permite manter o isolamento dos dados e possibilita que os serviços evoluam de maneira independente, proporcionando maior flexibilidade à arquitetura.

### Event-Driven Architecture

O projeto implementará fluxos orientados a eventos, nos quais os serviços poderão se comunicar por meio de eventos em vez de depender exclusivamente de comunicação HTTP síncrona.

O objetivo é compreender padrões e práticas utilizados em sistemas distribuídos e arquiteturas assíncronas.

### Webhooks

Também serão estudados Webhooks para integração com sistemas externos e processamento de eventos gerados externamente por meio de callbacks HTTP.

### Object Storage com MinIO

O MinIO será utilizado para implementar armazenamento de objetos compatível com a **API do Amazon S3**.

Serão abordados:

- Upload e armazenamento de arquivos.
- Recuperação de arquivos.
- Gerenciamento de objetos.
- Integração entre object storage e microservices.

### JasperReports

O JasperReports será utilizado para geração de relatórios dinâmicos e profissionais integrados aos microservices.

## 🏗️ Projeto

Ao longo do curso será desenvolvido um sistema distribuído completo envolvendo os seguintes domínios:

- **Produtos**
- **Clientes**
- **Pedidos**
- **Faturamento**
- **Logística**

O sistema será desenvolvido progressivamente, incorporando os conceitos apresentados durante o curso e resultando em uma aplicação prática baseada em microservices.

## 🛠️ Tecnologias

As principais tecnologias e conceitos utilizados neste repositório são:

- Java
- Spring Boot
- Apache Kafka
- Docker
- PostgreSQL
- MinIO: Um Object Storage compatível com Amazon S3
- JasperReports
- REST APIs
- Webhooks
- Event-Driven Architecture
- Microservices Architecture

## 🚀 Quick Start

### Pré-requisitos

- Docker e Docker Compose
- Make
- OpenSSL (para geração de senhas)

### Comandos Disponíveis

Execute `make help` para ver todos os comandos:

| Comando               | Descrição                                     |
| --------------------- | --------------------------------------------- |
| `make create-secrets` | Cria secrets interativamente                  |
| `make up`             | Sobe o stack de produção                      |
| `make down`           | Para o stack de produção (preserva dados)     |
| `make clean`          | Remove containers, dados e imagem do Postgres |
| `make dev-up`         | Sobe o banco de desenvolvimento (porta 5555)  |
| `make dev-down`       | Para o banco de desenvolvimento               |
| `make dev-clean`      | Remove container e dados de desenvolvimento   |

### Como os Secrets Funcionam

**`.env` NÃO é onde os secrets ficam.** Isso é um equívoco comum.

O arquivo `.env` fornece **valores padrão** para nomes de usuários e bancos de dados — não senhas. As senhas são geradas aleatoriamente pelo script `create-secrets`.

O fluxo:

```
.env (padrões)  →  create-secrets.sh (interativo)  →  arquivos secrets/  →  Docker Compose
```

| Arquivo     | Conteúdo                          | No Git | Finalidade                                |
| ----------- | --------------------------------- | ------ | ----------------------------------------- |
| `.env`      | Nomes de usuários, DBs, portas    | Não    | Valores padrão oferecidos durante criação |
| `secrets/*` | Todos os valores incluindo senhas | Não    | Secrets reais montados nos containers     |

**O que o `.env` fornece:**

- `DB_SUPER_USER=postgres` — nome padrão do superusuário
- `PRODUTOS_DB_NAME=products` — nome padrão do banco de dados
- `DB_PORT_EXTERNAL=5555` — mapeamento de portas

**O que o `.env` NÃO contém:**

- Senhas (geradas aleatoriamente, 32 caracteres)
- Qualquer credencial sensível

### Primeira Execução

```bash
# 1. (Opcional) Edite o .env com seus nomes de usuário/banco preferidos
vim services/database/.env

# 2. Crie os secrets interativamente
make create-secrets

# 3. Suba o stack
make up
```

O script `create-secrets` vai:

- Carregar o `.env` como valores padrão
- Perguntar se você quer manter ou alterar cada nome de usuário e banco
- Gerar senhas aleatórias de 32 caracteres automaticamente
- Mostrar um resumo antes de criar qualquer coisa
- Salvar todos os valores em `services/database/secrets/`

### Uso no Dia a Dia

**`make up` verifica os secrets automaticamente:**

- Se os secrets não existem → oferece para criá-los
- Se os secrets têm mais de 30 dias → oferece para recriar
- Se os secrets estão atualizados → sobe o stack diretamente

**Rotação manual de secrets:**

```bash
make create-secrets   # Execute novamente o script interativo
make up              # Reinicie com os novos secrets
```

### Notas de Segurança

- `.env` e `secrets/` estão no `.gitignore` — nunca são commitados
- Senhas são geradas com `openssl rand` (32 caracteres)
- Arquivos de secret têm `chmod 600` (somente leitura pelo proprietário)
- Spring Boot lê os secrets de `/run/secrets/` via `configtree`
- Secrets nunca aparecem em `docker inspect` ou variáveis de ambiente

## 🚀 O que espero aprender

Ao concluir este projeto, espero adquirir experiência prática no desenvolvimento e na arquitetura de sistemas backend distribuídos utilizando tecnologias modernas do ecossistema Java.O foco não será apenas aprender cada ferramenta individualmente, mas compreender **como essas tecnologias se integram para formar uma arquitetura completa baseada em microservices**.

---

# Java Microservices Architecture with Spring Boot and Apache Kafka

This repository contains my studies and practical implementation of **Microservices Architecture using Java, Spring Boot, and Apache Kafka**.

Throughout this course, I will build a complete distributed system while applying concepts and technologies commonly used in modern backend and enterprise environments.

## 🎯 Learning Goals

The main goal of this project is to understand how to design, develop, integrate, and operate independent services using modern microservices and event-driven architecture principles.

### Microservices Architecture

- Understand the fundamentals of microservices architecture.
- Explore service independence and scalability.
- Understand the advantages and challenges compared to monolithic architectures.
- Study communication, integration, and monitoring in distributed systems.
- Apply principles for designing independent and maintainable services.

### Spring Boot

- Build independent Spring Boot microservices.
- Develop lightweight, scalable, and production-oriented applications.
- Explore the Spring ecosystem for distributed backend applications.
- Apply common patterns and practices for modern Java backend development.

### Apache Kafka

- Understand event-driven communication between microservices.
- Produce and consume events using Apache Kafka.
- Work with topics and partitions.
- Understand message distribution and load balancing.
- Implement asynchronous communication between distributed services.

### Docker

Docker will be used to run the infrastructure and external services required by the application.

This includes containerizing and managing development dependencies in a way that resembles practices commonly adopted in production environments.

### Independent Databases

Each microservice will have its own database, following the **database-per-service** principle.

This approach allows the services to maintain data isolation and evolve independently while providing greater flexibility in the overall architecture.

### Event-Driven Architecture

The project will implement event-driven workflows where services communicate through events rather than relying exclusively on synchronous HTTP communication.

The goal is to understand practical patterns used in distributed systems and asynchronous architectures.

### Webhooks

The project will also explore Webhooks for integrating external systems and processing externally generated events through HTTP callbacks.

### Object Storage with MinIO

MinIO will be used to implement object storage compatible with the **Amazon S3 API**.

The project will cover:

- File upload and storage.
- File retrieval.
- Object management.
- Integration between object storage and microservices.

### JasperReports

JasperReports will be used to generate dynamic and professional reports integrated with the microservices.

## 🏗️ Project

Throughout the course, a complete distributed system will be developed around the following domains:

- **Products**
- **Customers**
- **Orders**
- **Billing**
- **Logistics**

The system will progressively incorporate the concepts covered throughout the course, resulting in a practical microservices-based application.

## 🛠️ Technologies

The main technologies and tools covered in this repository include:

- Java
- Spring Boot
- Apache Kafka
- Docker
- PostgreSQL
- MinIO: a Amazon S3-compatible Object Storage
- JasperReports
- REST APIs
- Webhooks
- Event-Driven Architecture
- Microservices Architecture

## 🚀 Quick Start

### Prerequisites

- Docker and Docker Compose
- Make
- OpenSSL (for password generation)

### Available Commands

Run `make help` to see all commands:

| Command               | Description                                 |
| --------------------- | ------------------------------------------- |
| `make create-secrets` | Create secrets interactively                |
| `make up`             | Start production stack                      |
| `make down`           | Stop production stack (preserves data)      |
| `make clean`          | Remove containers, data, and Postgres image |
| `make dev-up`         | Start development database (port 5555)      |
| `make dev-down`       | Stop development database                   |
| `make dev-clean`      | Remove dev container and data               |

### How Secrets Work

**`.env` is NOT where secrets live.** This is a common misconception.

The `.env` file provides **default values** for user names and database names — not passwords. Passwords are generated randomly by the `create-secrets` script.

The flow:

```
.env (defaults)  →  create-secrets.sh (interactive)  →  secrets/ files  →  Docker Compose
```

| File        | Contains                       | In Git | Purpose                                |
| ----------- | ------------------------------ | ------ | -------------------------------------- |
| `.env`      | User names, DB names, ports    | No     | Default values offered during creation |
| `secrets/*` | All values including passwords | No     | Actual secrets mounted into containers |

**What `.env` provides:**

- `DB_SUPER_USER=postgres` — default superuser name
- `PRODUTOS_DB_NAME=products` — default database name
- `DB_PORT_EXTERNAL=5555` — port mapping

**What `.env` does NOT contain:**

- Passwords (generated randomly, 32 chars)
- Any sensitive credentials

### First Time Setup

```bash
# 1. (Optional) Edit .env with your preferred user/db names
vim services/database/.env

# 2. Create secrets interactively
make create-secrets

# 3. Start the stack
make up
```

The `create-secrets` script will:

- Load `.env` as default values
- Prompt you to confirm or change each user name and database name
- Generate random 32-character passwords automatically
- Show a summary before creating anything
- Write all values to `services/database/secrets/`

### Day-to-Day Usage

**`make up` checks secrets automatically:**

- If secrets are missing → offers to create them
- If secrets are older than 30 days → offers to recreate
- If secrets are fresh → starts the stack directly

**Manual secret rotation:**

```bash
make create-secrets   # Re-run the interactive script
make up              # Restart with new secrets
```

### Security Notes

- `.env` and `secrets/` are gitignored — never committed
- Passwords are generated with `openssl rand` (32 chars)
- Secret files have `chmod 600` (owner read-only)
- Spring Boot reads secrets from `/run/secrets/` via `configtree`
- Secrets never appear in `docker inspect` or environment variables

## 🚀 What I Expect to Learn

By completing this project, I expect to gain practical experience designing and implementing distributed backend systems using modern Java technologies.

The focus is not only on learning individual tools, but on understanding **how these technologies work together to form a complete microservices architecture**.
