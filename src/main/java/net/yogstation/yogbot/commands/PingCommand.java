package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.Yogbot;
import reactor.core.publisher.Mono;

public class PingCommand extends TextCommand {
	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		int playerCount = (int) (float) (Float) Yogbot.byondConnector.request("?ping");
		return reply(event, String.format("There are **%d** players online, join them now with %s", playerCount, Yogbot.config.byondConfig.serverJoinAddress));
	}

	@Override
	protected String getDescription() {
		return "Pings the server.";
	}

	@Override
	public String getName() {
		return "ping";
	}
}
