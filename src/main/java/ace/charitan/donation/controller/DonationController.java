package ace.charitan.donation.controller;

import ace.charitan.donation.internal.dto.DonationRequestDto;
import ace.charitan.donation.internal.dto.InternalDonationDto;
import ace.charitan.donation.internal.service.InternalDonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
class DonationController {
    @Autowired
    private InternalDonationService service;

    @PostMapping
    public ResponseEntity<InternalDonationDto> createDonation(@RequestBody DonationRequestDto dto) {
        InternalDonationDto donation = service.createDonation(dto);
        return ResponseEntity.status(201).body(donation);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InternalDonationDto> getDonationById(@PathVariable Long id) {
        InternalDonationDto donation = service.getDonationById(id);
        return ResponseEntity.ok(donation);
    }

    @GetMapping
    public ResponseEntity<Page<InternalDonationDto>> getAllDonations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        Page<InternalDonationDto> donations = service.getAllDonations(page, limit);
        return ResponseEntity.ok(donations);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InternalDonationDto> updateDonation(
            @PathVariable Long id, @RequestBody DonationRequestDto dto) {
        InternalDonationDto updatedDonation = service.updateDonation(id, dto);
        return ResponseEntity.ok(updatedDonation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDonation(@PathVariable Long id) {
        service.deleteDonation(id);
        return ResponseEntity.noContent().build();
    }
}
