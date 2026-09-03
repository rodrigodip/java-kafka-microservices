# --- VARIÁVEIS DE CONFIGURAÇÃO ---
DOCKER_CMD = sudo docker
COMPOSE    = $(DOCKER_CMD) compose

DB_PATH      = ./services/database
COMPOSE_PROD = -f $(DB_PATH)/docker-compose.yaml
COMPOSE_DEV  = -f $(DB_PATH)/docker-compose-dev.yaml

IMAGE_POSTGRES = postgres:18

.PHONY: help up down clean dev-up dev-down dev-clean

.DEFAULT_GOAL := help

help: ## Mostra esta tela de ajuda com os comandos disponíveis
	@echo "Opções disponíveis no Makefile:"
	@echo "--------------------------------"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-15s\033[0m %s\n", $$1, $$2}'
	@echo "--------------------------------"

# --- AMBIENTE PRINCIPAL (PROD) ---
up: ## Sobe os containers de produção em segundo plano
	$(COMPOSE) $(COMPOSE_PROD) up -d

down: ## Apenas para os containers de produção (PRESERVA OS DADOS)
	$(COMPOSE) $(COMPOSE_PROD) down

clean: ## Destrutivo: Remove containers, apaga a pasta 'data' e a imagem Postgres
	$(COMPOSE) $(COMPOSE_PROD) down -v
	sudo rm -rf $(DB_PATH)/data
	-$(DOCKER_CMD) rmi $(IMAGE_POSTGRES) 2>/dev/null || true

# --- AMBIENTE DE DESENVOLVIMENTO (DEV) ---
dev-up: ## Sobe o banco de desenvolvimento local (Porta 5555)
	$(COMPOSE) $(COMPOSE_DEV) up -d

dev-down: ## Apenas para o container-dev (PRESERVA OS DADOS DE TESTE)
	$(COMPOSE) $(COMPOSE_DEV) down

dev-clean: ## Destrutivo: Remove o container-dev, apaga a pasta 'data_dev' e a imagem Postgres
	$(COMPOSE) $(COMPOSE_DEV) down -v
	sudo rm -rf $(DB_PATH)/data_dev
	-$(DOCKER_CMD) rmi $(IMAGE_POSTGRES) 2>/dev/null || true