package net.yogstation.yogbot.listeners.channel;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.ByondConnector;
import net.yogstation.yogbot.config.DiscordChannelsConfig;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class MSayChannel extends AbstractChannel {
	private final ByondConnector byondConnector;
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	public MSayChannel(DiscordChannelsConfig channelsConfig, ByondConnector byondConnector) {
		super(channelsConfig);
		this.byondConnector = byondConnector;
	}
	
	@Override
	public Snowflake getChannel() {
		return Snowflake.of(channelsConfig.channelMsay);
	}
	
	@Override
	public Mono<?> handle(MessageCreateEvent event) {
		if(event.getMessage().getAuthor().isEmpty()) return Mono.empty();
		String message = StringEscapeUtils.escapeHtml4(event.getMessage().getContent());
		String mentorName;
		if(event.getMember().isPresent()) {
			mentorName = event.getMember().get().getDisplayName();
		} else {
			mentorName = event.getMessage().getAuthor().get().getUsername();
		}
		
		byondConnector.request(String.format("?msay=%s&admin=%s", URLEncoder.encode(message, StandardCharsets.UTF_8), URLEncoder.encode(mentorName, StandardCharsets.UTF_8)));
		return Mono.empty();
	}
}
