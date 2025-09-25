package ovh.sobiech.Listener;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ovh.sobiech.Service.RetentionService;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class MessageCreateListener extends MessageListener implements EventListener<MessageCreateEvent> {


    public MessageCreateListener(RetentionService retentionService) {
        super(retentionService);
    }

    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        log.info("Handling event from user: {}", event.getMessage().getAuthor().map(User::getUsername).orElse("Unknown"));

        return Mono.defer(() -> {
            log.info("Starting command processing");
            return this.processCommand(event.getMessage())
                    .doOnSuccess(v -> log.info("Command processing completed successfully"))
                    .doOnError(e -> log.error("Command processing failed", e))
                    .doOnTerminate(() -> log.info("Command processing terminated"));
        });
    }
}