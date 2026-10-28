package br.com.rodrigodip.orders.client;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import br.com.rodrigodip.orders.client.dto.InvoiceGeneratedEvent;
import br.com.rodrigodip.orders.client.dto.OrderShippedEvent;

@Configuration
@EnableKafka
public class KafkaConsumerConfiguration {

    @Bean
    public ConsumerFactory<String, InvoiceGeneratedEvent> invoiceGeneratedConsumerFactory(
            KafkaClientProperties properties) {
        Map<String, Object> configProps = consumerConfig(properties);

        return new DefaultKafkaConsumerFactory<>(
                configProps,
                new StringDeserializer(),
                new JacksonJsonDeserializer<>(InvoiceGeneratedEvent.class).ignoreTypeHeaders());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InvoiceGeneratedEvent> kafkaListenerContainerFactory(
            @Qualifier("invoiceGeneratedConsumerFactory") ConsumerFactory<String, InvoiceGeneratedEvent> invoiceGeneratedConsumerFactory,
            KafkaClientProperties properties) {
        ConcurrentKafkaListenerContainerFactory<String, InvoiceGeneratedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(invoiceGeneratedConsumerFactory);
        factory.setConcurrency(properties.getConcurrency());
        factory.setCommonErrorHandler(new DefaultErrorHandler(new FixedBackOff(1000L, 3)));

        return factory;
    }

    @Bean
    public ConsumerFactory<String, OrderShippedEvent> orderShippedConsumerFactory(
            KafkaClientProperties properties) {
        Map<String, Object> configProps = consumerConfig(properties);

        return new DefaultKafkaConsumerFactory<>(
                configProps,
                new StringDeserializer(),
                new JacksonJsonDeserializer<>(OrderShippedEvent.class).ignoreTypeHeaders());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderShippedEvent> orderShippedKafkaListenerContainerFactory(
            @Qualifier("orderShippedConsumerFactory") ConsumerFactory<String, OrderShippedEvent> orderShippedConsumerFactory,
            KafkaClientProperties properties) {
        ConcurrentKafkaListenerContainerFactory<String, OrderShippedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderShippedConsumerFactory);
        factory.setConcurrency(properties.getConcurrency());
        factory.setCommonErrorHandler(new DefaultErrorHandler(new FixedBackOff(1000L, 3)));

        return factory;
    }

    private Map<String, Object> consumerConfig(KafkaClientProperties properties) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getServerUrl());
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, properties.getGroupId());
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, properties.getAutoOffsetReset());
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return configProps;
    }
}
