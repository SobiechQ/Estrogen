package ovh.sobiech.Service;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.retriever.EntityRetriever;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class RetentionServiceImpl implements RetentionService {
    private final static String RETENTION_CHANNEL_NAME = "poważne-gadanko-czat";
    private final EntityRetriever retriever;

    @Override
    public void clearAndNotify() {

        final var count = getRetentionChannel(retriever)
                .map(g -> g.getMessagesBefore(Snowflake.of(Instant.now())))
                .stream()
                .flatMap(Flux::toStream)
                .filter(m -> m.getAuthor().map(u->!u.isBot()).orElse(false))
                .count();

        getRetentionChannel(retriever)
                .map(g -> g.getMessagesBefore(Snowflake.of(Instant.now())))
                .stream()
                .flatMap(Flux::toStream)
                .map(m->m.delete("reason"))
                .forEach(Mono::subscribe);




        getRetentionChannel(retriever)
                .map(g -> {
                    log.info("Past messages information count published");
                    return g.createMessage(String.format("Na kanale jest %s wiadomości", count));
                })
                .ifPresent(Mono::subscribe);




    }

    private static Optional<Guild> getGuild(EntityRetriever retriever) {
        return retriever.getGuilds().toStream()
                .findFirst();
    }

    private static Optional<TextChannel> getRetentionChannel(Guild guild) {
        return guild.getChannels()
                .toStream()
                .filter(c -> c.getName().contains(RETENTION_CHANNEL_NAME))
                .map(g -> (TextChannel) g)
                .findAny();
    }

    private static Optional<TextChannel> getRetentionChannel(EntityRetriever entityRetriever) {
        return getGuild(entityRetriever)
                .flatMap(RetentionServiceImpl::getRetentionChannel);
    }
}
