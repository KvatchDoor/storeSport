package com.sportstore.infrastructure.adapter.in.rest.cucumber;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PUBLISH_QUIET_PROPERTY_NAME;

/**
 * Point d'entree surefire des scenarios Cucumber de l'adaptateur primaire.
 * <p>
 * Les scenarios traversent toute la pile : REST -> service applicatif -> domaine -> persistance H2.
 * La configuration du contexte Spring vit dans {@link CucumberSpringConfiguration}, les steps dans
 * {@link ArticleCatalogueSteps}.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.sportstore.infrastructure.adapter.in.rest.cucumber")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, summary")
@ConfigurationParameter(key = PLUGIN_PUBLISH_QUIET_PROPERTY_NAME, value = "true")
class ArticleRestCucumberIntegrationTest {
}
