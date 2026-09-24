package br.com.rodrigodip.orders.client;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;
import org.springframework.web.service.registry.ImportHttpServices;

@Configuration
@EnableConfigurationProperties(HttpClientsProperties.class)
@ImportHttpServices(group = "products", types = { ProductServiceClient.class })
@ImportHttpServices(group = "clients", types = { ClientServiceClient.class })
public class HttpClientConfiguration {

    @Bean
    RestClientHttpServiceGroupConfigurer groupConfigurer(HttpClientsProperties properties) {
        return groups -> groups.forEachGroup((group, clientBuilder, factoryBuilder) -> {
            String baseUrl = properties.getBaseUrls().get(group.name());
            if (baseUrl != null) {
                clientBuilder.baseUrl(baseUrl);
            }
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            requestFactory.setConnectTimeout(properties.getConnectTimeout());
            requestFactory.setReadTimeout(properties.getReadTimeout());
            clientBuilder.requestFactory(requestFactory);
        });
    }
}
