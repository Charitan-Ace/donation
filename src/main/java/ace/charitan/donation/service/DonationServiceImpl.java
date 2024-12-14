package ace.charitan.donation.service;

import ace.charitan.donation.external.service.ExternalDonationService;
import ace.charitan.donation.internal.dto.DonationRequestDto;
import ace.charitan.donation.internal.dto.InternalDonationDto;
import ace.charitan.donation.internal.service.InternalDonationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
class DonationServiceImpl implements InternalDonationService, ExternalDonationService {

    private final DonationRepository repository;

    public DonationServiceImpl(DonationRepository repository) {
        this.repository = repository;
    }
    @Override
    public Donation createDonation(DonationRequestDto dto) {
        Donation donation = new Donation();
        donation.setFirstName(dto.getFirstName());
        donation.setLastName(dto.getLastName());
        donation.setAddress(dto.getAddress());
        donation.setEmail(dto.getEmail());
        donation.setAmount(dto.getAmount());
        donation.setMessage(dto.getMessage());
        donation.setTransactionStripeId(dto.getTransactionStripeId());
        donation.setProjectId(dto.getProjectId());
        donation.setDonorId(dto.getDonorId());

        return repository.save(donation);
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
    public Donation updateDonation(Long id, DonationRequestDto dto) {
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
        if (dto.getTransactionStripeId() != null) {
            donation.setTransactionStripeId(dto.getTransactionStripeId());
        }
        if (dto.getProjectId() != null) {
            donation.setProjectId(dto.getProjectId());
        }
        if (dto.getDonorId() != null) {
            donation.setDonorId(dto.getDonorId());
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
