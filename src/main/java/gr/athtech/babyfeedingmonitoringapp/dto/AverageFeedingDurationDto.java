package gr.athtech.babyfeedingmonitoringapp.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import gr.athtech.babyfeedingmonitoringapp.serializers.DurationDeserializer;
import gr.athtech.babyfeedingmonitoringapp.serializers.DurationSerializer;
import gr.athtech.babyfeedingmonitoringapp.serializers.LocalDateTimeDeserializer;
import gr.athtech.babyfeedingmonitoringapp.serializers.LocalDateTimeSerializer;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
public class AverageFeedingDurationDto {
    @JsonSerialize(using = DurationSerializer.class)
    @JsonDeserialize(using = DurationDeserializer.class)
    private Duration averageFeedingDuration;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime startTime;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime endTime;
    private Long userId;
}
