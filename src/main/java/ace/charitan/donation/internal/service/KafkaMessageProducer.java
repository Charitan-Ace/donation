package ace.charitan.donation.internal.service;

import ace.charitan.donation.external.dto.ExternalDonationDto;
import ace.charitan.donation.internal.dto.InternalDonationDto;
import ace.charitan.donation.internal.dto.TestKafkaMessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
class KafkaMessageProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

//    public void sendMessage(String topic, String message) {
//        System.out.println("Project microservice sent message: " + message);
//        kafkaTemplate.send(topic, message);
//    }

    public void sendDonationNotification(Donation donation) {
        TestKafkaMessageDto dto = new TestKafkaMessageDto(donation.getFirstName(), donation.getMessage());
        kafkaTemplate.send("donation-notification", dto);
    }

}