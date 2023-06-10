package gr.athtech.babyfeedingmonitoringapp.dto;

import lombok.Data;

import java.util.Date;

@Data
public class FeedingSessionDto {
    private Long id;
    private Double milkConsumed;
    private Date startTime;
    private Date endTime;
    private Long userId;
}
