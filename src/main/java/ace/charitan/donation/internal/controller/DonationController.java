package ace.charitan.donation.internal.controller;

import ace.charitan.donation.internal.dto.CreateDonationRequestDto;
import ace.charitan.donation.internal.dto.InternalDonationDto;
import ace.charitan.donation.internal.dto.UpdateDonationRequestDto;
import ace.charitan.donation.internal.service.InternalDonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
class DonationController {
    @Autowired
    private InternalDonationService service;

    @PostMapping
    public ResponseEntity<InternalDonationDto> createDonation(@RequestBody CreateDonationRequestDto dto) throws Exception {
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

    @GetMapping("/my-donations")
    public ResponseEntity<Page<InternalDonationDto>> getAllUserDonations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) throws Exception {
        Page<InternalDonationDto> donations = service.getDonationsByUserId(page, limit);
        return ResponseEntity.ok(donations);
    }

    @GetMapping("/project-total-amount")
    public ResponseEntity<Double> getProjectDonationAmount(@RequestParam(name = "projectId") String projectId) {
        Double totalAmount = service.getProjectDonationAmount(projectId);
        return ResponseEntity.ok(totalAmount);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InternalDonationDto> updateDonation(
            @PathVariable Long id, @RequestBody UpdateDonationRequestDto dto) {
        InternalDonationDto updatedDonation = service.updateDonation(id, dto);
        return ResponseEntity.ok(updatedDonation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDonation(@PathVariable Long id) {
        service.deleteDonation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/test")
    public ResponseEntity<Void> test() {
        Map<String, Double> charity = service.getCharityDonationStatistics(List.of("123", "abc"));
        Map<String, Double> donor = service.getDonorDonationStatistics("1a2c6825-4a84-4d38-8c50-c65c0f01c83a");

        System.out.println(charity);
        System.out.println();
        System.out.println(donor);
        return ResponseEntity.noContent().build();
    }
}
