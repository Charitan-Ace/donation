package ace.charitan.donation.internal.service;

import java.util.List;

import org.springframework.data.domain.Page;

import ace.charitan.common.dto.project.ExternalProjectDto;
import ace.charitan.donation.internal.dto.CreateDonationRequestDto;
import ace.charitan.donation.internal.dto.InternalDonationDto;
import ace.charitan.donation.internal.dto.UpdateDonationRequestDto;

public interface InternalDonationService {
    InternalDonationDto createDonation(CreateDonationRequestDto dto) throws Exception;

    InternalDonationDto getDonationById(Long id);

    Page<InternalDonationDto> getAllDonations(int page, int limit);

    InternalDonationDto updateDonation(Long id, UpdateDonationRequestDto dto);

    void deleteDonation(Long id);

    Double getDonationProjectDonationAmount(String projectId);

    Page<InternalDonationDto> getDonationsByUserId(int page, int limit) throws Exception;

    /*
     * Test of Binh
     */
    List<ExternalProjectDto> getProjectListByCharityId(String charityId);
}
