package ace.charitan.donation.internal.service;

import ace.charitan.donation.external.dto.ExternalDonationDto;
import ace.charitan.donation.external.service.ExternalDonationService;
import ace.charitan.donation.internal.auth.AuthModel;
import ace.charitan.donation.internal.auth.AuthUtils;
import ace.charitan.donation.internal.dto.CreateDonationRequestDto;
import ace.charitan.donation.internal.dto.InternalDonationDto;
import ace.charitan.donation.internal.dto.UpdateDonationRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
class DonationServiceImpl implements InternalDonationService, ExternalDonationService {
    final private DonationRepository repository;
    final private KafkaMessageProducer producer;

    private DonationServiceImpl(
            DonationRepository repository,
            KafkaMessageProducer producer
    ) {
        this.repository = repository;
        this.producer = producer;
    }

    @Override
    public Donation createDonation(CreateDonationRequestDto dto) throws Exception {
        Donation donation = new Donation();
        donation.setAmount(dto.getAmount());
        donation.setMessage(dto.getMessage());
        donation.setProjectId(dto.getProjectId());

        AuthModel authModel = AuthUtils.getUserDetails();
        if (authModel == null) {
            throw new Exception("Cannot get auth model");
        }

        String userId = authModel.getUsername();
        donation.setDonorId(userId);

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
    public List<ExternalDonationDto> getDonationByProjectId(String projectId) {
        return repository.findAllByProjectId(projectId)
                .stream().map(model -> (ExternalDonationDto) model).toList();
    }

    @Override
    public Page<InternalDonationDto> getDonationsByUserId(int page, int limit) throws Exception {
        AuthModel authModel = AuthUtils.getUserDetails();
        if (authModel == null) {
            throw new Exception("Cannot get auth model");
        }

        String userId = authModel.getUsername();
        Pageable pageable = PageRequest.of(page, limit);
        return repository.findAllByDonorId(userId, pageable).map(donation -> donation);
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

    @Override
    public Double getDonationProjectDonationAmount(String projectId) {
        List<Donation> projectDonations = repository.findAllByProjectId(projectId);
        return projectDonations.stream()
                .map(Donation::getAmount)
                .reduce(0.0, Double::sum);
    }

    @Override
    public Map<String, Double> getDonorTotalProjectAndValue(String donorId) {
        List<Donation> donorDonations = repository.findAllByDonorId(donorId);

       Map<String, Double> projectDonationTotals = new HashMap<>();

        for (Donation donation: donorDonations) {
            if (donation.getDonorId().equals(donorId)) {
                String projectId = donation.getProjectId();
                Double amount = donation.getAmount();

                projectDonationTotals.put(projectId,
                        projectDonationTotals.getOrDefault(projectId, 0.0) + amount);
            }
        }

        return projectDonationTotals;

    }

}
