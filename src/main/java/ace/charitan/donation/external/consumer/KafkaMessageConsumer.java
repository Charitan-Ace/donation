package ace.charitan.donation.external.consumer;


import ace.charitan.common.dto.donation.*;
import ace.charitan.donation.external.dto.ExternalDonationDto;
import ace.charitan.donation.external.service.ExternalDonationService;
import ace.charitan.donation.internal.dto.UpdateDonationRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class KafkaMessageConsumer {
    final private ExternalDonationService service;
    final private Logger logger = LoggerFactory.getLogger(this.getClass());

    private KafkaMessageConsumer(
            ExternalDonationService service
    ) {
        this.service = service;
    }

    @KafkaListener(topics = "update-donation-stripe-id")
    public void handleUpdateDonationStripeId(UpdateDonationStripeIdDto dto) {
        UpdateDonationRequestDto updateDto = new UpdateDonationRequestDto(null, null, dto.getTransactionStripeId());

        service.updateDonation(dto.getId(), updateDto);
    }

    @KafkaListener(topics = "donation.get.id")
    @SendTo
    public DonationDto handleGetDonationById(GetDonationByIdDto reqDto) {
        logger.info("Received request for getting donation by id #{}", reqDto.getId());
        ExternalDonationDto dto = service.getDonationById(reqDto.getId());
        return new DonationDto(dto.getId(), dto.getAmount(), dto.getMessage(), dto.getTransactionStripeId(), dto.getProjectId(), dto.getDonorId(),dto.getCreatedAt());
    }

    @KafkaListener(topics = "donation.get.projectId")
    @SendTo
    public DonationsDto handleGetDonationByProjectId(GetDonationsByProjectIdDto reqDto) {
        logger.info("Received request for getting donation by project id #{}", reqDto.id());
        List<ExternalDonationDto> dtos = service.getDonationByProjectId(reqDto.id());
        logger.debug("{}", dtos.size());
        return new DonationsDto(
                dtos.stream().map(dto -> new DonationDto(
                        dto.getId(), dto.getAmount(), dto.getMessage(), dto.getTransactionStripeId(), dto.getProjectId(), dto.getDonorId(),dto.getCreatedAt()
                )).toList()
        );
    }
}
