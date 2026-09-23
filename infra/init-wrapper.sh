#!/bin/bash
set -e

echo "========================================================================="
echo "Provisioning databases..."
echo "========================================================================="

# Read credentials from Docker secrets (prod) or environment variables (dev)
if [ -f /run/secrets/products_db_user ]; then
    echo "Reading credentials from Docker secrets..."
    PRODUCTS_DB_USER=$(cat /run/secrets/products_db_user)
    PRODUCTS_DB_PASSWORD=$(cat /run/secrets/products_db_password)
    PRODUCTS_DB_NAME=$(cat /run/secrets/products_db_name)
    CLIENTS_DB_USER=$(cat /run/secrets/clients_db_user)
    CLIENTS_DB_PASSWORD=$(cat /run/secrets/clients_db_password)
    CLIENTS_DB_NAME=$(cat /run/secrets/clients_db_name)
    ORDERS_DB_USER=$(cat /run/secrets/orders_db_user)
    ORDERS_DB_PASSWORD=$(cat /run/secrets/orders_db_password)
    ORDERS_DB_NAME=$(cat /run/secrets/orders_db_name)
else
    echo "Reading credentials from environment variables..."
    # Use env vars already set by docker-compose environment:
    # PRODUCTS_DB_USER, PRODUCTS_DB_PASSWORD, PRODUCTS_DB_NAME
    # CLIENTS_DB_USER, CLIENTS_DB_PASSWORD, CLIENTS_DB_NAME
    # ORDERS_DB_USER, ORDERS_DB_PASSWORD, ORDERS_DB_NAME
    :
fi

# POSTGRES_USER, POSTGRES_PASSWORD, POSTGRES_DB are set by
# the POSTGRES_*_FILE pattern (prod) or POSTGRES_* env vars (dev)

echo "Creating users and databases..."

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    -- 1. Create Users
    CREATE USER "$PRODUCTS_DB_USER" WITH PASSWORD '$PRODUCTS_DB_PASSWORD';
    CREATE USER "$CLIENTS_DB_USER" WITH PASSWORD '$CLIENTS_DB_PASSWORD';
    CREATE USER "$ORDERS_DB_USER" WITH PASSWORD '$ORDERS_DB_PASSWORD';

    -- 2. Create Databases with respective owners
    CREATE DATABASE "$PRODUCTS_DB_NAME" OWNER "$PRODUCTS_DB_USER";
    CREATE DATABASE "$CLIENTS_DB_NAME" OWNER "$CLIENTS_DB_USER";
    CREATE DATABASE "$ORDERS_DB_NAME" OWNER "$ORDERS_DB_USER";

    -- 3. Grant Privileges
    GRANT ALL PRIVILEGES ON DATABASE "$PRODUCTS_DB_NAME" TO "$PRODUCTS_DB_USER";
    GRANT ALL PRIVILEGES ON DATABASE "$CLIENTS_DB_NAME" TO "$CLIENTS_DB_USER";
    GRANT ALL PRIVILEGES ON DATABASE "$ORDERS_DB_NAME" TO "$ORDERS_DB_USER";
EOSQL

echo "========================================================================="
echo "Database provisioning complete!"
echo "========================================================================="
