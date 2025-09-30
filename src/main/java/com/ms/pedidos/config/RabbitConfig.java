package com.ms.pedidos.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RabbitConfig {

    public static final String ENTRADA = "pedidos.entrada.lucas";

    public static final String DLQ = "pedidos.entrada.lucas.dlq";

    public static final String SUCESSO = "pedidos.status.sucesso.lucas";

    public static final String FALHA = "pedidos.status.falha.lucas";

    @Bean
    public Jackson2JsonMessageConverter converter() {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {

        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter());
        return template;
    }

    @Bean
    public Queue entradaQueue() {

        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "");
        args.put("x-dead-letter-routing-key", DLQ);
        return new Queue(ENTRADA, true, false, false, args);
    }

    @Bean
    public Queue dlqQueue() {

        return new Queue(DLQ, true);
    }

    @Bean
    public Queue sucessoQueue() {

        return new Queue(SUCESSO, true);
    }

    @Bean
    public Queue falhaQueue() {

        return new Queue(FALHA, true);
    }
}
