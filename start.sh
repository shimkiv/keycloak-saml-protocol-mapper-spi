#!/bin/bash
# set -x

START_TIME=`date +%s`
SPI_JAR="keycloak-saml-protocol-mapper-spi.jar"

mvn clean package

cp -rf target/${SPI_JAR} keycloak/

docker-compose up -d --build

# docker-compose down
rm -rf keycloak/${SPI_JAR}
mvn clean

RUN_TIME=$((`date +%s` - START_TIME))

echo ""
echo ""
echo ""
echo "[INFO] -----------------------------------------"
echo "[INFO] The Keycloak (ext-proto-mapper) environment is UP and RUNNING"
echo "[INFO] -----------------------------------------"
echo "[INFO] Total time taken: ${RUN_TIME} s"
echo "[INFO] -----------------------------------------"
echo ""
echo ""
echo ""
