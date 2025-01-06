package ace.charitan.donation.internal.service;

import ace.charitan.common.dto.TestKafkaMessageDto;
import ace.charitan.donation.external.service.ExternalDonationService;
import ace.charitan.donation.internal.dto.CreateDonationRequestDto;
import ace.charitan.donation.internal.dto.InternalDonationDto;
import ace.charitan.donation.internal.dto.UpdateDonationRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
class DonationServiceImpl implements InternalDonationService, ExternalDonationService {
    // TODO: THIS IS THE TEST USER ID. REMOVE WHEN ABLE TO EXTRACT FROM JWT
    private final String userId = "123";

    @Autowired
    private DonationRepository repository;

    @Autowired
    private KafkaMessageProducer producer;

    @Override
    public Donation createDonation(CreateDonationRequestDto dto) throws ExecutionException, InterruptedException {
        Donation donation = new Donation();
        donation.setAmount(dto.getAmount());
        donation.setMessage(dto.getMessage());
        donation.setProjectId(dto.getProjectId());
        donation.setDonorId(dto.getDonorId());

        Donation savedDonation = repository.save(donation);

//        producer.sendDonationNotification(savedDonation);
        TestKafkaMessageDto response = producer.testRequestResponse();
        System.out.println(response);

        return savedDonation;
    }

    @Override
    public Donation getDonationById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donation not found"));
    }

    @Override
    public Page<InternalDonationDto> getAllDonations(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        return repository.findAll(pageable).map(donation -> donation);
    }

    @Override
    public Donation updateDonation(Long id, UpdateDonationRequestDto dto) {
        Donation donation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donation not found"));

        if (dto.getAmount() != null) {
            donation.setAmount(dto.getAmount());
        }
        if (dto.getMessage() != null) {
            donation.setMessage(dto.getMessage());
        }

        if (dto.getTransactionStripeId() != null) {
            donation.setTransactionStripeId(dto.getTransactionStripeId());
        }

        return repository.save(donation);
    }

    @Override
    public void deleteDonation(Long id) {
        Donation donation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donation not found"));
        repository.delete(donation);
    }
}
