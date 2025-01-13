package ace.charitan.donation.internal.controller;

import ace.charitan.common.dto.project.ExternalProjectDto;
import ace.charitan.donation.internal.dto.CreateDonationRequestDto;
import ace.charitan.donation.internal.dto.CreateDonationResponseDto;
import ace.charitan.donation.internal.dto.InternalDonationDto;
import ace.charitan.donation.internal.dto.UpdateDonationRequestDto;
import ace.charitan.donation.internal.service.InternalDonationService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
class DonationController {
    @Autowired
    private InternalDonationService service;

    @PostMapping("/")
    public ResponseEntity<CreateDonationResponseDto> createDonation(@RequestBody CreateDonationRequestDto dto) throws Exception {
        CreateDonationResponseDto response = service.createDonation(dto);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InternalDonationDto> getDonationById(@PathVariable Long id) {
        InternalDonationDto donation = service.getDonationById(id);
        return ResponseEntity.ok(donation);
    }

    @RolesAllowed({"DONOR", "CHARITY"})
    @GetMapping
    public ResponseEntity<Page<InternalDonationDto>> getAllDonations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        Page<InternalDonationDto> donations = service.getAllDonations(page, limit);
        return ResponseEntity.ok(donations);
    }

    @GetMapping("/donor/my-donations")
    public ResponseEntity<Page<InternalDonationDto>> getAllUserDonations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) throws Exception {
        Page<InternalDonationDto> donations = service.getDonationsByUserId(page, limit);
        return ResponseEntity.ok(donations);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<Page<InternalDonationDto>> getAllDonationsByProjectId(
            @PathVariable(name = "projectId") String projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) throws Exception {
        Page<InternalDonationDto> donations = service.getDonationByProjectIdInternal(projectId, page, limit);
        return ResponseEntity.ok(donations);
    }

    @GetMapping("/project-total-amount")
    public ResponseEntity<Double> getProjectDonationAmount(@RequestParam(name = "projectId") String projectId) {
        Double totalAmount = service.getProjectDonationAmount(projectId);
        return ResponseEntity.ok(totalAmount);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InternalDonationDto> updateDonation(
            @PathVariable Long id, @RequestBody UpdateDonationRequestDto dto) throws ExecutionException, InterruptedException, TimeoutException {
        InternalDonationDto updatedDonation = service.updateDonation(id, dto);
        return ResponseEntity.ok(updatedDonation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDonation(@PathVariable Long id) {
        service.deleteDonation(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/test/project/{charityId}")
    public ResponseEntity<List<ExternalProjectDto>> getProjectListByCharityId(@PathVariable String charityId) {
        List<ExternalProjectDto> projectDtoList = service.getProjectListByCharityId(charityId);
        return new ResponseEntity<>(projectDtoList, HttpStatus.OK);
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
