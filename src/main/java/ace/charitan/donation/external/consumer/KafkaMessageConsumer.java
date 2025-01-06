package ace.charitan.donation.external.consumer;


import ace.charitan.common.dto.donation.UpdateDonationStripeIdDto;
import ace.charitan.donation.external.service.ExternalDonationService;
import ace.charitan.donation.internal.dto.UpdateDonationRequestDto;
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

    @KafkaListener(topics = "update-donation-stripe-id", groupId = "donation")
    public void handleUpdateDonationStripeId(UpdateDonationStripeIdDto dto) {
        UpdateDonationRequestDto updateDto = new UpdateDonationRequestDto(null, null, dto.getTransactionStripeId());

        service.updateDonation(dto.getId(), updateDto);
    }


}
