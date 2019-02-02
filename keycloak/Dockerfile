FROM jboss/keycloak

ADD keycloak-saml-protocol-mapper-spi.jar /opt/jboss/keycloak/standalone/deployments/

ENTRYPOINT [ "/opt/jboss/tools/docker-entrypoint.sh" ]

CMD ["-b", "0.0.0.0"]
