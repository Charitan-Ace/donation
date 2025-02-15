package ace.charitan.donation.internal.service;

import ace.charitan.donation.external.dto.ExternalDonationDto;
import ace.charitan.donation.internal.dto.InternalDonationDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "donation")
class Donation implements InternalDonationDto, ExternalDonationDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double amount;

    @Column(length = 250)
    private String message;

    @Column(length = 100)
    private String transactionStripeId;

    @Column(nullable = false)
    private String projectId;

    @Column(nullable = false)
    private String donorId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDate createdAt;
}
