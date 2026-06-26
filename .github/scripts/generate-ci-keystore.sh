#!/usr/bin/env bash
set -euo pipefail

KEYSTORE_DIR="environment/keys"
KEYSTORE_FILE="${KEYSTORE_DIR}/ci.jks"
KEY_ALIAS="ci-shortx"
KEY_PASSWORD="ci123456"
STORE_PASSWORD="ci123456"

mkdir -p "${KEYSTORE_DIR}"

if [ -f "${KEYSTORE_FILE}" ]; then
    echo "CI keystore already exists, skipping generation."
    exit 0
fi

echo "Generating CI keystore..."

keytool -genkeypair \
    -v \
    -keystore "${KEYSTORE_FILE}" \
    -alias "${KEY_ALIAS}" \
    -keyalg RSA \
    -keysize 2048 \
    -validity 365 \
    -storepass "${STORE_PASSWORD}" \
    -keypass "${KEY_PASSWORD}" \
    -dname "CN=CI Build, OU=ShortX, O=CI, L=Unknown, ST=Unknown, C=US"

echo "CI keystore generated at ${KEYSTORE_FILE}"
