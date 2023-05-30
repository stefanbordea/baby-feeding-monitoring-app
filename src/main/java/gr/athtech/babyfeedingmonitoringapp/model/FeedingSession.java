package gr.athtech.babyfeedingmonitoringapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity(name = "FEEDING_SESSIONS")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FeedingSession extends BaseEntity {

    @Column(nullable = false)
    @NotEmpty
    @NotNull
    private Double milkConsumed;

    @Column(nullable = false)
    @NotEmpty
    @NotNull
    private Date startTime;

    @Column(nullable = false)
    @NotEmpty
    @NotNull
    private Date endTime;
}
