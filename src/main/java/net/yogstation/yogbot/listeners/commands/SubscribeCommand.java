package net.yogstation.yogbot.listeners.commands;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.config.DiscordConfig;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class SubscribeCommand extends TextCommand {
	public SubscribeCommand(DiscordConfig discordConfig) {
		super(discordConfig);
	}
	
	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		if(event.getMember().isEmpty()) return Mono.empty();
		return event.getMember().get().addRole(Snowflake.of(discordConfig.subscriberRole)).and(
			reply(event, "You are now a subscriber")
		);
	}

	@Override
	protected String getDescription() {
		return "subscribe to the roundstart announcements";
	}

	@Override
	public String getName() {
		return "subscribe";
	}
}
