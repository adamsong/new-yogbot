package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.Yogbot;
import reactor.core.publisher.Mono;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BannuCommand extends TextCommand {

	private static final Pattern argsPattern = Pattern.compile(".\\w+\\s(<@!?\\d+>)\\s?(.*)");

	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		Matcher matcher = argsPattern.matcher(event.getMessage().getContent());
		if(!matcher.matches()) {
			assert Yogbot.config.discordConfig != null;
			return reply(event, "Usage is `%sbannu [@UserName] <reason>`", Yogbot.config.discordConfig.commandPrefix);
		}
		String who = matcher.group(1);
		String reason = matcher.group(2);
		if(reason.equals("")) {
			reason = "NO RAISIN";
		} else {
			reason = String.format("\"\"\"\"\"%s\"\"\"\"\"", reason.toUpperCase(Locale.ROOT));
		}
		return reply(event, "%s HAS BEEN BANED 4 %s", who, reason);
	}

	@Override
	public String getName() {
		return "bannu";
	}

	@Override
	protected String getDescription() {
		return "\"\"\"\"\"Ban\"\"\"\"\" a user";
	}

	@Override
	public boolean isHidden() {
		return true;
	}
}
