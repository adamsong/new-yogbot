package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.Yogbot;
import net.yogstation.yogbot.util.Result;
import reactor.core.publisher.Mono;

public class PingCommand extends TextCommand {
	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		Result<Object, String> pingResponse = Yogbot.byondConnector.request("?ping");
		if(pingResponse.hasError()) return reply(event, pingResponse.getError());
		int playerCount = (int) (float) (Float) pingResponse.getValue();
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
