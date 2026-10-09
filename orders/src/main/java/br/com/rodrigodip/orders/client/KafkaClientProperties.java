package br.com.rodrigodip.orders.client;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka")
public class KafkaClientProperties {

    private Map<String, String> topics = new HashMap<>();
    private String serverUrl;
    private String acks = "all";
    private int retries = 3;
    private Duration maxBlockMs = Duration.ofSeconds(5);

    public Map<String, String> getTopics() {
        return topics;
    }

    public void setTopics(Map<String, String> topics) {
        this.topics = topics;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getAcks() {
        return acks;
    }

    public void setAcks(String acks) {
        this.acks = acks;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public Duration getMaxBlockMs() {
        return maxBlockMs;
    }

    public void setMaxBlockMs(Duration maxBlockMs) {
        this.maxBlockMs = maxBlockMs;
    }

}
