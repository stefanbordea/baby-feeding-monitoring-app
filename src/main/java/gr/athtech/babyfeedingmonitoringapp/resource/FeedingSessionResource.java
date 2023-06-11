package gr.athtech.babyfeedingmonitoringapp.resource;

import gr.athtech.babyfeedingmonitoringapp.dto.AverageFeedingDurationDto;
import gr.athtech.babyfeedingmonitoringapp.dto.FeedingSessionDto;
import gr.athtech.babyfeedingmonitoringapp.dto.TimePeriodDto;
import gr.athtech.babyfeedingmonitoringapp.model.FeedingSession;
import gr.athtech.babyfeedingmonitoringapp.service.FeedingSessionService;
import gr.athtech.babyfeedingmonitoringapp.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jfree.chart.JFreeChart;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Path("/feed")
public class FeedingSessionResource {

    @Inject
    private FeedingSessionService feedingSessionService;

    @Inject
    private UserService userService;

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

    @POST
    @Path("/new")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN"})
    public Response createNewFeedingSession(FeedingSessionDto feedingSessionDto) {
        FeedingSession feedingSession = new FeedingSession();
        feedingSession.setUsers(new ArrayList<>());
        feedingSession.getUsers().add(userService.findById(feedingSessionDto.getUserId()).get());
        feedingSession.setStartTime(feedingSessionDto.getStartTime());
        feedingSession.setEndTime(feedingSessionDto.getEndTime());
        feedingSession.setMilkConsumed(feedingSessionDto.getMilkConsumed());
        feedingSessionService.create(feedingSession);
        return Response.ok().build();
    }

    @PUT
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN"})
    public Response updateFeedingSession(FeedingSessionDto feedingSessionDto) {
        FeedingSession feedingSession = feedingSessionService.findById(feedingSessionDto.getId()).get();
        feedingSession.getUsers().add(userService.findById(feedingSessionDto.getUserId()).get());
        feedingSession.setStartTime(feedingSessionDto.getStartTime());
        feedingSession.setEndTime(feedingSessionDto.getEndTime());
        feedingSession.setMilkConsumed(feedingSessionDto.getMilkConsumed());
        feedingSessionService.update(feedingSession);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN"})
    public Response deleteFeedingSession(FeedingSessionDto feedingSessionDto) throws NoSuchElementException {
        try {
            feedingSessionService.delete(feedingSessionService.findById(feedingSessionDto.getId()).get());

        } catch (NoSuchElementException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
