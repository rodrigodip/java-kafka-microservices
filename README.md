# Arquitetura de Microservices com Spring Boot e Apache Kafka

Este repositório contém meus estudos e implementações práticas de **Arquitetura de Microservices utilizando Java, Spring Boot e Apache Kafka**.

Ao longo do curso, será desenvolvido um sistema distribuído completo, aplicando conceitos e tecnologias utilizados em arquiteturas modernas de backend e ambientes corporativos.

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

## 🚀 What I Expect to Learn

By completing this project, I expect to gain practical experience designing and implementing distributed backend systems using modern Java technologies.

The focus is not only on learning individual tools, but on understanding **how these technologies work together to form a complete microservices architecture**.
