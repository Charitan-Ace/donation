package ace.charitan.donation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

@Configuration
class KafkaConsumerConfig {
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory,
            ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate
    ) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setReplyTemplate(replyingKafkaTemplate);
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}

