package com.nilga.taskmanager.messaging;

import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up RabbitMQ messaging components.
 * This class defines beans for RabbitTemplate, message converter,
 * and RabbitAdmin, providing essential RabbitMQ configuration.
 */
@Configuration
public class RabbitMQConfig {

    /**
     * Name of the RabbitMQ queue used for task messaging.
     */
    public static final String QUEUE_NAME = "task_queue";

    /**
     * Configures and returns a RabbitTemplate bean for sending messages.
     *
     * @param connectionFactory the connection factory for RabbitMQ
     * @return a configured RabbitTemplate instance with JSON message conversion
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    /**
     * Configures and returns a Jackson2JsonMessageConverter bean for message serialization.
     *
     * @return a Jackson2JsonMessageConverter instance
     */
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Configures and returns a RabbitAdmin bean for managing RabbitMQ resources.
     *
     * @param connectionFactory the connection factory for RabbitMQ
     * @return a RabbitAdmin instance
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}
