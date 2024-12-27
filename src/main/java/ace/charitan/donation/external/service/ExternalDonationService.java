package ace.charitan.donation.external.service;


import ace.charitan.donation.external.dto.ExternalDonationDto;
import ace.charitan.donation.internal.dto.UpdateDonationRequestDto;

public interface ExternalDonationService {
    ExternalDonationDto updateDonation(Long id, UpdateDonationRequestDto dto);
}
