package ace.charitan.donation.internal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDonationRequestDto {
    private Double amount;
    private String message;
    private String projectId;
    private String successUrl;
    private String cancelUrl;

    //For guest
    private String email;
    private String firstName;
    private String lastName;
    private String address;
}
