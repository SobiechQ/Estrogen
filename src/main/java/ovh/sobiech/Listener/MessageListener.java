package ovh.sobiech.Listener;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import ovh.sobiech.Configuration.DiscordConfiguration;
import ovh.sobiech.Service.RetentionService;
import reactor.core.publisher.Mono;

public abstract class MessageListener implements EventListener<MessageCreateEvent> {
    private final RetentionService retentionService;

    public MessageListener(RetentionService retentionService, GatewayDiscordClient client) {
        this.retentionService = retentionService;
        DiscordConfiguration.subscribe(client, this);
    }

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
                .doOnNext(m -> retentionService.clear())
                .log()
                .then();
    }

    private Mono<Void> processTest(Message message) {
        return Mono.just(message)
                .filter(m -> m.getAuthor().map(u -> !u.isBot()).orElse(false))
                .flatMap(Message::getChannel)
                .flatMapMany(c -> c.createMessage("meowr :3c").repeat(10))
                .log()
                .then();
    }
}
