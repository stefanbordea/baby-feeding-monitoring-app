package gr.athtech.babyfeedingmonitoringapp.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import gr.athtech.babyfeedingmonitoringapp.serializers.LocalDateTimeDeserializer;
import gr.athtech.babyfeedingmonitoringapp.serializers.LocalDateTimeSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class FeedingSessionDto implements Serializable {
    private Long id;
    private Double milkConsumed;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime startTime;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime endTime;
    private Long userId;
}
