package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

public class BugCommand extends TextCommand {


	@Override
	public String getName() {
		return "bug";
	}

	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		return send(event, "https://i.imgur.com/iO03Tqm.gifv");
	}
}
