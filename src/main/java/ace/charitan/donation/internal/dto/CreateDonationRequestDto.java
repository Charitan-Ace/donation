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
}
