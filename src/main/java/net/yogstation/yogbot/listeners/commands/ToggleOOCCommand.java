package net.yogstation.yogbot.listeners.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.ByondConnector;
import net.yogstation.yogbot.config.DiscordConfig;
import net.yogstation.yogbot.util.Result;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ToggleOOCCommand extends TextCommand {
	private final ByondConnector byondConnector;
	
	public ToggleOOCCommand(DiscordConfig discordConfig, ByondConnector byondConnector) {
		super(discordConfig);
		this.byondConnector = byondConnector;
	}
	
	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		Result<Object, String> result = byondConnector.request("?toggleooc");
		if(result.hasError()) return reply(event, result.getError());
		return reply(event, "OOC has been %s", (float) result.getValue() == 1.0 ? "enabled" : "disabled");
	}
	
	@Override
	protected String getDescription() {
		return "Toggles server OOC.";
	}
	
	@Override
	public String getName() {
		return "toggleooc";
	}
}
