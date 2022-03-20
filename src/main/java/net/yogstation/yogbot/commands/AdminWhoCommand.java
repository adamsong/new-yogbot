package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.Yogbot;
import net.yogstation.yogbot.util.ChannelUtil;
import net.yogstation.yogbot.util.Result;
import reactor.core.publisher.Mono;

import java.nio.channels.Channel;

public class AdminWhoCommand extends TextCommand {

	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		String admins = getAdmins(event);
		if(admins == null) return reply(event, "There was an error getting the admins");
		return reply(event, admins);
	}

	static String getAdmins(MessageCreateEvent event) {
		var byondMessage = "?adminwho";
		if(ChannelUtil.isAdminChannel(event.getMessage().getChannelId().asLong()))
			byondMessage += "&adminchannel=1";
		Result<Object, String> result = Yogbot.byondConnector.request(byondMessage);
		String admins = (String) (result.hasError() ? result.getError() : result.getValue());
		admins = admins.replaceAll("\0", "");
		return admins;
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
