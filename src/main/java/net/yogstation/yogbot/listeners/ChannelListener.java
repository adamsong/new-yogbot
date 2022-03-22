package net.yogstation.yogbot.listeners;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.listeners.channel.AbstractChannel;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class ChannelListener {
	private final List<AbstractChannel> channelMessageHandlers;
	
	public ChannelListener(List<AbstractChannel> channelMessageHandlers, GatewayDiscordClient client) {
		this.channelMessageHandlers = channelMessageHandlers;
		
		client.on(MessageCreateEvent.class, this::handle).subscribe();
	}
	
	public Mono<?> handle(MessageCreateEvent event) {
		return Flux.fromIterable(channelMessageHandlers)
			.filter(channelMessageHandler -> event.getMessage().getChannelId().equals(channelMessageHandler.getChannel()))
			.next()
			.flatMap(command -> command.handle(event));
	}
}
