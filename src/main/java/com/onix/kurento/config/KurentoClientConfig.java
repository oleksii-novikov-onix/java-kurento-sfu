package com.onix.kurento.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.KurentoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class KurentoClientConfig {

    @Bean
    public KurentoClient kurentoClient() {
        return KurentoClient.create();
    }

}
