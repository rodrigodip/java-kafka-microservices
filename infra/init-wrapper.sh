#!/bin/bash
set -e

echo "========================================================================="
echo "Provisioning databases..."
echo "========================================================================="

# Read credentials from Docker secrets (prod) or environment variables (dev)
if [ -f /run/secrets/produtos_db_user ]; then
    echo "Reading credentials from Docker secrets..."
    PRODUTOS_DB_USER=$(cat /run/secrets/produtos_db_user)
    PRODUTOS_DB_PASSWORD=$(cat /run/secrets/produtos_db_password)
    PRODUTOS_DB_NAME=$(cat /run/secrets/produtos_db_name)
    CLIENTES_DB_USER=$(cat /run/secrets/clientes_db_user)
    CLIENTES_DB_PASSWORD=$(cat /run/secrets/clientes_db_password)
    CLIENTES_DB_NAME=$(cat /run/secrets/clientes_db_name)
else
    echo "Reading credentials from environment variables..."
    # Use env vars already set by docker-compose environment:
    # PRODUTOS_DB_USER, PRODUTOS_DB_PASSWORD, PRODUTOS_DB_NAME
    # CLIENTES_DB_USER, CLIENTES_DB_PASSWORD, CLIENTES_DB_NAME
    :
fi

# POSTGRES_USER, POSTGRES_PASSWORD, POSTGRES_DB are set by
# the POSTGRES_*_FILE pattern (prod) or POSTGRES_* env vars (dev)

echo "Creating users and databases..."

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    -- 1. Create Users
    CREATE USER "$PRODUTOS_DB_USER" WITH PASSWORD '$PRODUTOS_DB_PASSWORD';
    CREATE USER "$CLIENTES_DB_USER" WITH PASSWORD '$CLIENTES_DB_PASSWORD';

    -- 2. Create Databases with respective owners
    CREATE DATABASE "$PRODUTOS_DB_NAME" OWNER "$PRODUTOS_DB_USER";
    CREATE DATABASE "$CLIENTES_DB_NAME" OWNER "$CLIENTES_DB_USER";

    -- 3. Grant Privileges
    GRANT ALL PRIVILEGES ON DATABASE "$PRODUTOS_DB_NAME" TO "$PRODUTOS_DB_USER";
    GRANT ALL PRIVILEGES ON DATABASE "$CLIENTES_DB_NAME" TO "$CLIENTES_DB_USER";
EOSQL

echo "========================================================================="
echo "Database provisioning complete!"
echo "========================================================================="
