package net.yogstation.yogbot.listeners.channel;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.config.DiscordChannelsConfig;
import net.yogstation.yogbot.config.DiscordConfig;
import reactor.core.publisher.Mono;

public abstract class AbstractChannel {
	protected final DiscordChannelsConfig channelsConfig;
	
	public AbstractChannel(DiscordChannelsConfig channelsConfig) {
		this.channelsConfig = channelsConfig;
	}
	
	public abstract Snowflake getChannel();
	public abstract Mono<?> handle(MessageCreateEvent event);
}
