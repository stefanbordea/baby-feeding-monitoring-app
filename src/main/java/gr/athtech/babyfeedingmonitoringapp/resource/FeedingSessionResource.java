package gr.athtech.babyfeedingmonitoringapp.resource;

import gr.athtech.babyfeedingmonitoringapp.dto.AverageFeedingDurationDto;
import gr.athtech.babyfeedingmonitoringapp.dto.FeedingSessionDto;
import gr.athtech.babyfeedingmonitoringapp.dto.TimePeriodDto;
import gr.athtech.babyfeedingmonitoringapp.service.FeedingSessionService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jfree.chart.JFreeChart;

import java.time.LocalDateTime;
import java.util.List;

@Path("/feed")
public class FeedingSessionResource {

    @Inject
    private FeedingSessionService feedingSessionService;

    @POST
    @Path("/sessions")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"PHYSICIAN", "ADMIN"})
    public Response getFeedingSessionsByTimePeriod(TimePeriodDto timePeriodDto) {
        Long userId = timePeriodDto.getUserId();
        LocalDateTime startTime = timePeriodDto.getStartTime();
        LocalDateTime endTime = timePeriodDto.getEndTime();
        List<FeedingSessionDto> feedingSessions = feedingSessionService.findByTimePeriod(userId, startTime, endTime);
        return Response.ok(feedingSessions).build();
    }

    @POST
    @Path("/averageMilk")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"PHYSICIAN", "ADMIN"})
    public Response getAverageMilkPerFeedingSession(TimePeriodDto timePeriodDto) {
        Long userId = timePeriodDto.getUserId();
        LocalDateTime startTime = timePeriodDto.getStartTime();
        LocalDateTime endTime = timePeriodDto.getEndTime();
        FeedingSessionDto averageMilkConsumed = feedingSessionService.calculateAverageMilkConsumed(userId, startTime, endTime);
        return Response.ok(averageMilkConsumed).build();
    }

    @POST
    @Path("/averageDuration")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"PHYSICIAN", "ADMIN"})
    public Response getAverageFeedingSessionDuration(TimePeriodDto timePeriodDto) {
        Long userId = timePeriodDto.getUserId();
        LocalDateTime startTime = timePeriodDto.getStartTime();
        LocalDateTime endTime = timePeriodDto.getEndTime();
        AverageFeedingDurationDto averageFeedingDuration = feedingSessionService.calculateAverageFeedingDuration(userId, startTime, endTime);
        return Response.ok(averageFeedingDuration).build();
    }

    @POST
    @Path("/milkChart")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"PHYSICIAN", "ADMIN"})
    public Response getMilkChart(TimePeriodDto timePeriodDto) {
        List<FeedingSessionDto> feedingSessionDtos = feedingSessionService.findByTimePeriod(timePeriodDto.getUserId(), timePeriodDto.getStartTime(), timePeriodDto.getEndTime());
        JFreeChart chart = feedingSessionService.generateMilkConsumedChart(feedingSessionDtos);
        String base64Image = feedingSessionService.generateBase64Image(chart);
        return Response.ok(base64Image).build();
    }

    @POST
    @Path("/durationChart")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"PHYSICIAN", "ADMIN"})
    public Response getDurationChart(TimePeriodDto timePeriodDto) {
        List<FeedingSessionDto> feedingSessionDtos = feedingSessionService.findByTimePeriod(timePeriodDto.getUserId(), timePeriodDto.getStartTime(), timePeriodDto.getEndTime());
        JFreeChart chart = feedingSessionService.generateFeedingDurationChart(feedingSessionDtos);
        String base64Image = feedingSessionService.generateBase64Image(chart);
        return Response.ok(base64Image).build();
    }
}
