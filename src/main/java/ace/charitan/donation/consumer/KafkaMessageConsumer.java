package ace.charitan.donation.consumer;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaMessageConsumer {

    @KafkaListener(topics = "test", groupId = "project")
    public void listen(String message) {
        System.out.println("Project microservice received message: " + message);
    }

}

