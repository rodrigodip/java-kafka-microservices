package br.com.rodrigodip.invoicing.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(InvoicingProperties.class)
public class InvoicingConfiguration {
}
