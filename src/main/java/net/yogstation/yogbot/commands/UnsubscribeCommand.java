package net.yogstation.yogbot.commands;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.Yogbot;
import reactor.core.publisher.Mono;

public class UnsubscribeCommand extends TextCommand{
	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		if(event.getMember().isEmpty()) return Mono.empty();
		return event.getMember().get().removeRole(Snowflake.of(Yogbot.config.discordConfig.subscriberRole)).and(
			reply(event, "You are no longer a subscriber")
		);
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
