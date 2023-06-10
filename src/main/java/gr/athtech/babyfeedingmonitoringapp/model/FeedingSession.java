package gr.athtech.babyfeedingmonitoringapp.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import gr.athtech.babyfeedingmonitoringapp.serializers.LocalDateTimeDeserializer;
import gr.athtech.babyfeedingmonitoringapp.serializers.LocalDateTimeSerializer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "FEEDING_SESSIONS")
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
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime startTime;

    @Column(nullable = false)
    @NotEmpty
    @NotNull
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
