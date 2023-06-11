package gr.athtech.babyfeedingmonitoringapp.service.implementation;

import gr.athtech.babyfeedingmonitoringapp.dto.AverageFeedingDurationDto;
import gr.athtech.babyfeedingmonitoringapp.dto.FeedingSessionDto;
import gr.athtech.babyfeedingmonitoringapp.model.FeedingSession;
import gr.athtech.babyfeedingmonitoringapp.repository.FeedingSessionRepository;
import gr.athtech.babyfeedingmonitoringapp.service.FeedingSessionService;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Named
public class FeedingSessionServiceImpl implements FeedingSessionService {

    @Inject
    private FeedingSessionRepository feedingSessionRepository;


    public String generateBase64Image(JFreeChart chart) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ChartUtils.writeChartAsPNG(outputStream, chart, 800, 600);
            byte[] chartBytes = outputStream.toByteArray();
            return Base64.encodeBase64String(chartBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public JFreeChart generateMilkConsumedChart(List<FeedingSessionDto> feedingSessions) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (FeedingSessionDto session : feedingSessions) {
            double milkConsumed = session.getMilkConsumed();
            String startTime = session.getStartTime().toString();
            dataset.addValue(milkConsumed, "Milk Consumed", startTime);
        }
        return ChartFactory.createLineChart("Milk Consumed Chart", "Time", "Milk Consumed", dataset);
    }

    @Override
    public JFreeChart generateFeedingDurationChart(List<FeedingSessionDto> feedingSessions) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (FeedingSessionDto session : feedingSessions) {
            LocalDateTime startTime = session.getStartTime();
            LocalDateTime endTime = session.getEndTime();

            Duration feedingDuration = Duration.between(startTime, endTime);
            String startTimeString = startTime.toString();
            dataset.addValue(feedingDuration.toMinutes(), "Feeding Duration (minutes)", startTimeString);
        }

        return ChartFactory.createLineChart("Feeding Duration Chart", "Time", "Feeding Duration (minutes)", dataset);

    }

    @Override
    public List<FeedingSessionDto> findByTimePeriod(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null) {
            startTime = LocalDateTime.of(1970, 1, 1, 0, 0, 1);
        }

        if (endTime == null) {
            endTime = LocalDateTime.now();
        }
        return feedingSessionRepository.findByTimePeriod(userId, startTime, endTime);
    }

    @Override
    public FeedingSessionDto calculateAverageMilkConsumed(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null) {
            startTime = LocalDateTime.of(1970, 1, 1, 0, 0, 1);
        }

        if (endTime == null) {
            endTime = LocalDateTime.now();
        }
        return feedingSessionRepository.calculateAverageMilkConsumed(userId, startTime, endTime);
    }

    @Override
    public AverageFeedingDurationDto calculateAverageFeedingDuration(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null) {
            startTime = LocalDateTime.of(1970, 1, 1, 0, 0, 1);
        }

        if (endTime == null) {
            endTime = LocalDateTime.now();
        }
        return feedingSessionRepository.calculateAverageFeedingDuration(userId, startTime, endTime);
    }

    @Override
    public Optional<FeedingSession> create(FeedingSession feedingSession) {
        return feedingSessionRepository.create(feedingSession);
    }

    @Override
    public Optional<FeedingSession> findById(Long id) {
        return feedingSessionRepository.findById(id);
    }

    @Override
    public List<FeedingSession> findAll() {
        return feedingSessionRepository.findAll();
    }

    @Override
    public Optional<FeedingSession> update(FeedingSession feedingSession) {
        return feedingSessionRepository.update(feedingSession);
    }

    @Override
    public void delete(FeedingSession feedingSession) {
        feedingSessionRepository.delete(feedingSession);
    }
}
