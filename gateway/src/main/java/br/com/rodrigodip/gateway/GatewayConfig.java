package br.com.rodrigodip.gateway;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

/**
 * Declares the gateway routes (path predicate -> target URI) and the global
 * CORS configuration. The downstream targets use Docker network hostnames.
 */
@Configuration
public class GatewayConfig implements WebMvcConfigurer {

    @Bean
    RouterFunction<ServerResponse> productsRoute() {
        return route("products")
                .route(path("/products").or(path("/products/**")), http())
                .before(uri("http://product-service:8081"))
                .build();
    }

    @Bean
    RouterFunction<ServerResponse> clientsRoute() {
        return route("clients")
                .route(path("/clients").or(path("/clients/**")), http())
                .before(uri("http://client-service:8082"))
                .build();
    }

    @Bean
    RouterFunction<ServerResponse> ordersRoute() {
        return route("orders")
                .route(path("/orders").or(path("/orders/**")), http())
                .before(uri("http://order-service:8083"))
                .build();
    }

    @Bean
    RouterFunction<ServerResponse> invoicesRoute() {
        return route("invoices")
                .route(path("/invoices").or(path("/invoices/**")), http())
                .before(uri("http://invoicing-service:8084"))
                .build();
    }

    @Bean
    RouterFunction<ServerResponse> shipmentsRoute() {
        return route("shipments")
                .route(path("/shipments").or(path("/shipments/**")), http())
                .before(uri("http://logistics-service:8085"))
                .build();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowedHeaders("*");
    }
}
