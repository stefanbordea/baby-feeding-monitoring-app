package gr.athtech.babyfeedingmonitoringapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "USERS")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    @NotEmpty
    @NotNull
    private String username;

    @Column(nullable = false)
    @NotEmpty
    @NotNull
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
}
