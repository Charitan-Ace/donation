package ace.charitan.donation.external.service;


import ace.charitan.donation.external.dto.ExternalDonationDto;
import ace.charitan.donation.internal.dto.CreateDonationRequestDto;
import ace.charitan.donation.internal.dto.UpdateDonationRequestDto;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import java.util.List;

public interface ExternalDonationService {
    void createMonthlyDonation(Double amount, String message, String transactionStripeId, String projectId, String donorId);
    ExternalDonationDto updateDonation(Long id, UpdateDonationRequestDto dto);
    ExternalDonationDto getDonationById(Long id);
    List<ExternalDonationDto> getDonationByProjectId(String projectId);
    Map<String, Double> getCharityDonationStatistics(List<String> projectIds);
    Map<String, Double> getDonorDonationStatistics(String donorId);
    Map<String, Double> getDonorsOfTheMonth();
    Map<String, Double> getCharityDonorsOfTheMonth(List<String> projectIds);
    List<String> getProjectIdsByDonorId(String donorId);
}
