package ace.charitan.donation.external.service;


import ace.charitan.donation.external.dto.ExternalDonationDto;
import ace.charitan.donation.internal.dto.UpdateDonationRequestDto;

import java.util.List;

public interface ExternalDonationService {
    ExternalDonationDto updateDonation(Long id, UpdateDonationRequestDto dto);
    ExternalDonationDto getDonationById(Long id);
    List<ExternalDonationDto> getDonationByProjectId(String projectId);
}
