package ace.charitan.donation.internal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDonationResponseDto {
    private Long id;

    private Double amount;

    private String message;

    private String transactionStripeId;

    private String projectId;

    private String donorId;

    private LocalDate createdAt;

    private String redirectUrl;
}
