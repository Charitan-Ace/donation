package ace.charitan.donation.external.consumer;


import ace.charitan.donation.external.service.ExternalDonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
class KafkaMessageConsumer {
    @Autowired
    private ExternalDonationService service;

    @KafkaListener(topics = "donation-notification-response", groupId = "donation")
    public void handleDonationNotificationResponse(String message) {
        System.out.println("Donation microservice received message: " + message);
    }

    @KafkaListener(topics = "donation-payment-response", groupId = "donation")
    public void handleDonationPaymentResponse(String message) {
        System.out.println("Donation microservice received message: " + message);
    }


}
