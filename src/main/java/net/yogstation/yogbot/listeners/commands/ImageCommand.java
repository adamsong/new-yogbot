package net.yogstation.yogbot.listeners.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.spec.EmbedCreateSpec;
import net.yogstation.yogbot.config.DiscordConfig;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;

public abstract class ImageCommand extends TextCommand {

	protected final Random random;
	
	public ImageCommand(DiscordConfig discordConfig, Random random) {
		super(discordConfig);
		this.random = random;
	}
	
	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		List<String> urls = getImages();
		String url = urls.get(random.nextInt(urls.size()));
		EmbedCreateSpec embed = EmbedCreateSpec.builder()
			.color(discord4j.rest.util.Color.of(random.nextInt(0xFFFFFF)))
			.title(getTitle())
			.image(url)
			.footer(url, "")
			.build();
		return reply(event, embed);
	}

	protected abstract List<String> getImages();
	protected abstract String getTitle();
	
	@Override
	public boolean isHidden() {
		return true;
	}
}
