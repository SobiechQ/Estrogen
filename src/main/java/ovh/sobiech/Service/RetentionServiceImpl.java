package ovh.sobiech.Service;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.CategorizableChannel;
import discord4j.core.object.entity.channel.Category;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.retriever.EntityRetriever;
import discord4j.core.spec.TextChannelCreateSpec;
import discord4j.core.spec.TextChannelEditSpec;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Calendar;

@Slf4j
@Service
@AllArgsConstructor
public class RetentionServiceImpl implements RetentionService {
    private final static String RETENTION_CHANNEL_NAME = "poważne-gadanko-czat";
    private final static String CATEGORY_NAME = "Poważne Gadanko";
    private final EntityRetriever retriever;

    @Override
    public void clear() {
        getRetentionChannel(retriever)
                .flatMap(Channel::delete)
                .then(createChannel(retriever))
                .subscribe();
    }

    private static Mono<Guild> getGuild(EntityRetriever retriever) {
        return retriever.getGuilds()
                .singleOrEmpty();
    }

    private static Mono<TextChannel> getRetentionChannel(Guild guild) {
        return guild.getChannels()
                .filter(c -> c.getName().contains(RETENTION_CHANNEL_NAME))
                .map(g -> (TextChannel) g)
                .singleOrEmpty();
    }

    private static Mono<TextChannel> getRetentionChannel(EntityRetriever retriever) {
        return getGuild(retriever)
                .flatMap(RetentionServiceImpl::getRetentionChannel);
    }

    private static Mono<TextChannel> createChannel(EntityRetriever retriever) {
        final var category = getCategory(retriever)
                .map(c -> TextChannelCreateSpec.builder()
                        .name(getNewName())
                        .parentId(c.getId())
                        .position(1000)
                        .build());

        return category.flatMap(tccs -> getGuild(retriever).flatMap(g -> g.createTextChannel(tccs)));
    }

    private static String getNewName() {
        final var cal = Calendar.getInstance();
        return String.format("%s-%s-%s", RETENTION_CHANNEL_NAME, cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
    }

    private static Mono<Category> getCategory(EntityRetriever retriever) {
        return getGuild(retriever)
                .flatMapMany(Guild::getChannels)
                .filter(gc -> gc instanceof CategorizableChannel)
                .cast(CategorizableChannel.class)
                .flatMap(CategorizableChannel::getCategory)
                .distinct()
                .filter(c -> c.getName().contains(CATEGORY_NAME))
                .next();
    }


}
