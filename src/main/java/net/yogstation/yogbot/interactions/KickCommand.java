package net.yogstation.yogbot.interactions;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent;
import discord4j.core.event.domain.interaction.UserInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.MessageComponent;
import discord4j.core.object.component.TextInput;
import discord4j.discordjson.json.ComponentData;
import net.yogstation.yogbot.Yogbot;
import reactor.core.publisher.Mono;


public class KickCommand implements IInteractionHandler<UserInteractionEvent>, IModalSubmitHandler {
	@Override
	public String getName() {
		return "Kick";
	}

	@Override
	public Mono<?> handle(UserInteractionEvent event) {
		if(event.getInteraction().getGuildId().isEmpty())
			return event.reply().withEphemeral(true).withContent("Must be used in a guild");
		if(!Yogbot.permissions.hasPermission(event.getInteraction().getMember().orElse(null), "kick"))
			return event.reply().withEphemeral(true).withContent("You do not have permission to run that command");
		if(event.getTargetId().equals(event.getInteraction().getUser().getId()))
			return event.reply().withEphemeral(true).withContent("You cannot kick yourself");
		return event.getTargetUser().flatMap(user -> user.asMember(event.getInteraction().getGuildId().get()).flatMap(member -> {
			if(Yogbot.permissions.hasPermission(member, "kick"))
				return event.reply().withEphemeral(true).withContent("Cannot kick staff");
			return event.presentModal()
				.withCustomId(String.format("%s-%s", getIdPrefix(), event.getTargetId().asString()))
				.withTitle("Kick Menu")
				.withComponents(ActionRow.of(
						TextInput.paragraph("reason", "Kick Reason")
					)
				);
		}));
	}


	@Override
	public String getIdPrefix() {
		return "kick";
	}

	@Override
	public Mono<?> handle(ModalSubmitInteractionEvent event) {
		Snowflake toBan = Snowflake.of(event.getCustomId().split("-")[1]);
		String reason = "";

		for(MessageComponent component : event.getComponents()) {
			if(component.getType() == MessageComponent.Type.ACTION_ROW) {
				if(component.getData().components().isAbsent()) continue;
				for(ComponentData data : component.getData().components().get()) {
					if(data.customId().isAbsent()) continue;
					if ("reason".equals(data.customId().get())) {
						if (data.value().isAbsent())
							return event.reply().withContent("Please specify a ban reason");
						reason = data.value().get();
					}
				}
			}
		}
		String finalReason = reason;
		return event.getInteraction().getGuild().flatMap(guild ->
			guild.getMemberById(toBan)).flatMap(member -> member.kick(
				String.format("Kicked by %s for %s", event.getInteraction().getUser().getUsername(), finalReason)
		));
	}
}
