package net.yogstation.yogbot.listeners.commands;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.config.DiscordConfig;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UnsubscribeCommand extends TextCommand {
	public UnsubscribeCommand(DiscordConfig discordConfig) {
		super(discordConfig);
	}
	
	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		if (event.getMember().isEmpty()) return Mono.empty();
		return event.getMember()
			.get()
			.removeRole(Snowflake.of(discordConfig.subscriberRole))
			.and(reply(event, "You are no longer a subscriber"));
	}
	
	@Override
	protected String getDescription() {
		return "unsubscribe to the roundstart announcements";
	}
	
	@Override
	public String getName() {
		return "subscribe";
	}
}
