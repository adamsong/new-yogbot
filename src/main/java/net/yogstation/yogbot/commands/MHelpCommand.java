package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import net.yogstation.yogbot.Yogbot;
import net.yogstation.yogbot.util.Result;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MHelpCommand extends PermissionsCommand {
	private static final Pattern argsPattern = Pattern.compile(".\\w+\\s(\\w+)\\s(.+)");

	@Override
	protected String getRequiredPermissions() {
		return "mhelp";
	}

	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		Matcher matcher = argsPattern.matcher(event.getMessage().getContent());
		if(!matcher.matches())
			return reply(event, "Usage is `%smhelp <ckey> <message>", Yogbot.config.discordConfig.commandPrefix);
		StringBuilder builder = new StringBuilder("?mhelp=1");
		builder.append("&msg=").append(URLEncoder.encode(matcher.group(2), StandardCharsets.UTF_8));
		builder.append("&admin=");
		User author = event.getMessage().getAuthor().orElse(null);
		if(author != null) {
			builder.append(URLEncoder.encode(String.format("@%s#%s", author.getUsername(), author.getDiscriminator()), StandardCharsets.UTF_8));
			builder.append("&admin_id=").append(author.getId().asLong());
		} else {
			builder.append("Mentor");
			builder.append("&admin_id=").append("0");
		}
		builder.append("&whom=").append(URLEncoder.encode(matcher.group(1), StandardCharsets.UTF_8));
		Result<Object, String> result = Yogbot.byondConnector.request(builder.toString());
		if(result.hasError()) return reply(event, result.getError());
		if(((float) result.getValue()) == 0) {
			return reply(event, "Error: Mentor-PM: Client %s not found.", matcher.group(1));
		}
		return Mono.empty();
	}

	@Override
	protected String getDescription() {
		return "Replay to a mentorhelp.";
	}

	@Override
	public String getName() {
		return "mhelp";
	}
}
