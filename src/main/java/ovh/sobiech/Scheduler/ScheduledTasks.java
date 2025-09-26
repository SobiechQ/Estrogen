package ovh.sobiech.Scheduler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ovh.sobiech.Service.RetentionService;

@Slf4j
@Component
@AllArgsConstructor
public class ScheduledTasks {
    private final RetentionService retentionService;

    @Scheduled(cron = "* * * 1 * *")
    public void now() {
        log.info("Scheduled task started");
        retentionService.clear();
    }
}
