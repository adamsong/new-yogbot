package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.ByondConnector;
import net.yogstation.yogbot.config.ByondConfig;
import net.yogstation.yogbot.config.DiscordConfig;
import net.yogstation.yogbot.util.Result;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PingCommand extends TextCommand {
	private final ByondConnector byondConnector;
	private final ByondConfig byondConfig;
	
	public PingCommand(DiscordConfig discordConfig, ByondConnector byondConnector, ByondConfig byondConfig) {
		super(discordConfig);
		this.byondConnector = byondConnector;
		this.byondConfig = byondConfig;
	}
	
	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		Result<Object, String> pingResponse = byondConnector.request("?ping");
		if(pingResponse.hasError()) return reply(event, pingResponse.getError());
		int playerCount = (int) (float) (Float) pingResponse.getValue();
		return reply(event, "There are **%d** players online, join them now with %s", playerCount, byondConfig.serverJoinAddress);
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
