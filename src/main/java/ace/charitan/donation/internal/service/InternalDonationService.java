package ace.charitan.donation.internal.service;

import ace.charitan.donation.internal.dto.DonationRequestDto;
import ace.charitan.donation.internal.dto.InternalDonationDto;
import org.springframework.data.domain.Page;

public interface InternalDonationService {
    InternalDonationDto createDonation(DonationRequestDto dto);
    InternalDonationDto getDonationById(Long id);
    Page<InternalDonationDto> getAllDonations(int page, int limit);
    InternalDonationDto updateDonation(Long id, DonationRequestDto dto);
    void deleteDonation(Long id);
}
