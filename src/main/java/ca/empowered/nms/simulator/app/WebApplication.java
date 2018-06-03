package ca.empowered.nms.simulator.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import ca.empowered.nms.simulator.amqp.RMQObjectReceiver;
import ca.empowered.nms.simulator.amqp.RMQReceiver;


/**
 * Entry point for spring framework
 * @author mboparai
 *
 */
@SpringBootApplication
@EnableWebSocketMessageBroker
@ComponentScan("ca.empowered.nms.simulator")
@EnableJpaRepositories(basePackages = "ca.empowered.nms.simulator.db.dao")
@EntityScan(basePackages="ca.empowered.nms.simulator.db.model")
@PropertySource("classpath:application.properties")
public class WebApplication extends SpringBootServletInitializer implements WebSocketMessageBrokerConfigurer {

	private static final Logger log = LogManager.getLogger(WebApplication.class.getName());

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
    
    /**
     * This method initializes spring in tomcat
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
    	log.debug("Starting web application");
    	return builder.sources(WebApplication.class);
    }
    
    // rabbit mq configurations
    
    public static final String topicExchangeName = "network-simulator-exchange";
    private static final String queueName = "network-simulator-queue";
    // all messages starting with ca.empowered.nms.simulator.
    private static final String routingKey = "ca.empowered.nms.simulator.test";

    @Bean
    Queue queue() {
        return new Queue(queueName, false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName);
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(RMQReceiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }
    
    // our exchanges
    
    public static final String objectTopicExchangeName = "eni-network-simulator-exchange";
    private static final String objectQueueName = "eni-network-simulator-queue";
    private static final String objectRoutingKey = "ca.empowered.nms.simulator.object.#";
    
    @Bean
    Queue objectQueue() {
        return new Queue(objectQueueName, false);
    }

    @Bean
    TopicExchange objectExchange() {
        return new TopicExchange(objectTopicExchangeName);
    }

    @Bean
    Binding objectBinding(Queue objectQueue, TopicExchange objectExchange) {
        return BindingBuilder.bind(objectQueue).to(objectExchange).with(objectRoutingKey);
    }

    @Bean
    SimpleMessageListenerContainer objectContainer(ConnectionFactory connectionFactory,
            MessageListenerAdapter objectListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(objectQueueName);
        // https://sivalabs.in/2018/02/springboot-messaging-rabbitmq/
        //container.setMessageConverter(new Jackson2JsonMessageConverter(new ObjectMapper()));
        container.setMessageListener(objectListenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter objectListenerAdapter(RMQObjectReceiver rmqObjectReceiver) {
        return new MessageListenerAdapter(rmqObjectReceiver, "receiveMessage");
    }
    
    // web socket configurations
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/websocket-route");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket")
			        .setAllowedOrigins("*")	// for development only
			        .withSockJS();
    }
    
}
