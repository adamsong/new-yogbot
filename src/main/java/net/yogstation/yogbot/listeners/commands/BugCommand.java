package net.yogstation.yogbot.listeners.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.config.DiscordConfig;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class BugCommand extends TextCommand {
	
	public BugCommand(DiscordConfig discordConfig) {
		super(discordConfig);
	}
	
	@Override
	public String getName() {
		return "bug";
	}

	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		return send(event, "https://i.imgur.com/iO03Tqm.gifv");
	}

	@Override
	protected String getDescription() {
		return "Get off my lawn";
	}

	@Override
	public boolean isHidden() {
		return true;
	}
}
