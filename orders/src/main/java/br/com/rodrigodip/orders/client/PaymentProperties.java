package br.com.rodrigodip.orders.client;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.payment")
public class PaymentProperties {

    private int maxAttempts = 3;
    private String callbackApiKey;
    private Duration placedTtl = Duration.ofMinutes(5);

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public String getCallbackApiKey() {
        return callbackApiKey;
    }

    public void setCallbackApiKey(String callbackApiKey) {
        this.callbackApiKey = callbackApiKey;
    }

    public Duration getPlacedTtl() {
        return placedTtl;
    }

    public void setPlacedTtl(Duration placedTtl) {
        this.placedTtl = placedTtl;
    }
}
