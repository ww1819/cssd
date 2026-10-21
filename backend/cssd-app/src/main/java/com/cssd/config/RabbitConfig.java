package com.cssd.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "cssd.rabbit.enabled", havingValue = "true", matchIfMissing = true)
public class RabbitConfig {
    @Bean
    public TopicExchange cssdAuditExchange() {
        return new TopicExchange("cssd.audit", true, false);
    }
}
