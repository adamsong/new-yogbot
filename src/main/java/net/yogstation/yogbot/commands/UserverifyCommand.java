package net.yogstation.yogbot.commands;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.PartialMember;
import discord4j.core.object.entity.User;
import net.yogstation.yogbot.Yogbot;
import net.yogstation.yogbot.config.DiscordConfig;
import net.yogstation.yogbot.permissions.PermissionsManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Component
public class UserverifyCommand extends PermissionsCommand {
	public UserverifyCommand(DiscordConfig discordConfig, PermissionsManager permissions) {
		super(discordConfig, permissions);
	}
	
	@Override
	protected String getRequiredPermissions() {
		return "userverify";
	}
	
	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		PartialMember target;
		List<PartialMember> mentions = event.getMessage().getMemberMentions();
		if(mentions.size() < 1)
			return reply(event, "Usage: `%s <@Username>`", event.getMessage().getContent().split(" ")[0]);
		target = mentions.get(0);
		
		Optional<User> author = event.getMessage().getAuthor();
		if(author.isEmpty()) return Mono.empty();
		String user = author.get().getUsername();
		Snowflake verifyRole = Snowflake.of(discordConfig.manualVerifyRole);
		if(target.getRoleIds().contains(verifyRole))
			return target.removeRole(verifyRole, String.format("Manually unverified by %s", user)).and(reply(event, "User unverified"));
		return target.addRole(verifyRole, String.format("Manually verified by %s", user)).and(reply(event, "User verified"));
	}
	
	@Override
	protected String getDescription() {
		return "Verifies someone on the discord. Use again to un-verify someone.";
	}
	
	@Override
	public String getName() {
		return "userverify";
	}
}
