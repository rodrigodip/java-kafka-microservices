package br.com.rodrigodip.invoicing.bucket;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.s3-client")
public class S3ClientProperties {

    private String endpointUrl;
    private String publicEndpointUrl;
    private String bucketName;
    private String accessKey;
    private String secretKey;

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public void setPublicEndpointUrl(String publicEndpointUrl) {
        this.publicEndpointUrl = publicEndpointUrl;
    }

    public String getPublicEndpointUrl() {
        return publicEndpointUrl;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
