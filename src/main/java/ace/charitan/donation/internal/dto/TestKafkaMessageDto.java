package ace.charitan.donation.internal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestKafkaMessageDto {
    private String name;
    private String description;
}
