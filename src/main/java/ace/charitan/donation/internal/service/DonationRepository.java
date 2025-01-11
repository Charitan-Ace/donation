package ace.charitan.donation.internal.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findAllByProjectId(String projectId);
    List<Donation> findAllByDonorId(String projectId);
    Page<Donation> findAllByDonorId(String donorId, Pageable pageable);
}
