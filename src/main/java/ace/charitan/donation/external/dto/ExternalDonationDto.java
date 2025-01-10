package ace.charitan.donation.external.dto;

import java.time.LocalDate;

public interface ExternalDonationDto {
    Long getId();
    Double getAmount();
    String getMessage();
    String getTransactionStripeId();
    String getProjectId();
    String getDonorId();
    LocalDate getCreatedAt();
}
