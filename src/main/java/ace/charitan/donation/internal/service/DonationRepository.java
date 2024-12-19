package ace.charitan.donation.internal.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface DonationRepository extends JpaRepository<Donation, Long> {
}
