package net.yogstation.yogbot.interactions;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent;
import discord4j.core.event.domain.interaction.UserInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.MessageComponent;
import discord4j.core.object.component.TextInput;
import discord4j.discordjson.json.ComponentData;
import net.yogstation.yogbot.bans.BanManager;
import net.yogstation.yogbot.permissions.PermissionsManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UnbanCommand implements IUserCommand, IModalSubmitHandler {
	private final PermissionsManager permissions;
	private final BanManager banManager;
	
	public UnbanCommand(PermissionsManager permissions, BanManager banManager) {
		this.permissions = permissions;
		this.banManager = banManager;
	}
	
	@Override
	public String getName() {
		return "Unban";
	}

	@Override
	public Mono<?> handle(UserInteractionEvent event) {
		if(permissions.hasPermission(event.getInteraction().getMember().orElse(null), "ban"))
			return event.reply().withEphemeral(true).withContent("You do not have permission to run that command");


		return event.presentModal()
			.withCustomId(String.format("%s-%s", getIdPrefix(), event.getTargetId().asString()))
			.withTitle("Unban Menu")
			.withComponents(ActionRow.of(
				TextInput.paragraph("reason", "Ban Reason")
			)
		);
	}


	@Override
	public String getIdPrefix() {
		return "unban";
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
						if (data.value().isAbsent()) return event.reply().withContent("Please specify a ban reason");
						reason = data.value().get();
					}
				}
			}
		}
		String finalReason = reason;
		return event.getInteraction().getGuild().flatMap(guild ->
			guild.getMemberById(toBan)).flatMap(member -> banManager.unban(member, finalReason, event.getInteraction().getUser().getUsername()).and(
				event.reply().withEphemeral(true).withContent("Ban issued successfully")));
	}
}
