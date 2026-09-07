# --- VARIÁVEIS DE CONFIGURAÇÃO ---
DOCKER_CMD = sudo docker
COMPOSE    = $(DOCKER_CMD) compose

SECRETS_PATH  = ./infra
COMPOSE_PROD  = -f docker-compose.yaml
COMPOSE_DEV   = -f docker-compose-dev.yaml

IMAGE_POSTGRES = postgres:18

.PHONY: help up down clean dev-up dev-down dev-clean create-secrets check-secrets

.DEFAULT_GOAL := help

help: ## Mostra esta tela de ajuda com os comandos disponíveis
	@echo "Opções disponíveis no Makefile:"
	@echo "--------------------------------"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-15s\033[0m %s\n", $$1, $$2}'
	@echo "--------------------------------"

# --- SECRETS ---
check-secrets: ## Verifica se existem secrets e se estão atualizados
	@if [ ! -f $(SECRETS_PATH)/secrets/db_super_password ]; then \
		echo "Secrets not found."; \
		read -p "Create secrets now? (Y/n): " -n 1 -r; echo; \
		if [ "$$REPLY" != "N" ] && [ "$$REPLY" != "n" ]; then \
			bash infra/create-secrets.sh; \
		fi; \
	elif [ $$(( ( $$(date +%s) - $$(stat -c %Y $(SECRETS_PATH)/secrets/db_super_password) ) / 86400 )) -gt 30 ]; then \
		echo "Secrets are older than 30 days."; \
		read -p "Recreate secrets? (y/N): " -n 1 -r; echo; \
		if [ "$$REPLY" = "y" ] || [ "$$REPLY" = "Y" ]; then \
			bash infra/create-secrets.sh; \
		fi; \
	fi

create-secrets: ## Cria secrets interativamente (usuários, senhas geradas randomicamente)
	bash infra/create-secrets.sh

# --- AMBIENTE PRINCIPAL (PROD) ---
up: check-secrets ## Sobe os containers de produção em segundo plano (rebuild sempre)
	$(COMPOSE) $(COMPOSE_PROD) up -d --build

down: ## Apenas para os containers de produção (PRESERVA OS DADOS)
	$(COMPOSE) $(COMPOSE_PROD) down

clean: ## Destrutivo: Remove containers, volumes, todas as imagens e os dados
	$(COMPOSE) $(COMPOSE_PROD) down -v --rmi all
	sudo rm -rf $(SECRETS_PATH)/data

# --- AMBIENTE DE DESENVOLVIMENTO (DEV) ---
dev-up: ## Sobe o banco de desenvolvimento local (Porta 5555)
	$(COMPOSE) $(COMPOSE_DEV) up -d

dev-down: ## Apenas para o container-dev (PRESERVA OS DADOS DE TESTE)
	$(COMPOSE) $(COMPOSE_DEV) down

dev-clean: ## Destrutivo: Remove o container-dev, apaga a pasta 'data_dev' e a imagem Postgres
	$(COMPOSE) $(COMPOSE_DEV) down -v
	sudo rm -rf $(SECRETS_PATH)/data_dev
	-$(DOCKER_CMD) rmi $(IMAGE_POSTGRES) 2>/dev/null || true
