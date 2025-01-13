package ace.charitan.donation.internal.service;

import java.util.List;

import ace.charitan.donation.internal.dto.CreateDonationResponseDto;
import org.springframework.data.domain.Page;

import ace.charitan.common.dto.project.ExternalProjectDto;
import ace.charitan.donation.internal.dto.CreateDonationRequestDto;
import ace.charitan.donation.internal.dto.InternalDonationDto;
import ace.charitan.donation.internal.dto.UpdateDonationRequestDto;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


public interface InternalDonationService {
    CreateDonationResponseDto createDonation(CreateDonationRequestDto dto) throws Exception;

    InternalDonationDto getDonationById(Long id);

    Page<InternalDonationDto> getAllDonations(int page, int limit);

    InternalDonationDto updateDonation(Long id, UpdateDonationRequestDto dto) throws ExecutionException, InterruptedException, TimeoutException;

    void deleteDonation(Long id);

    Page<InternalDonationDto> getDonationsByUserId(int page, int limit) throws Exception;

    List<ExternalProjectDto> getProjectListByCharityId(String charityId);

    Double getProjectDonationAmount(String projectId);
    Map<String, Double> getCharityDonationStatistics(List<String> projectIds);
    Map<String, Double> getDonorDonationStatistics(String donorId);

}
