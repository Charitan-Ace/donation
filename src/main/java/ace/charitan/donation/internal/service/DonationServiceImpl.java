package ace.charitan.donation.internal.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ace.charitan.common.dto.auth.AuthRequestByEmailDto;
import ace.charitan.common.dto.auth.AuthRequestByIdDto;
import ace.charitan.common.dto.auth.ExternalAuthDto;
import ace.charitan.common.dto.auth.RegisterGuestDto;
import ace.charitan.common.dto.email.donation.EmailDonationCreationDto;
import ace.charitan.common.dto.payment.CreateDonationPaymentRedirectUrlRequestDto;
import ace.charitan.donation.internal.dto.CreateDonationResponseDto;
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

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


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
    public CreateDonationResponseDto createDonation(CreateDonationRequestDto dto) throws Exception {
        Donation donation = new Donation();
        donation.setAmount(dto.getAmount());
        donation.setMessage(dto.getMessage());
        donation.setProjectId(dto.getProjectId());

        AuthModel authModel = AuthUtils.getUserDetails();

        String userId = "";
        if (authModel != null) {
            userId = authModel.getUsername();
        } else {
            if (dto.getEmail() != null) {
                ExternalAuthDto authDto = producer.getAuthDtoByEmail(new AuthRequestByEmailDto(dto.getEmail()));
                if (authDto == null) {
                    Map<String, String> profile = new HashMap<>() {{
                        if (dto.getFirstName() != null) {
                            put("firstName", dto.getFirstName());
                        }
                        if (dto.getLastName() != null) {
                            put("lastName", dto.getLastName());
                        }
                        if (dto.getAddress() != null) {
                            put("address", dto.getAddress());
                        }
                    }};
                    userId = producer.createGuestAccount(new RegisterGuestDto(dto.getEmail(), profile)).id();
                } else {
                    userId = authDto.id();
                }
            } else {
                throw new RuntimeException("You must provide an email, first name, last name and address when donating as guest");
            }
        }
        donation.setDonorId(userId);

        Donation savedDonation = repository.save(donation);

        String redirectUrl = producer.createPaymentRedirectUrl(new CreateDonationPaymentRedirectUrlRequestDto(userId, savedDonation.getId(), savedDonation.getAmount(), dto.getSuccessUrl(), dto.getCancelUrl())).getRedirectUrl();

//        producer.sendDonationEmail(savedDonation);

        return new CreateDonationResponseDto(savedDonation.getId(), savedDonation.getAmount(), savedDonation.getMessage(), savedDonation.getTransactionStripeId(), savedDonation.getProjectId(), savedDonation.getDonorId(), savedDonation.getCreatedAt(), redirectUrl);
    }

    @Override
    public void createMonthlyDonation(Double amount, String message, String transactionStripeId, String projectId, String donorId) throws ExecutionException, InterruptedException, TimeoutException {
        Donation donation = new Donation(null, amount, message, transactionStripeId, projectId, donorId, null);

        Donation savedDonation = repository.save(donation);
        producer.sendDonationEmail(new EmailDonationCreationDto(savedDonation.getDonorId(), "Monthly donation to project " + projectId, "Your subscription for project " + projectId + " has been charged for this month."));

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
    public Page<InternalDonationDto> getDonationByProjectIdInternal(String projectId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        return repository.findAllByProjectId(projectId, pageable)
                .map(model -> model);
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
    public Donation updateDonation(Long id, UpdateDonationRequestDto dto) throws ExecutionException, InterruptedException, TimeoutException {
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
            producer.sendDonationEmail(new EmailDonationCreationDto(donation.getDonorId(), "One time donation to project " + donation.getProjectId(), "Your donation to project " + donation.getProjectId() + " has been successfully processed"));
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

    public Map<String, Double> getCharityDonationStatistics(List<String> projectIds, String time) {
        Map<String, Double> projectDonationTotals = new HashMap<>();

        LocalDate startDate = null;
        if (!time.equalsIgnoreCase("all")) {
            LocalDate currentDate = LocalDate.now();
            switch (time.toLowerCase()) {
                case "week":
                    startDate = currentDate.minusWeeks(1);
                    break;
                case "month":
                    startDate = currentDate.minusMonths(1);
                    break;
                case "year":
                    startDate = currentDate.minusYears(1);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid time frame: " + time);
            }
        }

        for (String projectId: projectIds) {
            System.out.println(projectId);
            List<Donation> projectDonations = new ArrayList<>();
            if (time.equalsIgnoreCase("all")) {
                projectDonations = repository.findAllByProjectId(projectId);
            } else {
                projectDonations = repository.findAllByProjectIdAndCreatedAtAfter(projectId, startDate);
            }
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
