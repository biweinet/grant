package com.bw.swarm.configuration;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(HttpComponentsClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory simpleClientHttpRequestFactory() {

        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(1000 * 60 * 30);
        httpRequestFactory.setReadTimeout(1000 * 60 * 30);
        HttpClient httpClient = HttpClientBuilder.create().useSystemProperties().setRedirectStrategy(new LaxRedirectStrategy()).build();
        httpRequestFactory.setHttpClient(httpClient);
        return httpRequestFactory;

    }

}

