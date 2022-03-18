package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.Yogbot;
import net.yogstation.yogbot.util.ChannelUtil;
import reactor.core.publisher.Mono;

import java.nio.channels.Channel;

public class AdminWhoCommand extends TextCommand {

	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		var byondMessage = "?adminwho";
		if(ChannelUtil.isAdminChannel(event.getMessage().getChannelId().asLong()))
			byondMessage += "&adminchannel=1";
		String admins = (String) Yogbot.byondConnector.request(byondMessage);
		if(admins == null) return reply(event, "There was an error getting the admins");
		return reply(event, admins);
	}

	@Override
	public String getName() {
		return "adminwho";
	}

	@Override
	protected String getDescription() {
		return "Get current admins online.";
	}
}
