package ovh.sobiech.Scheduler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ovh.sobiech.Service.PingService;
import ovh.sobiech.Service.RetentionService;

@Slf4j
@Component
@AllArgsConstructor
public class ScheduledTasks {
    private final RetentionService retentionService;
    private final PingService pingService;

    @Scheduled(cron = "0 0 0 1 * *")
    public void retentionServiceMonthly() {
        log.info("Retention task started");
        retentionService.resetChat();
        retentionService.giveRoles();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void pingDaily() {
        log.info("Ping task started");
        pingService.ping();
    }
}
