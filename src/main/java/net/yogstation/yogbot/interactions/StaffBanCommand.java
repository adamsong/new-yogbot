package net.yogstation.yogbot.interactions;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.UserInteractionEvent;
import net.yogstation.yogbot.config.DiscordConfig;
import net.yogstation.yogbot.permissions.PermissionsManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
public class StaffBanCommand implements IUserCommand {
	private final PermissionsManager permissions;
	private final DiscordConfig discordConfig;
	
	public StaffBanCommand(PermissionsManager permissions, DiscordConfig discordConfig) {
		this.permissions = permissions;
		this.discordConfig = discordConfig;
	}
	
	@Override
	public String getName() {
		return "Staff Public Ban";
	}
	
	@Override
	public String getURI() {
		return "staffban.json";
	}
	
	@Override
	public Mono<?> handle(UserInteractionEvent event) {
		if(event.getInteraction().getGuildId().isEmpty())
			return event.reply().withEphemeral(true).withContent("Must be used in a guild");
		if(!permissions.hasPermission(event.getInteraction().getMember().orElse(null), "staffban"))
			return event.reply().withEphemeral(true).withContent("You do not have permission to run that command");
		
		return event.getTargetUser().flatMap(user -> user.asMember(event.getInteraction().getGuildId().get()).flatMap(member -> {
			Set<Snowflake> roles = member.getRoleIds();
			if(roles.contains(Snowflake.of(discordConfig.secondWarningRole))) {
				return member.addRole(Snowflake.of(discordConfig.staffPublicBanRole)).and(
					event.reply().withContent("User was banned from staff public")
				);
			} else if(roles.contains(Snowflake.of(discordConfig.firstWarningRole))) {
				return member.addRole(Snowflake.of(discordConfig.secondWarningRole)).and(
					event.reply().withContent("User was given the second warning role")
				);
			} else {
				return member.addRole(Snowflake.of(discordConfig.firstWarningRole)).and(
					event.reply().withContent("User was given the first warning role")
				);
			}
		}));
	}
}
