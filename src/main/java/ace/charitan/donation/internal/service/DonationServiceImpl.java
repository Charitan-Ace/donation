package ace.charitan.donation.internal.service;

import ace.charitan.donation.external.service.ExternalDonationService;
import ace.charitan.donation.internal.dto.CreateDonationRequestDto;
import ace.charitan.donation.internal.dto.InternalDonationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
class DonationServiceImpl implements InternalDonationService, ExternalDonationService {

    @Autowired
    private DonationRepository repository;

    @Autowired
    private KafkaMessageProducer producer;

    @Override
    public Donation createDonation(CreateDonationRequestDto dto) {
        Donation donation = new Donation();
        donation.setFirstName(dto.getFirstName());
        donation.setLastName(dto.getLastName());
        donation.setAddress(dto.getAddress());
        donation.setEmail(dto.getEmail());
        donation.setAmount(dto.getAmount());
        donation.setMessage(dto.getMessage());
//        TODO: UPDATE THE PROJECT, DONOR and STRIPE ID
        donation.setTransactionStripeId(dto.getTransactionStripeId());
        donation.setProjectId(dto.getProjectId());
        donation.setDonorId(dto.getDonorId());

        Donation savedDonation = repository.save(donation);

        producer.sendDonationNotification(savedDonation);

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
    public Donation updateDonation(Long id, CreateDonationRequestDto dto) {
        Donation donation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donation not found"));

        if (dto.getFirstName() != null) {
            donation.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            donation.setLastName(dto.getLastName());
        }
        if (dto.getAddress() != null) {
            donation.setAddress(dto.getAddress());
        }
        if (dto.getEmail() != null) {
            donation.setEmail(dto.getEmail());
        }
        if (dto.getAmount() != null) {
            donation.setAmount(dto.getAmount());
        }
        if (dto.getMessage() != null) {
            donation.setMessage(dto.getMessage());
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
