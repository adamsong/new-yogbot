package net.yogstation.yogbot.listeners.commands;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.config.DiscordConfig;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RemoveJesterCommand extends TextCommand{
	public RemoveJesterCommand(DiscordConfig discordConfig) {
		super(discordConfig);
	}
	
	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		if(event.getMember().isEmpty()) return Mono.empty();
		return event.getMember().get().removeRole(Snowflake.of(discordConfig.jesterRole)).and(
			reply(event, "Success! But beware if you violate the sacred Jester Oath by daring to ping Jester once again you shall be smited with a thousand YOGGERS!")
		);
	}

	@Override
	protected String getDescription() {
		return "Removes the jester role if you have it";
	}

	@Override
	public String getName() {
		return "removejester";
	}
}
