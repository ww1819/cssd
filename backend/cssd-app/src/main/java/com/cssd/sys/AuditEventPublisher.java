package com.cssd.sys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuditEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(AuditEventPublisher.class);
    private final ObjectProvider<RabbitTemplate> rabbit;

    public AuditEventPublisher(ObjectProvider<RabbitTemplate> rabbit) {
        this.rabbit = rabbit;
    }

    public void publish(String routingKey, Map<String, Object> payload) {
        RabbitTemplate t = rabbit.getIfAvailable();
        if (t == null) {
            log.debug("rabbit unavailable, skip {}", routingKey);
            return;
        }
        try {
            t.convertAndSend("cssd.audit", routingKey, payload);
        } catch (Exception e) {
            log.warn("audit event drop: {}", e.getMessage());
        }
    }
}
