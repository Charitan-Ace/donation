package ace.charitan.donation.internal.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ace.charitan.common.dto.project.ExternalProjectDto;
import ace.charitan.common.dto.project.GetProjectByCharityIdDto.GetProjectByCharityIdRequestDto;
import ace.charitan.common.dto.project.GetProjectByCharityIdDto.GetProjectByCharityIdResponseDto;
import ace.charitan.donation.external.dto.ExternalDonationDto;
import ace.charitan.donation.external.service.ExternalDonationService;
import ace.charitan.donation.internal.auth.AuthModel;
import ace.charitan.donation.internal.auth.AuthUtils;
import ace.charitan.donation.internal.dto.CreateDonationRequestDto;
import ace.charitan.donation.internal.dto.InternalDonationDto;
import ace.charitan.donation.internal.dto.UpdateDonationRequestDto;

import ace.charitan.donation.internal.utils.DateUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;


@Service
class DonationServiceImpl implements InternalDonationService, ExternalDonationService {
    final private DonationRepository repository;
    final private KafkaMessageProducer producer;

    private DonationServiceImpl(
            DonationRepository repository,
            KafkaMessageProducer producer) {
        this.repository = repository;
        this.producer = producer;
    }

    @Override
    public Donation createDonation(CreateDonationRequestDto dto) throws Exception {
        //TODO: HANDLE DONATE AS GUEST
        Donation donation = new Donation();
        donation.setAmount(dto.getAmount());
        donation.setMessage(dto.getMessage());
        donation.setProjectId(dto.getProjectId());

        AuthModel authModel = AuthUtils.getUserDetails();
        if (authModel == null) {
            throw new Exception("Cannot get auth model");
        }

        if (dto.getDonorId() != null) {
            donation.setDonorId(dto.getDonorId());
        } else {
            String userId = authModel.getUsername();
            donation.setDonorId(userId);
        }

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
    public Double getProjectDonationAmount(String projectId) {
        List<Donation> projectDonations = repository.findAllByProjectId(projectId);
        return projectDonations.stream()
                .map(Donation::getAmount)
                .reduce(0.0, Double::sum);
    }

    @Override
    public Map<String, Double> getDonorDonationStatistics(String donorId) {
        List<Donation> donorDonations = repository.findAllByDonorId(donorId);

        Map<String, Double> projectDonationTotals = new HashMap<>();

        for (Donation donation : donorDonations) {
            if (donation.getDonorId().equals(donorId)) {
                String projectId = donation.getProjectId();
                Double amount = donation.getAmount();

                projectDonationTotals.put(projectId,
                        projectDonationTotals.getOrDefault(projectId, 0.0) + amount);
            }
        }

        return projectDonationTotals;
    }

    public Map<String, Double> getCharityDonationStatistics(List<String> projectIds) {
        Map<String, Double> projectDonationTotals = new HashMap<>();

        for (String projectId: projectIds) {
            List<Donation> projectDonations = repository.findAllByProjectId(projectId);
            for (Donation donation: projectDonations) {
                if (donation.getProjectId().equals(projectId)) {
                    Double amount = donation.getAmount();

                    projectDonationTotals.put(projectId,
                            projectDonationTotals.getOrDefault(projectId, 0.0) + amount);
                }
            }
        }
        return projectDonationTotals;
    }

    public Map<String, Double> getDonorsOfTheMonth() {
        List<Donation> currentMonthDonations = repository.findAllByCreatedAtBetween(DateUtils.getStartOfMonth(), DateUtils.getEndOfMonth());

        return getGetTopDonorsAndAmount(currentMonthDonations);
    }

    public Map<String, Double> getCharityDonorsOfTheMonth(List<String> projectIds) {
        List<Donation> currentMonthDonations = repository.findAllByProjectIdInAndCreatedAtBetween(
                projectIds,
                DateUtils.getStartOfMonth(),
                DateUtils.getEndOfMonth()
        );

        return getGetTopDonorsAndAmount(currentMonthDonations);
    }

    private Map<String, Double> getGetTopDonorsAndAmount(List<Donation> currentMonthDonations) {
        Map<String, Double> aggregatedDonations = new HashMap<>();

        for (Donation donation : currentMonthDonations) {
            String donorId = donation.getDonorId();
            double amount = donation.getAmount();

            aggregatedDonations.put(donorId, aggregatedDonations.getOrDefault(donorId, 0.0) + amount);
        }

        List<Map.Entry<String, Double>> donationList = new ArrayList<>(aggregatedDonations.entrySet());

        donationList.sort((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()));

        Map<String, Double> topDonors = new LinkedHashMap<>();

        for (int i = 0; i < Math.min(10, donationList.size()); i++) {
            Map.Entry<String, Double> entry = donationList.get(i);
            topDonors.put(entry.getKey(), entry.getValue());
        }

        return topDonors;
    }

    @Override
    public List<ExternalProjectDto> getProjectListByCharityId(String charityId) {
        GetProjectByCharityIdResponseDto responseDto = producer
                .sendAndReceive(new GetProjectByCharityIdRequestDto(charityId, new ArrayList<>()));
        return responseDto.getProjectDtoList();
    }

    @Override
    public List<String> getProjectIdsByDonorId(String donorId) {
        List<Donation> donations = repository.findAllByDonorId(donorId);

        Set<String> projectIds = new HashSet<>();
        for (Donation donation : donations) {
            projectIds.add(donation.getProjectId());
        }

        return new ArrayList<>(projectIds);
    }

}
