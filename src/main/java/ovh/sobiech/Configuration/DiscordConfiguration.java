package ovh.sobiech.Configuration;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.core.retriever.EntityRetriever;
import discord4j.core.retriever.StoreEntityRetriever;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ovh.sobiech.Listener.EventListener;

import java.util.List;

@Slf4j
@Configuration
public class DiscordConfiguration {
    @Value("${DISCORD_TOKEN_VALUE}")
    private String token;

    @Bean
    public <T extends Event> GatewayDiscordClient gatewayDiscordClient(List<EventListener<T>> listeners) {
        log.info("Token {}", token == null || token.isBlank() ? "is empty" : "properly set");
        final var client = DiscordClientBuilder.create(token)
                .build()
                .login()
                .blockOptional()
                .orElseThrow();

        listeners.forEach(l ->
                client.on(l.getEventType())
                        .doOnSubscribe(s -> log.info("Subscribed to event: {}", l.getEventType().getSimpleName()))
                        .flatMap(l::execute)
                        .onErrorResume(l::handleError)
                        .subscribe());
        return client;
    }



    @Bean
    @Primary
    public EntityRetriever entityRetriever(GatewayDiscordClient client) {
        return new StoreEntityRetriever(client);
    }

}
