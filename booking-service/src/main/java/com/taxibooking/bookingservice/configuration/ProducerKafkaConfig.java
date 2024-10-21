package com.taxibooking.bookingservice.configuration;

import com.taxibooking.bookingservice.model.BookingCancelledDTO;
import com.taxibooking.bookingservice.model.BookingRequestDTO;
import com.taxibooking.bookingservice.model.DriverTriggerDTO;
import com.taxibooking.bookingservice.model.PaymentTriggerDTO;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ProducerKafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${booking.request.topic}")
    private String bookingRequestTopic;

    @Value("${booking.cancelled.topic}")
    private String bookingCancelledTopic;

    @Value("${payment.trigger.topic}")
    private String paymentTriggerTopic;

    @Value("${payment.stage.topic}")
    private String paymentStageTopic;

    @Value("${driver.trigger.topic}")
    private String driverTriggerTopic;

    @Value("${driver.stage.topic}")
    private String driverStageTopic;

    private Map<String, Object> producerConfigs() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        return configProps;
    }

    private <T> ProducerFactory<String, T> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, BookingRequestDTO> kafkaTemplateForBookingRequest() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public KafkaTemplate<String, BookingCancelledDTO> kafkaTemplateForBookingCancelled() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public KafkaTemplate<String, PaymentTriggerDTO> kafkaTemplateForPaymentTrigger() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public KafkaTemplate<String, DriverTriggerDTO> kafkaTemplateForDriverTrigger() {
        return new KafkaTemplate<>(producerFactory());
    }


    private NewTopic createTopic(String topicName) {
        return TopicBuilder
                .name(topicName)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic bookingRequestEventTopic() {
        return createTopic(bookingRequestTopic);
    }

    @Bean
    public NewTopic bookingCancelledEventTopic() {
        return createTopic(bookingCancelledTopic);
    }

    @Bean
    public NewTopic paymentTriggerEventTopic() {
        return createTopic(paymentTriggerTopic);
    }

    @Bean
    public NewTopic paymentStageEventTopic() {
        return createTopic(paymentStageTopic);
    }

    @Bean
    public NewTopic driverTriggerEventTopic() {
        return createTopic(driverTriggerTopic);
    }

    @Bean
    public NewTopic driverStageEventTopic() {
        return createTopic(driverStageTopic);
    }
}
