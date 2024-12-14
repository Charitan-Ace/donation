package ace.charitan.donation.service;

import ace.charitan.donation.external.dto.ExternalDonationDto;
import ace.charitan.donation.internal.dto.InternalDonationDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

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

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column()
    private String address;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private Float amount;

    @Column(length = 500)
    private String message;

    @Column(nullable = false, length = 100)
    private String transactionStripeId;

    @Column(nullable = false)
    private Long projectId;

    @Column(nullable = false)
    private Long donorId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
