package ace.charitan.donation.external.service;


import ace.charitan.donation.external.dto.ExternalDonationDto;
import ace.charitan.donation.internal.dto.CreateDonationRequestDto;
import ace.charitan.donation.internal.dto.UpdateDonationRequestDto;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import java.util.List;
import java.util.concurrent.TimeoutException;

public interface ExternalDonationService {
    void createMonthlyDonation(Double amount, String message, String transactionStripeId, String projectId, String donorId) throws ExecutionException, InterruptedException, TimeoutException;
    ExternalDonationDto updateDonation(Long id, UpdateDonationRequestDto dto) throws ExecutionException, InterruptedException, TimeoutException;
    ExternalDonationDto getDonationById(Long id);
    List<ExternalDonationDto> getDonationByProjectId(String projectId);
    Map<String, Double> getCharityDonationStatistics(List<String> projectIds, String time);
    Map<String, Double> getDonorDonationStatistics(String donorId);
    Map<String, Double> getDonorsOfTheMonth();
    Map<String, Double> getCharityDonorsOfTheMonth(List<String> projectIds);
    List<String> getProjectIdsByDonorId(String donorId);
}
