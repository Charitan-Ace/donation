package ace.charitan.donation.external.service;


import ace.charitan.donation.external.dto.ExternalDonationDto;
import ace.charitan.donation.internal.dto.CreateDonationRequestDto;
import ace.charitan.donation.internal.dto.UpdateDonationRequestDto;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import java.util.List;

public interface ExternalDonationService {
    ExternalDonationDto createDonation(CreateDonationRequestDto dto) throws Exception;
    ExternalDonationDto updateDonation(Long id, UpdateDonationRequestDto dto);
    ExternalDonationDto getDonationById(Long id);
    List<ExternalDonationDto> getDonationByProjectId(String projectId);
    Map<String, Double> getDonorTotalProjectAndValue(String donorId);
}
