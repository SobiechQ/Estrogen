package ovh.sobiech.Listener;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.PartialMember;
import discord4j.core.retriever.EntityRetriever;
import ovh.sobiech.Configuration.DiscordConfiguration;
import ovh.sobiech.Service.RetentionService;
import reactor.core.publisher.Mono;

public abstract class MessageListener implements EventListener<MessageCreateEvent> {
    private final static String MODERATOR_ROLE_NAME = "Moderator";
    private final RetentionService retentionService;
    private final EntityRetriever retriever;

    public MessageListener(RetentionService retentionService, GatewayDiscordClient client, EntityRetriever retriever) {
        this.retentionService = retentionService;
        this.retriever = retriever;
        DiscordConfiguration.subscribe(client, this);
    }

    public Mono<Void> processCommand(Message message) {
        if (!isMessageSentByModerator(message)){
            return Mono.empty();
        }

        return switch (message.getContent().toLowerCase()){
            case "!test" -> processTest(message);
            case "!clear" -> processClear(message);
            case "!roles" -> processRoles(message);
            default -> Mono.empty();
        };
    }

    public Mono<Void> processClear(Message message) {
        return Mono.just(message)
                .filter(m -> m.getAuthor().map(u -> !u.isBot()).orElse(false))
                .doOnNext(m -> retentionService.resetChat())
                .log()
                .then();
    }

    public Mono<Void> processRoles(Message message) {
        return Mono.just(message)
                .filter(m -> m.getAuthor().map(u -> !u.isBot()).orElse(false))
                .doOnNext(m -> retentionService.giveRoles())
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

    private boolean isMessageSentByModerator(Message message) {
        final var moderatorRole = retriever.getGuilds()
                .singleOrEmpty()
                .flatMapMany(Guild::getRoles)
                .filter(r -> r.getName().contains(MODERATOR_ROLE_NAME))
                .next();

        return Boolean.TRUE.equals(moderatorRole.flatMap(modRole ->
                message.getAuthorAsMember()
                        .flatMapMany(PartialMember::getRoles)
                        .any(r -> r.equals(modRole))
        ).block());
    }
}
