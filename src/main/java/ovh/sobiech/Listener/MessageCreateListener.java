package ovh.sobiech.Listener;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.retriever.EntityRetriever;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ovh.sobiech.Service.RetentionService;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class MessageCreateListener extends MessageListener  {

    public MessageCreateListener(RetentionService retentionService, GatewayDiscordClient client, EntityRetriever retriever) {
        super(retentionService, client, retriever);
    }

    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return Mono.defer(() -> this.processCommand(event.getMessage()));
    }
}