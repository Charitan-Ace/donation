package ace.charitan.donation.internal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDonationRequestDto {
    private String firstName;
    private String lastName;
    private String address;
    private String email;
    private Float amount;
    private String message;
    private String transactionStripeId;
}