package ovh.sobiech.Service;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.retriever.EntityRetriever;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Calendar;

@Slf4j
@Service
@AllArgsConstructor
public class PingServiceImpl implements PingService {
    private final static String PING_CHANNEL_NAME = "admin-log";
    private final EntityRetriever retriever;

    @Override
    public void ping() {
        this.getPingChannel()
                .flatMap(tc -> tc.createMessage(getMessage()))
                .subscribe();
    }

    private Mono<TextChannel> getPingChannel() {
        return this.getGuild()
                .flatMapMany(Guild::getChannels)
                .filter(c -> c.getName().contains(PING_CHANNEL_NAME))
                .map(g -> (TextChannel) g)
                .singleOrEmpty();
    }

    private Mono<Guild> getGuild() {
        return retriever.getGuilds()
                .singleOrEmpty();
    }

    private static String getMessage() {
        final var cal = Calendar.getInstance();
        return String.format("Estrogen running for: Day: [%s], Month: [%s]", cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1);
    }
}
