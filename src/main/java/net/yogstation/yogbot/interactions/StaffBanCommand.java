package net.yogstation.yogbot.interactions;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent;
import discord4j.core.event.domain.interaction.UserInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.MessageComponent;
import discord4j.core.object.component.TextInput;
import discord4j.core.object.entity.Member;
import discord4j.discordjson.json.ComponentData;
import net.yogstation.yogbot.Yogbot;
import reactor.core.publisher.Mono;

import java.util.Set;


public class StaffBanCommand implements IInteractionHandler<UserInteractionEvent> {
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
		if(!Yogbot.permissions.hasPermission(event.getInteraction().getMember().orElse(null), "staffban"))
			return event.reply().withEphemeral(true).withContent("You do not have permission to run that command");
		
		return event.getTargetUser().flatMap(user -> user.asMember(event.getInteraction().getGuildId().get()).flatMap(member -> {
			Set<Snowflake> roles = member.getRoleIds();
			if(roles.contains(Snowflake.of(Yogbot.config.discordConfig.secondWarningRole))) {
				return member.addRole(Snowflake.of(Yogbot.config.discordConfig.staffPublicBanRole)).and(
					event.reply().withContent("User was banned from staff public")
				);
			} else if(roles.contains(Snowflake.of(Yogbot.config.discordConfig.firstWarningRole))) {
				return member.addRole(Snowflake.of(Yogbot.config.discordConfig.secondWarningRole)).and(
					event.reply().withContent("User was given the second warning role")
				);
			} else {
				return member.addRole(Snowflake.of(Yogbot.config.discordConfig.firstWarningRole)).and(
					event.reply().withContent("User was given the first warning role")
				);
			}
		}));
	}
}
