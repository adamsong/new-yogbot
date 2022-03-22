package net.yogstation.yogbot.listeners.commands;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.DatabaseManager;
import net.yogstation.yogbot.config.DiscordConfig;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class WhoIsCommand extends TextCommand {
	private final DatabaseManager database;
	private final GatewayDiscordClient client;
	
	public WhoIsCommand(DiscordConfig discordConfig, DatabaseManager database, GatewayDiscordClient client) {
		super(discordConfig);
		this.database = database;
		this.client = client;
	}
	
	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		CommandTarget target = getTarget(event);
		if(target == null)
			return reply(event, "Usage: `%swhois <@Username|ckey>", discordConfig.commandPrefix);
		String error = target.populate(database);
		if (error != null) return reply(event, error);
		return client.getUserById(target.getSnowflake())
			.flatMap(
				user -> reply(event, "Ckey %s is linked to discord account %s#%s", target.getCkey(), user.getUsername(),
				              user.getDiscriminator()));
	}
	
	@Override
	protected String getDescription() {
		return "Checks a Discord users ckey if they have their accounts linked.";
	}
	
	@Override
	public String getName() {
		return "whois";
	}
}
