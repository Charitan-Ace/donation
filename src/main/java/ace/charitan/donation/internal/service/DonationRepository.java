package ace.charitan.donation.internal.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> getAllByProjectId(String projectId);

    List<Donation> findAllByProjectId(String projectId);
}
