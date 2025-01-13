package ace.charitan.donation.internal.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findAllByProjectId(String projectId);
    List<Donation> findAllByDonorId(String donorId);
    Page<Donation> findAllByDonorId(String donorId, Pageable pageable);
    List<Donation> findAllByCreatedAtBetween(LocalDate startDate, LocalDate endDate);
    List<Donation> findAllByProjectIdInAndCreatedAtBetween(List<String> projectIds, LocalDate startDate, LocalDate endDate);
    Page<Donation> findAllByProjectId(String projectId, Pageable pageable);
}
