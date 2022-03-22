package net.yogstation.yogbot.listeners.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.ByondConnector;
import net.yogstation.yogbot.config.DiscordConfig;
import net.yogstation.yogbot.util.Result;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class UnlinkCommand extends TextCommand {
	private final ByondConnector byondConnector;
	
	public UnlinkCommand(DiscordConfig discordConfig, ByondConnector byondConnector) {
		super(discordConfig);
		this.byondConnector = byondConnector;
	}
	
	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		String[] args = event.getMessage().getContent().split(" ");
		if(args.length < 2)
			return reply(event, "Usage: `%s <ckey>`", args[0]);
		
		Result<Object, String> requestResult = byondConnector.request(String.format("?unlink=%s", URLEncoder.encode(args[1], StandardCharsets.UTF_8)));
		String message;
		if(requestResult.hasError()) message = requestResult.getError();
		else message = ((String) requestResult.getValue()).replaceAll("\0", "");
		return reply(event, message);
	}
	
	@Override
	protected String getDescription() {
		return "Unlinks a ckey from a discord ID";
	}
	
	@Override
	public String getName() {
		return "unlink";
	}
}
