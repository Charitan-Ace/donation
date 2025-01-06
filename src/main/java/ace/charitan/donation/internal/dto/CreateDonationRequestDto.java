package ace.charitan.donation.internal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDonationRequestDto {
    private Float amount;
    private String message;
    private Long projectId;
    private Long donorId;
}
