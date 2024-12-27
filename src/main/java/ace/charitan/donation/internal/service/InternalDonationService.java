package ace.charitan.donation.internal.service;

import ace.charitan.donation.internal.dto.CreateDonationRequestDto;
import ace.charitan.donation.internal.dto.InternalDonationDto;
import ace.charitan.donation.internal.dto.UpdateDonationRequestDto;
import org.springframework.data.domain.Page;

public interface InternalDonationService {
    InternalDonationDto createDonation(CreateDonationRequestDto dto);
    InternalDonationDto getDonationById(Long id);
    Page<InternalDonationDto> getAllDonations(int page, int limit);
    InternalDonationDto updateDonation(Long id, UpdateDonationRequestDto dto);
    void deleteDonation(Long id);
}
