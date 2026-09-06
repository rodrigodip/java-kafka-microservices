#!/bin/bash
# create-secrets.sh - Create secret files for Docker Compose
# Usage: ./create-secrets.sh

set -e

# --- Configuration ---
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
SECRETS_DIR="$SCRIPT_DIR/secrets"
ENV_FILE="$SCRIPT_DIR/.env"

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# --- Helper Functions ---

generate_password() {
    openssl rand -base64 24 | tr -d "=+/" | cut -c1-32
}

create_file_secret() {
    local NAME=$1
    local VALUE=$2

    echo "$VALUE" > "$SECRETS_DIR/$NAME"
    chmod 644 "$SECRETS_DIR/$NAME"
    echo -e "${GREEN}  Created '${NAME}'${NC}" >&2
}

prompt_with_default() {
    local LABEL=$1
    local SECRET_NAME=$2
    local DEFAULT=$3

    local CURRENT
    if [ -f "$SECRETS_DIR/$SECRET_NAME" ]; then
        CURRENT=$(cat "$SECRETS_DIR/$SECRET_NAME")
        echo -e "  ${LABEL} ${BLUE}[current: ${CURRENT}]${NC}" >&2
    else
        CURRENT="$DEFAULT"
        echo -e "  ${LABEL} ${BLUE}[default: ${DEFAULT}]${NC}" >&2
    fi

    read -p "  Press Enter to keep '${CURRENT}', or enter new value: " NEW_VALUE >&2

    if [ -n "$NEW_VALUE" ]; then
        echo "$NEW_VALUE"
    else
        echo "$CURRENT"
    fi
}

# --- Main ---

main() {
    echo -e "\n${BLUE}========================================${NC}" >&2
    echo -e "${BLUE}  Secrets Creation Script${NC}" >&2
    echo -e "${BLUE}========================================${NC}\n" >&2

    # Load .env if it exists
    if [ -f "$ENV_FILE" ]; then
        echo -e "${BLUE}Loading .env...${NC}\n" >&2
        set -a
        source "$ENV_FILE"
        set +a
    fi

    # Create secrets directory
    mkdir -p "$SECRETS_DIR"

    # --- Database Superuser ---
    echo -e "${BLUE}Database Superuser${NC}" >&2
    DB_SUPER_USER=$(prompt_with_default "DB_SUPER_USER" "db_super_user" "$DB_SUPER_USER")
    DB_SUPER_PASSWORD=$(generate_password)
    DB_DEFAULT_NAME=$(prompt_with_default "DB_DEFAULT_NAME" "db_default_name" "$DB_DEFAULT_NAME")

    # --- Products Database ---
    echo -e "\n${BLUE}Products Database${NC}" >&2
    PRODUTOS_DB_USER=$(prompt_with_default "PRODUTOS_DB_USER" "produtos_db_user" "$PRODUTOS_DB_USER")
    PRODUTOS_DB_PASSWORD=$(generate_password)
    PRODUTOS_DB_NAME=$(prompt_with_default "PRODUTOS_DB_NAME" "produtos_db_name" "$PRODUTOS_DB_NAME")

    # --- Clients Database ---
    echo -e "\n${BLUE}Clients Database${NC}" >&2
    CLIENTES_DB_USER=$(prompt_with_default "CLIENTES_DB_USER" "clientes_db_user" "$CLIENTES_DB_USER")
    CLIENTES_DB_PASSWORD=$(generate_password)
    CLIENTES_DB_NAME=$(prompt_with_default "CLIENTES_DB_NAME" "clientes_db_name" "$CLIENTES_DB_NAME")

    # --- Summary ---
    echo -e "\n${BLUE}========================================${NC}" >&2
    echo -e "${BLUE}  Secret Summary${NC}" >&2
    echo -e "${BLUE}========================================${NC}" >&2
    echo -e "  DB_SUPER_USER:     ${GREEN}${DB_SUPER_USER}${NC}" >&2
    echo -e "  DB_DEFAULT_NAME:   ${GREEN}${DB_DEFAULT_NAME}${NC}" >&2
    echo -e "  PRODUTOS_DB_USER:  ${GREEN}${PRODUTOS_DB_USER}${NC}" >&2
    echo -e "  PRODUTOS_DB_NAME:  ${GREEN}${PRODUTOS_DB_NAME}${NC}" >&2
    echo -e "  CLIENTES_DB_USER:  ${GREEN}${CLIENTES_DB_USER}${NC}" >&2
    echo -e "  CLIENTES_DB_NAME:  ${GREEN}${CLIENTES_DB_NAME}${NC}" >&2
    echo -e "  ${YELLOW}(Passwords will be generated randomly)${NC}" >&2
    echo -e "${BLUE}========================================${NC}" >&2

    # Confirm
    read -p "Create these secrets? (y/N): " -n 1 -r >&2
    echo >&2
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo -e "${YELLOW}Cancelled.${NC}" >&2
        exit 0
    fi

    # Create files
    echo -e "\n${BLUE}Creating secret files...${NC}\n" >&2

    create_file_secret "db_super_user"       "$DB_SUPER_USER"
    create_file_secret "db_super_password"   "$DB_SUPER_PASSWORD"
    create_file_secret "db_default_name"     "$DB_DEFAULT_NAME"

    create_file_secret "produtos_db_user"     "$PRODUTOS_DB_USER"
    create_file_secret "produtos_db_password" "$PRODUTOS_DB_PASSWORD"
    create_file_secret "produtos_db_name"     "$PRODUTOS_DB_NAME"

    create_file_secret "clientes_db_user"     "$CLIENTES_DB_USER"
    create_file_secret "clientes_db_password" "$CLIENTES_DB_PASSWORD"
    create_file_secret "clientes_db_name"     "$CLIENTES_DB_NAME"

    echo -e "\n${GREEN}All secrets created in ${SECRETS_DIR}/${NC}" >&2
    echo -e "${BLUE}Run 'make up' to start the stack.${NC}\n" >&2
}

main
