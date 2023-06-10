package gr.athtech.babyfeedingmonitoringapp.dto;

import gr.athtech.babyfeedingmonitoringapp.model.Role;
import lombok.Data;

import java.util.List;

@Data
public class UserWithFeedingSessionsDto {
    private Long id;
    private String username;
    private Role role;
    private List<FeedingSessionDto> feedingSessions;
}
