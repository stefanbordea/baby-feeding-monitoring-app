package gr.athtech.babyfeedingmonitoringapp.dto;

import gr.athtech.babyfeedingmonitoringapp.model.Role;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private Role role;
}
