#!/bin/sh
set -eu

# Keycloak (Quarkus) does not natively support *_FILE env vars.
# Read the Docker secrets mounted at /run/secrets and export them before
# delegating to the official entrypoint (kc.sh).
if [ -f "$KC_DB_USERNAME_FILE" ]; then
    export KC_DB_USERNAME="$(cat "$KC_DB_USERNAME_FILE")"
fi
if [ -f "$KC_DB_PASSWORD_FILE" ]; then
    export KC_DB_PASSWORD="$(cat "$KC_DB_PASSWORD_FILE")"
fi
if [ -f "$KC_DB_NAME_FILE" ]; then
    export KC_DB_URL="jdbc:postgresql://postgres-db:5432/$(cat "$KC_DB_NAME_FILE")"
fi
if [ -f "$KC_BOOTSTRAP_ADMIN_PASSWORD_FILE" ]; then
    export KC_BOOTSTRAP_ADMIN_PASSWORD="$(cat "$KC_BOOTSTRAP_ADMIN_PASSWORD_FILE")"
fi

exec /opt/keycloak/bin/kc.sh "$@"
