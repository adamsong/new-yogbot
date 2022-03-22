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
public class ASayChannel extends AbstractChannel {
	private final ByondConnector byondConnector;
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	public ASayChannel(DiscordChannelsConfig channelsConfig, ByondConnector byondConnector) {
		super(channelsConfig);
		this.byondConnector = byondConnector;
	}
	
	@Override
	public Snowflake getChannel() {
		return Snowflake.of(channelsConfig.channelAsay);
	}
	
	@Override
	public Mono<?> handle(MessageCreateEvent event) {
		if(event.getMessage().getAuthor().isEmpty()) return Mono.empty();
		StringBuilder messageBuilder = new StringBuilder(StringEscapeUtils.escapeHtml4(event.getMessage().getContent()));
		event.getMessage().getAttachments().forEach(attachment -> {
			LOGGER.info("Attachment {} found", attachment.getFilename());
			if(attachment.getFilename().endsWith(".jpg") || attachment.getFilename().endsWith(".png")) {
				messageBuilder.append("<br><img src=\"").append(attachment.getUrl()).append("\" alt=\"Image\">");
			}
		});
		String adminName;
		if(event.getMember().isPresent()) {
			adminName = event.getMember().get().getDisplayName();
		} else {
			adminName = event.getMessage().getAuthor().get().getUsername();
		}
		
		LOGGER.info(messageBuilder.toString());
		byondConnector.request(String.format("?asay=%s&admin=%s", URLEncoder.encode(messageBuilder.toString(), StandardCharsets.UTF_8), URLEncoder.encode(adminName, StandardCharsets.UTF_8)));
		return Mono.empty();
	}
}
