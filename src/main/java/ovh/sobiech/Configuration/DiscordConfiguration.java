package ovh.sobiech.Configuration;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.core.retriever.EntityRetriever;
import discord4j.core.retriever.StoreEntityRetriever;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ovh.sobiech.Listener.EventListener;

import java.util.function.Consumer;

@Slf4j
@Configuration
public class DiscordConfiguration {
    @Value("${DISCORD_TOKEN_VALUE}")
    private String token;

    @Bean
    public GatewayDiscordClient gatewayDiscordClient() {
        if (token == null || token.isBlank()){
            log.warn("Discord token is not set");
            throw new IllegalStateException("Discord token is not set");
        }
        return DiscordClientBuilder.create(token)
                .build()
                .login()
                .blockOptional()
                .orElseThrow();
    }

    @Bean
    @Primary
    public EntityRetriever entityRetriever(GatewayDiscordClient client) {
        return new StoreEntityRetriever(client);
    }

    public static <T extends Event> void subscribe(GatewayDiscordClient client, EventListener<T> eventListener) {
        client.on(eventListener.getEventType())
                .doOnSubscribe(s -> log.info("Subscribed to event: {}", eventListener.getEventType().getSimpleName()))
                .flatMap(eventListener::execute)
                .log()
                .subscribe();
    }


}
