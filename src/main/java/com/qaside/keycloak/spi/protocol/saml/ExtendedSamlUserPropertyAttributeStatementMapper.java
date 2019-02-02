/*
 * All materials herein: Copyright (c) 2000-2019 Qaside. All Rights Reserved.
 *
 * These materials are owned by Qaside and are protected by copyright laws
 * and international copyright treaties, as well as other intellectual property laws
 * and treaties.
 *
 * All right, title and interest in the copyright, confidential information,
 * patents, design rights and all other intellectual property rights of
 * whatsoever nature in and to these materials are and shall remain the sole
 * and exclusive property of Qaside.
 */

package com.qaside.keycloak.spi.protocol.saml;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.keycloak.dom.saml.v2.assertion.AttributeStatementType;
import org.keycloak.models.AuthenticatedClientSessionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.ProtocolMapperUtils;
import org.keycloak.protocol.saml.mappers.AbstractSAMLProtocolMapper;
import org.keycloak.protocol.saml.mappers.AttributeStatementHelper;
import org.keycloak.protocol.saml.mappers.SAMLAttributeStatementMapper;
import org.keycloak.provider.ProviderConfigProperty;

/**
 * Keycloak SPI: extended SAML user property attribute statement mapper
 *
 * @author Serhii Shymkiv
 */
public class ExtendedSamlUserPropertyAttributeStatementMapper extends AbstractSAMLProtocolMapper
    implements SAMLAttributeStatementMapper {
  private static final List<ProviderConfigProperty> CONFIG_PROPERTIES = new ArrayList<>();

  private static final String COMMA_REG_EXP = ",";
  private static final String PROVIDER_ID = "ext-saml-user-property-mapper";
  private static final String DISPLAY_TYPE = "Extended User Properties";
  private static final String HELP_TEXT =
      "Map a built in user properties (like email, firstName, lastName or all together) "
          + "to a SAML attribute type.";

  static {
    ProviderConfigProperty property = new ProviderConfigProperty();

    property.setName(ProtocolMapperUtils.USER_ATTRIBUTE);
    property.setLabel(ProtocolMapperUtils.USER_MODEL_PROPERTY_LABEL);
    property.setHelpText(ProtocolMapperUtils.USER_MODEL_PROPERTY_HELP_TEXT);
    CONFIG_PROPERTIES.add(property);

    AttributeStatementHelper.setConfigProperties(CONFIG_PROPERTIES);
  }

  public static ProtocolMapperModel createAttributeMapper(
      String name,
      String userAttribute,
      String samlAttributeName,
      String nameFormat,
      String friendlyName) {
    return AttributeStatementHelper.createAttributeMapper(
        name, userAttribute, samlAttributeName, nameFormat, friendlyName, PROVIDER_ID);
  }

  @Override
  public String getId() {
    return PROVIDER_ID;
  }

  @Override
  public String getDisplayType() {
    return DISPLAY_TYPE;
  }

  @Override
  public String getDisplayCategory() {
    return AttributeStatementHelper.ATTRIBUTE_STATEMENT_CATEGORY;
  }

  @Override
  public String getHelpText() {
    return HELP_TEXT;
  }

  @Override
  public List<ProviderConfigProperty> getConfigProperties() {
    return CONFIG_PROPERTIES;
  }

  @Override
  public void transformAttributeStatement(
      AttributeStatementType attributeStatement,
      ProtocolMapperModel mappingModel,
      KeycloakSession session,
      UserSessionModel userSession,
      AuthenticatedClientSessionModel clientSession) {
    List<String> propertiesParts = new LinkedList<>();
    UserModel userModel = userSession.getUser();

    for (String propertyName :
        mappingModel.getConfig().get(ProtocolMapperUtils.USER_ATTRIBUTE).split(COMMA_REG_EXP)) {
      Optional.ofNullable(ProtocolMapperUtils.getUserModelValue(userModel, propertyName))
          .filter(propertyValue -> !propertyValue.isEmpty())
          .ifPresent(propertiesParts::add);
    }

    if (propertiesParts.isEmpty()) {
      return;
    }

    AttributeStatementHelper.addAttribute(
        attributeStatement, mappingModel, String.join(" ", propertiesParts));
  }
}
