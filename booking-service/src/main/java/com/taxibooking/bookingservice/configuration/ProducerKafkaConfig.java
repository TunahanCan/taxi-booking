package com.taxibooking.bookingservice.configuration;


import com.taxibooking.bookingservice.model.BookingRequestDTO;
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

    @Bean
    public ProducerFactory<String, BookingRequestDTO> producerFactoryForBookingRequest() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, BookingRequestDTO> kafkaTemplateForBookingRequest() {
        return new KafkaTemplate<>(producerFactoryForBookingRequest());
    }

    @Bean
    public NewTopic bookingRequestEventTopic() {
        return TopicBuilder
                .name(bookingRequestTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic bookingCancelledEventTopic() {
        return TopicBuilder
                .name(bookingCancelledTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
