package ovh.sobiech.Listener;

import discord4j.core.object.entity.Message;
import lombok.AllArgsConstructor;
import ovh.sobiech.Service.RetentionService;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public abstract class MessageListener {
    private final RetentionService retentionService;

    public Mono<Void> processCommand(Message eventMessage) {
        return switch (eventMessage.getContent().toLowerCase()){
            case "!test" -> processTest(eventMessage);
            case "!clear" -> processClear(eventMessage);
            default -> Mono.empty();
        };
    }

    public Mono<Void> processClear(Message message) {
        return Mono.just(message)
                .filter(m -> m.getAuthor().map(u -> !u.isBot()).orElse(false))
                .doOnNext(m -> retentionService.clearAndNotify())
                .then();
    }

    private Mono<Void> processTest(Message message) {
        return Mono.just(message)
                .filter(m -> m.getAuthor().map(u -> !u.isBot()).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(c -> c.createMessage("meowr :3c"))
                .then();
    }
}
