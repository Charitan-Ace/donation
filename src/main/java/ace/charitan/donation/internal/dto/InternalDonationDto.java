package ace.charitan.donation.internal.dto;


public interface InternalDonationDto {
    Long getId();
    String getFirstName();
    String getLastName();
    String getAddress();
    String getEmail();
    Float getAmount();
    String getMessage();
    String getTransactionStripeId();
    Long getProjectId();
    Long getDonorId();
}
