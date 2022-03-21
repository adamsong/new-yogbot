package net.yogstation.yogbot.commands;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.ByondConnector;
import net.yogstation.yogbot.config.DiscordChannelsConfig;
import net.yogstation.yogbot.config.DiscordConfig;
import net.yogstation.yogbot.util.ChannelUtil;
import net.yogstation.yogbot.util.Result;
import reactor.core.publisher.Mono;

public class AdminWhoCommand extends TextCommand {

	private final ByondConnector byondConnector;
	private final DiscordChannelsConfig channelsConfig;
	
	public AdminWhoCommand(DiscordConfig discordConfig, ByondConnector byondConnector,
	                       DiscordChannelsConfig channelsConfig) {
		super(discordConfig);
		this.byondConnector = byondConnector;
		this.channelsConfig = channelsConfig;
	}
	
	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		return reply(event, getAdmins(event.getMessage().getChannelId(), byondConnector, channelsConfig));
	}

	static String getAdmins(Snowflake channelID, ByondConnector byondConnector, DiscordChannelsConfig channelsConfig) {
		var byondMessage = "?adminwho";
		if(ChannelUtil.isAdminChannel(channelID.asLong(), channelsConfig))
			byondMessage += "&adminchannel=1";
		Result<Object, String> result = byondConnector.request(byondMessage);
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
