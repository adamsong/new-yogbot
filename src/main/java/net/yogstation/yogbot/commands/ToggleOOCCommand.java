package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.Yogbot;
import net.yogstation.yogbot.util.Result;
import reactor.core.publisher.Mono;

public class ToggleOOCCommand extends TextCommand {
	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		Result<Object, String> result = Yogbot.byondConnector.request("?toggleooc");
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
