package ace.charitan.donation.external.consumer;


import ace.charitan.common.dto.donation.DonationDto;
import ace.charitan.common.dto.donation.GetDonationByIdDto;
import ace.charitan.common.dto.donation.UpdateDonationStripeIdDto;
import ace.charitan.donation.external.dto.ExternalDonationDto;
import ace.charitan.donation.external.service.ExternalDonationService;
import ace.charitan.donation.internal.dto.UpdateDonationRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
class KafkaMessageConsumer {
    @Autowired
    private ExternalDonationService service;

    @KafkaListener(topics = "update-donation-stripe-id", groupId = "donation")
    public void handleUpdateDonationStripeId(UpdateDonationStripeIdDto dto) {
        UpdateDonationRequestDto updateDto = new UpdateDonationRequestDto(null, null, dto.getTransactionStripeId());

        service.updateDonation(dto.getId(), updateDto);
    }

    @KafkaListener(topics = "donation-info", groupId = "donation")
    @SendTo
    public DonationDto handleGetDonationDto(GetDonationByIdDto reqDto) {
        ExternalDonationDto dto = service.getDonationById(reqDto.getId());
        return new DonationDto(dto.getId(), dto.getAmount(), dto.getMessage(), dto.getTransactionStripeId(), dto.getProjectId(), dto.getDonorId(),dto.getCreatedAt());
    }

}
