package net.yogstation.yogbot.listeners;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import net.yogstation.yogbot.config.DiscordConfig;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class MessageListener {
	private static final Pattern prRegex = Pattern.compile("\\[#([0-9]+)]");
	private final DiscordConfig discordConfig;
	
	public MessageListener(GatewayDiscordClient client, DiscordConfig discordConfig) {
		this.discordConfig = discordConfig;
		client.on(MessageCreateEvent.class, this::handle).subscribe();
	}
	
	protected Mono<?> reply(MessageCreateEvent event, String format, Object... args) {
		return reply(event, String.format(format, args));
	}
	
	protected Mono<?> reply(MessageCreateEvent event, String message) {
		return event.getMessage()
			.getChannel()
			.flatMap(channel -> channel.createMessage(
				MessageCreateSpec.builder().messageReference(event.getMessage().getId()).content(message).build()));
	}
	
	protected Mono<?> reply(MessageCreateEvent event, EmbedCreateSpec embed) {
		return event.getMessage()
			.getChannel()
			.flatMap(channel -> channel.createMessage(MessageCreateSpec.builder()
				                                          .messageReference(event.getMessage().getId())
				                                          .content("")
				                                          .addEmbed(embed)
				                                          .build()));
	}
	
	public Mono<?> handle(MessageCreateEvent event) {
		Message message = event.getMessage();
		String messageContent = message.getContent();
		String lowerMessage = messageContent.toLowerCase(Locale.ROOT);
		Mono<MessageChannel> channelMono = message.getChannel();
		
		if (message.getAuthor().isEmpty() || message.getGuildId().isEmpty()) return Mono.empty();
		if (message.getAuthor().get().isBot()) return Mono.empty();
		
		if (lowerMessage.contains("snail") && lowerMessage.contains("when"))
			return channelMono.flatMap(channel -> channel.createMessage("When you code it"));
		
		if (message.getRoleMentionIds().contains(Snowflake.of(discordConfig.jesterRole))) {
			return message.getAuthorAsMember()
				.filter(member -> !member.getRoleIds().contains(Snowflake.of(discordConfig.jesterRole)))
				.flatMap(member -> member.addRole(Snowflake.of(discordConfig.jesterRole))
					.and(reply(event,
						"It appears you have, for the first time, engaged in the dastardly action to ping Jester! For this crime you have been assigned the role of Jester. Congratulations on your promotion!")));
		}
		
		Matcher prMatcher = prRegex.matcher(event.getMessage().getContent());
		if (prMatcher.matches()) return reply(event, "https://github.com/yogstation13/Yogstation/issues/%s", prMatcher.group(1));
		
		return Mono.empty();
	}
}
